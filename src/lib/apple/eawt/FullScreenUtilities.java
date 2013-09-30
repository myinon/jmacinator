/*
 * Java/Mac OS X Integration - Integrate Java programs with Mac OS X
 * Copyright (C) 2012  Yinon Michaeli
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact by e-mail if you discover any bugs or if you have a suggestion
 * to myinon2005@hotmail.com
 */

package lib.apple.eawt;

import java.awt.Window;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lib.apple.eawt.AppEvent.FullScreenEvent;
import lib.apple.eawt.Application.AppInvocationHandler;

/**
 * Utility class perform animated full screen actions to top-level {@link Window}s.
 *
 * This class manages the relationship between {@link Windows}s and the {@link FullScreenListener}s
 * attached to them. It's design is similar to the Java SE 6u10 {@code com.sun.awt.AWTUtilities}
 * class which adds additional functionality to AWT Windows, without adding new API to the
 * {@link java.awt.Window} class.
 *
 * Full screen operations can only be performed on top-level {@link Window}s that are also {@link RootPaneContainer}s.
 *
 * @see FullScreenAdapter
 * @see GestureUtilities
 *
 * @since Java for Mac OS X 10.7 Update 1
 */
public final class FullScreenUtilities {
	private static final ConcurrentMap<FullScreenListener, Object> FULLSCREEN_MAP = new ConcurrentHashMap<>();
	
	private static Class<?> _fullScreenListenerClass = null;
	private static Method _setWindowCanFullScreen = null;
	private static Method _addFullScreenListenerTo = null;
	private static Method _removeFullScreenListenerFrom = null;
	private static ClassLoader loader = null;
	
	static {
		try {
			Path java = Paths.get("/System/Library/Java");
			if (Files.exists(java, LinkOption.NOFOLLOW_LINKS)) {
				ClassLoader sys_loader = ClassLoader.getSystemClassLoader();
				if (URLClassLoader.class.isAssignableFrom(sys_loader.getClass())) {
					Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
					doPrivileged(addUrl);
					addUrl.invoke(sys_loader, java.toUri().toURL());
				}
			}
			
			Class<?> utilClass = Class.forName("com.apple.eawt.FullScreenUtilities");
			loader = utilClass.getClassLoader();
			_fullScreenListenerClass = Class.forName("com.apple.eawt.FullScreenListener");
			_setWindowCanFullScreen = utilClass.getMethod("setWindowCanFullScreen", Window.class, Boolean.TYPE);
			_addFullScreenListenerTo = utilClass.getMethod("addFullScreenListenerTo", Window.class, _fullScreenListenerClass);
			_removeFullScreenListenerFrom = utilClass.getMethod("removeFullScreenListenerFrom", Window.class, _fullScreenListenerClass);
		} catch (ClassNotFoundException e) {
		} catch (IllegalAccessException | NoSuchMethodException
		| InvocationTargetException | MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Deprecated
	private FullScreenUtilities() {
		throw new InternalError();
	}
	
	/**
	 * Marks a {@link Window} as able to animate into or out of full screen mode.
	 *
	 * Only top-level {@link Window}s which are {@link RootPaneContainer}s are able to be animated into and out of full screen mode.
	 * The {@link Window} must be marked as full screen-able before the native peer is created with {@link Component#addNotify()}.
	 *
	 * @param window
	 * @param canFullScreen
	 * @throws IllegalArgumentException if window is not a {@link RootPaneContainer}
	 */
	public static void setWindowCanFullScreen(final Window window, final boolean canFullScreen) {
		if (_setWindowCanFullScreen != null) {
			call(_setWindowCanFullScreen, window, canFullScreen);
		}
	}
	
	/**
	 * Attaches a {@link FullScreenListener} to the specified top-level {@link Window}.
	 * @param window to attach the {@link FullScreenListener} to
	 * @param listener to be notified when a full screen event occurs
	 * @throws IllegalArgumentException if window is not a {@link RootPaneContainer}
	 */
	public static void addFullScreenListenerTo(final Window window, final FullScreenListener listener) {
		if (_addFullScreenListenerTo != null) {
			try {
				Object handler = FULLSCREEN_MAP.get(listener);
				if (handler == null) {
					Class<?> proxy = Proxy.getProxyClass(loader, _fullScreenListenerClass);
					handler = proxy.getConstructor(InvocationHandler.class)
						.newInstance(new AppInvocationHandler<FullScreenListener, FullScreenEvent>(listener, FullScreenEvent.class));
					Object prev = FULLSCREEN_MAP.putIfAbsent(listener, handler);
					if (prev != null) {
						handler = null;
					}
				}
				call(_addFullScreenListenerTo, window, handler);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
			| NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Removes a {@link FullScreenListener} from the specified top-level {@link Window}.
	 * @param window to remove the {@link FullScreenListener} from
	 * @param listener to be removed
	 * @throws IllegalArgumentException if window is not a {@link RootPaneContainer}
	 */
	public static void removeFullScreenListenerFrom(final Window window, final FullScreenListener listener) {
		if (_removeFullScreenListenerFrom != null) {
			Object handler = FULLSCREEN_MAP.remove(listener);
			
			if (handler != null)
				call(_removeFullScreenListenerFrom, window, handler);
		}
	}
	
	private static void doPrivileged(final Method m) {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			@Override
			public Void run() {
				m.setAccessible(true);
				return null;
			}
		});
	}
	
	// Call the actual com.apple.eawt.FullScreenUtilities method.
	private static Object call(Method method, Object... args) {
		if (method == null) return null;
		try {
			return method.invoke(null, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}