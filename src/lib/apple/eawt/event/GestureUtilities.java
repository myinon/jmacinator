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

package lib.apple.eawt.event;

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

import javax.swing.JComponent;

/**
 * Registration utility class to add {@link GestureListener}s to Swing components.
 *
 * This class manages the relationship between {@link JComponent}s and the {@link GestureListener}s
 * attached to them. It's design is similar to the Java SE 6u10 {@code com.sun.awt.AWTUtilities}
 * class which adds additional functionality to AWT Windows, without adding new API to the
 * {@link java.awt.Window} class.
 *
 * To add a {@link GestureListener} to a top-level Swing window, use the {@link JRootPane} of the
 * top-level window type.
 *
 * @see GestureAdapter
 * @see JFrame#getRootPane()
 *
 * @since Java for Mac OS X 10.5 Update 7, Java for Mac OS X 10.6 Update 2
 */
public final class GestureUtilities {
	private static final ConcurrentMap<GesturePhaseListener, Object> PHASE_MAP = new ConcurrentHashMap<>();
	private static final ConcurrentMap<MagnificationListener, Object> MAGNIFICATION_MAP = new ConcurrentHashMap<>();
	private static final ConcurrentMap<RotationListener, Object> ROTATION_MAP = new ConcurrentHashMap<>();
	private static final ConcurrentMap<SwipeListener, Object> SWIPE_MAP = new ConcurrentHashMap<>();
	
	private static Class<?> _gestureListenerClass = null;
	private static Class<?> _gesturePhaseListenerClass = null;
	private static Class<?> _magnificationListenerClass = null;
	private static Class<?> _rotationListenerClass = null;
	private static Class<?> _swipeListenerClass = null;
	private static Method _addGestureListenerTo = null;
	private static Method _removeGestureListenerFrom = null;
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
			
			Class<?> utilClass = Class.forName("com.apple.eawt.event.GestureUtilities");
			loader = utilClass.getClassLoader();
			_gestureListenerClass = Class.forName("com.apple.eawt.event.GestureListener");
			_gesturePhaseListenerClass = Class.forName("com.apple.eawt.event.GesturePhaseListener");
			_magnificationListenerClass = Class.forName("com.apple.eawt.event.MagnificationListener");
			_rotationListenerClass = Class.forName("com.apple.eawt.event.RotationListener");
			_swipeListenerClass = Class.forName("com.apple.eawt.event.SwipeListener");
			_addGestureListenerTo = utilClass.getMethod("addGestureListenerTo", JComponent.class, _gestureListenerClass);
			_removeGestureListenerFrom = utilClass.getMethod("removeGestureListenerFrom", JComponent.class, _gestureListenerClass);
		} catch (ClassNotFoundException e) {
		} catch (IllegalAccessException | NoSuchMethodException
		| InvocationTargetException | MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Deprecated
	private GestureUtilities() {
		throw new InternalError();
	}
	
	/**
	 * Attaches a {@link GestureListener} to the specified {@link JComponent}.
	 * @param component to attach the {@link GestureListener} to
	 * @param listener to be notified when a gesture occurs
	 */
	public static void addGestureListenerTo(final JComponent component, final GestureListener listener) {
		if (_addGestureListenerTo != null) {
			Object handler = null;
			if (listener instanceof GesturePhaseListener) {
				GesturePhaseListener gpl = (GesturePhaseListener) listener;
				try {
					handler = PHASE_MAP.get(gpl);
					if (handler == null) {
						Class<?> proxy = Proxy.getProxyClass(loader, _gesturePhaseListenerClass);
						handler = proxy.getConstructor(InvocationHandler.class)
							.newInstance(new GestureInvocationHandler<GesturePhaseListener, GesturePhaseEvent>(gpl, GesturePhaseEvent.class));
						Object prev = PHASE_MAP.putIfAbsent(gpl, handler);
						if (prev != null) {
							handler = null;
						}
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			if (listener instanceof MagnificationListener) {
				MagnificationListener ml = (MagnificationListener) listener;
				try {
					handler = MAGNIFICATION_MAP.get(ml);
					if (handler == null) {
						Class<?> proxy = Proxy.getProxyClass(loader, _magnificationListenerClass);
						handler = proxy.getConstructor(InvocationHandler.class)
							.newInstance(new GestureInvocationHandler<MagnificationListener, MagnificationEvent>(ml, MagnificationEvent.class));
						Object prev = MAGNIFICATION_MAP.putIfAbsent(ml, handler);
						if (prev != null) {
							handler = null;
						}
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			if (listener instanceof RotationListener) {
				RotationListener rl = (RotationListener) listener;
				try {
					handler = ROTATION_MAP.get(rl);
					if (handler == null) {
						Class<?> proxy = Proxy.getProxyClass(loader, _rotationListenerClass);
						handler = proxy.getConstructor(InvocationHandler.class)
							.newInstance(new GestureInvocationHandler<RotationListener, RotationEvent>(rl, RotationEvent.class));
						Object prev = ROTATION_MAP.putIfAbsent(rl, handler);
						if (prev != null) {
							handler = null;
						}
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			if (listener instanceof SwipeListener) {
				SwipeListener sl = (SwipeListener) listener;
				try {
					handler = SWIPE_MAP.get(sl);
					if (handler == null) {
						Class<?> proxy = Proxy.getProxyClass(loader, _swipeListenerClass);
						handler = proxy.getConstructor(InvocationHandler.class)
							.newInstance(new GestureInvocationHandler<SwipeListener, SwipeEvent>(sl, SwipeEvent.class));
						Object prev = SWIPE_MAP.putIfAbsent(sl, handler);
						if (prev != null) {
							handler = null;
						}
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			
			if (handler != null) {
				call(_addGestureListenerTo, component, handler);
			}
		}
	}
	
	/**
	 * Removes a {@link GestureListener} from the specified {@link JComponent}
	 * @param component to remove the {@link GestureListener} from
	 * @param listener to be removed
	 */
	public static void removeGestureListenerFrom(final JComponent component, final GestureListener listener) {
		if (_removeGestureListenerFrom != null) {
			Object handler = null;
			if (listener instanceof GesturePhaseListener) {
				handler = PHASE_MAP.remove((GesturePhaseListener) listener);
			}
			if (listener instanceof MagnificationListener) {
				handler = MAGNIFICATION_MAP.remove((MagnificationListener) listener);
			}
			if (listener instanceof RotationListener) {
				handler = ROTATION_MAP.remove((RotationListener) listener);
			}
			if (listener instanceof SwipeListener) {
				handler = SWIPE_MAP.remove((SwipeListener) listener);
			}
			
			if (handler != null)
				call(_removeGestureListenerFrom, component, handler);
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
	
	// Call the actual com.apple.eawt.event.GestureUtilities method.
	private static Object call(Method method, Object... args) {
		if (method == null) return null;
		try {
			return method.invoke(null, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	// Wrapper around the actual XXListener objects in com.apple.eawt.event.
	// Called methods will be forwarded to the user Handler object.
	//
	// H represents the Handler class and E is the Event class that is passed to the Handler's methods.
	static class GestureInvocationHandler<H, E> implements InvocationHandler {
		private H _handler;
		private Class<E> _eventClass;
		
		GestureInvocationHandler(H handler, Class<E> event) {
			this._handler = handler;
			this._eventClass = event;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (this._handler == null) return null;
			
			E _event = createEventObject(args[0], this._eventClass);
			Class<?>[] types = new Class<?>[args.length];
			args[0] = _event;
			types[0] = this._eventClass;
			for (int i = 1; i < args.length; i++) {
				types[i] = args[i].getClass();
				if (types[i].getName().contains("$Proxy"))
					types[i] = types[i].getInterfaces()[0]; // All proxy classes used implement one interface
			}
			try {
				Method m = this._handler.getClass().getMethod(method.getName(), types);
				doPrivileged(m);
				return m.invoke(this._handler, args);
			} catch (NoSuchMethodException e) {
				// Might not get here?
				if (method.getName().equals("equals") && args.length == 1) {
					return Boolean.valueOf(proxy == args[0]);
				}
				return null;
			} catch (NullPointerException e) {
				return null;
			}
		}
	}
	
	// Wrapper around the actual XXEvent object in com.apple.eawt.event.
	private static <E> E createEventObject(final Object appleEventObj, Class<E> eventClass) {
		Class<?> proxy = Proxy.getProxyClass(GestureUtilities.class.getClassLoader(), eventClass);
		try {
			Object event = proxy.getConstructor(InvocationHandler.class)
				.newInstance(new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return appleEventObj.getClass().getMethod(method.getName(), method.getParameterTypes())
							.invoke(appleEventObj, args);
					}
				});
			return eventClass.cast(event);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
		| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}