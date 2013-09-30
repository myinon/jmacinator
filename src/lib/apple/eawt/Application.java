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

import java.awt.Image;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Window;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
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

import javax.swing.JMenuBar;

import lib.apple.eawt.AppEvent.AboutEvent;
import lib.apple.eawt.AppEvent.AppForegroundEvent;
import lib.apple.eawt.AppEvent.AppHiddenEvent;
import lib.apple.eawt.AppEvent.AppReOpenedEvent;
import lib.apple.eawt.AppEvent.OpenFilesEvent;
import lib.apple.eawt.AppEvent.OpenURIEvent;
import lib.apple.eawt.AppEvent.PreferencesEvent;
import lib.apple.eawt.AppEvent.PrintFilesEvent;
import lib.apple.eawt.AppEvent.QuitEvent;
import lib.apple.eawt.AppEvent.ScreenSleepEvent;
import lib.apple.eawt.AppEvent.SystemSleepEvent;
import lib.apple.eawt.AppEvent.UserSessionEvent;

/**
 * The <code>Application</code> class allows you to integrate your Java application with the native Mac OS X environment.
 * You can provide your Mac OS X users a greatly enhanced experience by implementing a few basic handlers for standard system events.
 *
 * For example:
 * <ul>
 * <li>Open an about dialog when a user chooses About from the application menu.</li>
 * <li>Open a preferences window when the users chooses Preferences from the application menu.</li>
 * <li>Create a new document when the user clicks on your Dock icon, and no windows are open.</li>
 * <li>Open a document that the user double-clicked on in the Finder.</li>
 * <li>Open a custom URL scheme when a user clicks on link in a web browser.</li>
 * <li>Reconnect to network services after the system has awoke from sleep.</li>
 * <li>Cleanly shutdown your application when the user chooses Quit from the application menu, Dock icon, or types Command-Q.</li>
 * <li>Cancel shutdown/logout if the user has unsaved changes in your application.</li>
 * </ul>
 *
 * @since 1.4
 */
public final class Application {
	private static final Class<?>[] NULL_TYPES = new Class<?>[0];
	private static final Object[] NULL_ARGS = new Object[0];
	
	private static final ConcurrentMap<AppForegroundListener, Object> FOREGROUND_MAP = new ConcurrentHashMap<>();
	private static final ConcurrentMap<AppHiddenListener, Object> HIDDEN_MAP = new ConcurrentHashMap<>();
	private static final ConcurrentMap<AppReOpenedListener, Object> REOPENED_MAP = new ConcurrentHashMap<>();
	private static final ConcurrentMap<ScreenSleepListener, Object> SCREEN_MAP = new ConcurrentHashMap<>();
	private static final ConcurrentMap<SystemSleepListener, Object> SYSTEM_MAP = new ConcurrentHashMap<>();
	private static final ConcurrentMap<UserSessionListener, Object> USER_MAP = new ConcurrentHashMap<>();
	
	@SuppressWarnings("deprecation")
	private static final ConcurrentMap<ApplicationListener, Object> APPLICATION_MAP = new ConcurrentHashMap<>();
	
	private static Object application = null;
	private static Class<?> _appEventListenerClass = null;
	private static Class<?> _appForegroundListenerClass = null;
	private static Class<?> _appHiddenListenerClass = null;
	private static Class<?> _appReOpenedListenerClass = null;
	private static Class<?> _screenSleepListenerClass = null;
	private static Class<?> _systemSleepListenerClass = null;
	private static Class<?> _userSessionListenerClass = null;
	private static Class<?> _aboutHandlerClass = null;
	private static Class<?> _preferencesHandlerClass = null;
	private static Class<?> _openFilesHandlerClass = null;
	private static Class<?> _printFilesHandlerClass = null;
	private static Class<?> _openURIHandlerClass = null;
	private static Class<?> _quitHandlerClass = null;
	private static Class<?> _quitStrategyClass = null;
	private static Class<?> _quitResponseClass = null;
	private static Class<?> _applicationListenerClass = null;
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
			
			Class<?> appClass = Class.forName("com.apple.eawt.Application");
			loader = appClass.getClassLoader();
			application = appClass.getMethod("getApplication").invoke(null);
			_appEventListenerClass = Class.forName("com.apple.eawt.AppEventListener");
			_appForegroundListenerClass = Class.forName("com.apple.eawt.AppForegroundListener");
			_appHiddenListenerClass = Class.forName("com.apple.eawt.AppHiddenListener");
			_appReOpenedListenerClass = Class.forName("com.apple.eawt.AppReOpenedListener");
			_screenSleepListenerClass = Class.forName("com.apple.eawt.ScreenSleepListener");
			_systemSleepListenerClass = Class.forName("com.apple.eawt.SystemSleepListener");
			_userSessionListenerClass = Class.forName("com.apple.eawt.UserSessionListener");
			_aboutHandlerClass = Class.forName("com.apple.eawt.AboutHandler");
			_preferencesHandlerClass = Class.forName("com.apple.eawt.PreferencesHandler");
			_openFilesHandlerClass = Class.forName("com.apple.eawt.OpenFilesHandler");
			_printFilesHandlerClass = Class.forName("com.apple.eawt.PrintFilesHandler");
			_openURIHandlerClass = Class.forName("com.apple.eawt.OpenURIHandler");
			_quitHandlerClass = Class.forName("com.apple.eawt.QuitHandler");
			_quitStrategyClass = Class.forName("com.apple.eawt.QuitStrategy");
			_quitResponseClass = Class.forName("com.apple.eawt.QuitResponse");
			_applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
		} catch (ClassNotFoundException e) {
			application = null;
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Creates an Application instance. Should not be used.
	 * @deprecated
	 *
	 * @since 1.4
	 */
	@Deprecated
	private Application() {
		throw new InternalError();
	}
	
	/**
	 * Adds sub-types of {@link AppEventListener} to listen for notifications from the native Mac OS X system.
	 *
	 * @see AppForegroundListener
	 * @see AppHiddenListener
	 * @see AppReOpenedListener
	 * @see ScreenSleepListener
	 * @see SystemSleepListener
	 * @see UserSessionListener
	 *
	 * @param listener
	 * @since Java for Mac OS X 10.6 Update 3
	 * @since Java for Mac OS X 10.5 Update 8
	 */
	public void addAppEventListener(final AppEventListener listener) {
		if (application != null) {
			Object handler = null;
			if (listener instanceof AppForegroundListener) {
				AppForegroundListener apl = (AppForegroundListener) listener;
				try {
					handler = FOREGROUND_MAP.get(apl);
					if (handler == null) {
						Class<?> proxy = Proxy.getProxyClass(loader, _appForegroundListenerClass);
						handler = proxy.getConstructor(InvocationHandler.class)
							.newInstance(new AppInvocationHandler<AppForegroundListener, AppForegroundEvent>(apl, AppForegroundEvent.class));
						Object prev = FOREGROUND_MAP.putIfAbsent(apl, handler);
						if (prev != null) {
							handler = null;
						}
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			if (listener instanceof AppHiddenListener) {
				AppHiddenListener ahl = (AppHiddenListener) listener;
				try {
					handler = HIDDEN_MAP.get(ahl);
					if (handler == null) {
						Class<?> proxy = Proxy.getProxyClass(loader, _appHiddenListenerClass);
						handler = proxy.getConstructor(InvocationHandler.class)
							.newInstance(new AppInvocationHandler<AppHiddenListener, AppHiddenEvent>(ahl, AppHiddenEvent.class));
						Object prev = HIDDEN_MAP.putIfAbsent(ahl, handler);
						if (prev != null) {
							handler = null;
						}
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			if (listener instanceof AppReOpenedListener) {
				AppReOpenedListener arol = (AppReOpenedListener) listener;
				try {
					handler = REOPENED_MAP.get(arol);
					if (handler == null) {
						Class<?> proxy = Proxy.getProxyClass(loader, _appReOpenedListenerClass);
						handler = proxy.getConstructor(InvocationHandler.class)
							.newInstance(new AppInvocationHandler<AppReOpenedListener, AppReOpenedEvent>(arol, AppReOpenedEvent.class));
						Object prev = REOPENED_MAP.putIfAbsent(arol, handler);
						if (prev != null) {
							handler = null;
						}
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			if (listener instanceof ScreenSleepListener) {
				ScreenSleepListener ssl = (ScreenSleepListener) listener;
				try {
					handler = SCREEN_MAP.get(ssl);
					if (handler == null) {
						Class<?> proxy = Proxy.getProxyClass(loader, _screenSleepListenerClass);
						handler = proxy.getConstructor(InvocationHandler.class)
							.newInstance(new AppInvocationHandler<ScreenSleepListener, ScreenSleepEvent>(ssl, ScreenSleepEvent.class));
						Object prev = SCREEN_MAP.putIfAbsent(ssl, handler);
						if (prev != null) {
							handler = null;
						}
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			if (listener instanceof SystemSleepListener) {
				SystemSleepListener ssl = (SystemSleepListener) listener;
				try {
					handler = SYSTEM_MAP.get(ssl);
					if (handler == null) {
						Class<?> proxy = Proxy.getProxyClass(loader, _systemSleepListenerClass);
						handler = proxy.getConstructor(InvocationHandler.class)
							.newInstance(new AppInvocationHandler<SystemSleepListener, SystemSleepEvent>(ssl, SystemSleepEvent.class));
						Object prev = SYSTEM_MAP.putIfAbsent(ssl, handler);
						if (prev != null) {
							handler = null;
						}
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			if (listener instanceof UserSessionListener) {
				UserSessionListener usl = (UserSessionListener) listener;
				try {
					handler = USER_MAP.get(usl);
					if (handler == null) {
						Class<?> proxy = Proxy.getProxyClass(loader, _userSessionListenerClass);
						handler = proxy.getConstructor(InvocationHandler.class)
							.newInstance(new AppInvocationHandler<UserSessionListener, UserSessionEvent>(usl, UserSessionEvent.class));
						Object prev = USER_MAP.putIfAbsent(usl, handler);
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
				call(application, "addAppEventListener", new Class<?>[]{ _appEventListenerClass }, new Object[]{ handler });
			}
		}
	}
	
	/**
	 * Removes sub-types of {@link AppEventListener} from listening for notifications from the native Mac OS X system.
	 *
	 * @see AppForegroundListener
	 * @see AppHiddenListener
	 * @see AppReOpenedListener
	 * @see ScreenSleepListener
	 * @see SystemSleepListener
	 * @see UserSessionListener
	 *
	 * @param listener
	 * @since Java for Mac OS X 10.6 Update 3
	 * @since Java for Mac OS X 10.5 Update 8
	 */
	public void removeAppEventListener(final AppEventListener listener) {
		if (application != null) {
			Object handler = null;
			if (listener instanceof AppForegroundListener) {
				handler = FOREGROUND_MAP.remove((AppForegroundListener) listener);
			}
			if (listener instanceof AppHiddenListener) {
				handler = HIDDEN_MAP.remove((AppHiddenListener) listener);
			}
			if (listener instanceof AppReOpenedListener) {
				handler = REOPENED_MAP.remove((AppReOpenedListener) listener);
			}
			if (listener instanceof ScreenSleepListener) {
				handler = SCREEN_MAP.remove((ScreenSleepListener) listener);
			}
			if (listener instanceof SystemSleepListener) {
				handler = SYSTEM_MAP.get((SystemSleepListener) listener);
			}
			if (listener instanceof UserSessionListener) {
				handler = USER_MAP.get((UserSessionListener) listener);
			}
			
			if (handler != null)
				call(application, "removeAppEventListener", new Class<?>[]{ _appEventListenerClass }, new Object[]{ handler });
		}
	}
	
	/**
	 * Installs a handler to show a custom About window for your application.
	 *
	 * Setting the {@link AboutHandler} to <code>null</code> reverts it to the default Cocoa About window.
	 *
	 * @param aboutHandler the handler to respond to the {@link AboutHandler#handleAbout()} message
	 * @since Java for Mac OS X 10.6 Update 3
	 * @since Java for Mac OS X 10.5 Update 8
	 */
	public static void setAboutHandler(final AboutHandler aboutHandler) {
		if (application != null) {
			Class<?> proxy = Proxy.getProxyClass(loader, _aboutHandlerClass);
			try {
				Object handler = proxy.getConstructor(InvocationHandler.class)
					.newInstance(new AppInvocationHandler<AboutHandler, AboutEvent>(aboutHandler, AboutEvent.class));
				call(application, "setAboutHandler", new Class<?>[]{ _aboutHandlerClass }, new Object[]{ handler });
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
			| NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Installs a handler to create the Preferences menu item in your application's app menu.
	 *
	 * Setting the {@link PreferencesHandler} to <code>null</code> will remove the Preferences item from the app menu.
	 *
	 * @param preferencesHandler
	 * @since Java for Mac OS X 10.6 Update 3
	 * @since Java for Mac OS X 10.5 Update 8
	 */
	public static void setPreferencesHandler(final PreferencesHandler preferencesHandler) {
		if (application != null) {
			Class<?> proxy = Proxy.getProxyClass(loader, _preferencesHandlerClass);
			try {
				Object handler = proxy.getConstructor(InvocationHandler.class)
					.newInstance(new AppInvocationHandler<PreferencesHandler, PreferencesEvent>(preferencesHandler, PreferencesEvent.class));
				call(application, "setPreferencesHandler", new Class<?>[]{ _preferencesHandlerClass }, new Object[]{ handler });
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
			| NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Installs the handler which is notified when the application is asked to open a list of files.
	 * The {@link OpenFilesHandler#openFiles(AppEvent.OpenFilesEvent)} notifications are only sent if the Java app is a bundled application, with a <code>CFBundleDocumentTypes</code> array present in it's Info.plist.
	 * See the <a href="http://developer.apple.com/mac/library/documentation/General/Reference/InfoPlistKeyReference">Info.plist Key Reference</a> for more information about adding a <code>CFBundleDocumentTypes</code> key to your app's Info.plist.
	 *
	 * @param openFileHandler
	 * @since Java for Mac OS X 10.6 Update 3
	 * @since Java for Mac OS X 10.5 Update 8
	 */
	public static void setOpenFileHandler(final OpenFilesHandler openFileHandler) {
		if (application != null) {
			Class<?> proxy = Proxy.getProxyClass(loader, _openFilesHandlerClass);
			try {
				Object handler = proxy.getConstructor(InvocationHandler.class)
					.newInstance(new AppInvocationHandler<OpenFilesHandler, OpenFilesEvent>(openFileHandler, OpenFilesEvent.class));
				call(application, "setOpenFileHandler", new Class<?>[]{ _openFilesHandlerClass }, new Object[]{ handler });
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
			| NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Installs the handler which is notified when the application is asked to print a list of files.
	 * The {@link PrintFilesHandler#printFiles(AppEvent.PrintFilesEvent)} notifications are only sent if the Java app is a bundled application, with a <code>CFBundleDocumentTypes</code> array present in it's Info.plist.
	 * See the <a href="http://developer.apple.com/mac/library/documentation/General/Reference/InfoPlistKeyReference">Info.plist Key Reference</a> for more information about adding a <code>CFBundleDocumentTypes</code> key to your app's Info.plist.
	 *
	 * @param printFileHandler
	 * @since Java for Mac OS X 10.6 Update 3
	 * @since Java for Mac OS X 10.5 Update 8
	 */
	public static void setPrintFileHandler(final PrintFilesHandler printFileHandler) {
		if (application != null) {
			Class<?> proxy = Proxy.getProxyClass(loader, _printFilesHandlerClass);
			try {
				Object handler = proxy.getConstructor(InvocationHandler.class)
					.newInstance(new AppInvocationHandler<PrintFilesHandler, PrintFilesEvent>(printFileHandler, PrintFilesEvent.class));
				call(application, "setPrintFileHandler", new Class<?>[]{ _printFilesHandlerClass }, new Object[]{ handler });
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
			| NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Installs the handler which is notified when the application is asked to open a URL.
	 * The {@link OpenURIHandler#openURI(AppEvent.OpenURIEvent)} notifications are only sent if the Java app is a bundled application, with a <code>CFBundleURLTypes</code> array present in it's Info.plist.
	 * See the <a href="http://developer.apple.com/mac/library/documentation/General/Reference/InfoPlistKeyReference">Info.plist Key Reference</a> for more information about adding a <code>CFBundleURLTypes</code> key to your app's Info.plist.
	 *
	 * Setting the handler to <code>null</code> causes all {@link OpenURIHandler#openURI(AppEvent.OpenURIEvent)} requests to be enqueued until another handler is set.
	 *
	 * @param openURIHandler
	 * @since Java for Mac OS X 10.6 Update 3
	 * @since Java for Mac OS X 10.5 Update 8
	 */
	public static void setOpenURIHandler(final OpenURIHandler openURIHandler) {
		if (application != null) {
			Class<?> proxy = Proxy.getProxyClass(loader, _openURIHandlerClass);
			try {
				Object handler = proxy.getConstructor(InvocationHandler.class)
					.newInstance(new AppInvocationHandler<OpenURIHandler, OpenURIEvent>(openURIHandler, OpenURIEvent.class));
				call(application, "setOpenURIHandler", new Class<?>[]{ _openURIHandlerClass }, new Object[]{ handler });
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
			| NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Installs the handler which determines if the application should quit.
	 * The handler is passed a one-shot {@link QuitResponse} which can cancel or proceed with the quit.
	 * Setting the handler to <code>null</code> causes all quit requests to directly perform the default {@link QuitStrategy}.
	 *
	 * @param quitHandler the handler that is called when the application is asked to quit
	 * @since Java for Mac OS X 10.6 Update 3
	 * @since Java for Mac OS X 10.5 Update 8
	 */
	public static void setQuitHandler(final QuitHandler quitHandler) {
		if (application != null) {
			Class<?> proxy = Proxy.getProxyClass(loader, _quitHandlerClass);
			try {
				Object handler = proxy.getConstructor(InvocationHandler.class)
					.newInstance(new AppInvocationHandler<QuitHandler, QuitEvent>(quitHandler, QuitEvent.class));
				call(application, "setQuitHandler", new Class<?>[]{ _quitHandlerClass }, new Object[]{ handler });
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
			| NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Sets the default strategy used to quit this application. The default is calling SYSTEM_EXIT_0.
	 *
	 * The quit strategy can also be set with the "apple.eawt.quitStrategy" system property.
	 *
	 * @param strategy the way this application should be shutdown
	 * @since Java for Mac OS X 10.6 Update 3
	 * @since Java for Mac OS X 10.5 Update 8
	 */
	public static void setQuitStrategy(final QuitStrategy strategy) {
		if (application != null) {
			try {
				Field e = _quitStrategyClass.getField(strategy.name());
				doPrivileged(e);
				call(application, "setQuitStrategy", new Class<?>[]{ _quitStrategyClass }, new Object[]{ e.get(null) });
			} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Enables this application to be suddenly terminated.
	 *
	 * Call this method to indicate your application's state is saved, and requires no notification to be terminated.
	 * Letting your application remain terminatable improves the user experience by avoiding re-paging in your application when it's asked to quit.
	 *
	 * <b>Note: enabling sudden termination will allow your application to be quit without notifying your QuitHandler, or running any shutdown hooks.</b>
	 * User initiated Cmd-Q, logout, restart, or shutdown requests will effectively "kill -KILL" your application.
	 *
	 * This call has no effect on Mac OS X versions prior to 10.6.
	 *
	 * @see <a href="http://developer.apple.com/mac/library/documentation/cocoa/reference/foundation/Classes/NSProcessInfo_Class">NSProcessInfo class references</a> for more information about Sudden Termination.
	 * @see Application#disableSuddenTermination()
	 *
	 * @since Java for Mac OS X 10.6 Update 3
	 * @since Java for Mac OS X 10.5 Update 8
	 */
	public static void enableSuddenTermination() {
		call("enableSuddenTermination");
	}
	
	/**
	 * Prevents this application from being suddenly terminated.
	 *
	 * Call this method to indicate that your application has unsaved state, and may not be terminated without notification.
	 *
	 * This call has no effect on Mac OS X versions prior to 10.6.
	 *
	 * @see <a href="http://developer.apple.com/mac/library/documentation/cocoa/reference/foundation/Classes/NSProcessInfo_Class">NSProcessInfo class references</a> for more information about Sudden Termination.
	 * @see Application#enableSuddenTermination()
	 *
	 * @since Java for Mac OS X 10.6 Update 3
	 * @since Java for Mac OS X 10.5 Update 8
	 */
	public static void disableSuddenTermination() {
		call("disableSuddenTermination");
	}
	
	/**
	 * Requests this application to move to the foreground.
	 *
	 * @param allWindows if all windows of this application should be moved to the foreground, or only the foremost one
	 *
	 * @since Java for Mac OS X 10.6 Update 1
	 * @since Java for Mac OS X 10.5 Update 6 - 1.6, 1.5
	 */
	public static void requestForeground(final boolean allWindows) {
		call(application, "requestActivation", new Class<?>[]{ Boolean.TYPE }, new Object[]{ Boolean.valueOf(allWindows) });
	}
	
	/**
	 * Requests user attention to this application (usually through bouncing the Dock icon). Critical
	 * requests will continue to bounce the Dock icon until the app is activated. An already active
	 * application requesting attention does nothing.
	 *
	 * @param critical if this is an important request
	 *
	 * @since Java for Mac OS X 10.6 Update 1
	 * @since Java for Mac OS X 10.5 Update 6 - 1.6, 1.5
	 */
	public static void requestUserAttention(final boolean critical) {
		call(application, "requestUserAttention", new Class<?>[]{ Boolean.TYPE }, new Object[]{ Boolean.valueOf(critical) });
	}
	
	/**
	 * Opens the native help viewer application if a Help Book has been added to the
	 * application bundler and registered in the Info.plist with CFBundleHelpBookFolder.
	 *
	 * See http://developer.apple.com/qa/qa2001/qa1022.html for more information.
	 *
	 * @since Java for Mac OS X 10.5 - 1.5
	 * @since Java for Mac OS X 10.5 Update 1 - 1.6
	 */
	public static void openHelpViewer() {
		call("openHelpViewer");
	}
	
	/**
	 * Attaches the contents of the provided PopupMenu to the application's Dock icon.
	 *
	 * @param menu the PopupMenu to attach to this application's Dock icon
	 *
	 * @since Java for Mac OS X 10.5 - 1.5
	 * @since Java for Mac OS X 10.5 Update 1 - 1.6
	 */
	public static void setDockMenu(final PopupMenu menu) {
		call(application, "setDockMenu", new Class<?>[]{ PopupMenu.class }, new Object[]{ menu });
	}
	
	/**
	 * @return the PopupMenu used to add items to this application's Dock icon
	 *
	 * @since Java for Mac OS X 10.5 - 1.5
	 * @since Java for Mac OS X 10.5 Update 1 - 1.6
	 */
	public static PopupMenu getDockMenu() {
		return (PopupMenu) call("getDockMenu");
	}
	
	/**
	 * Changes this application's Dock icon to the provided image.
	 *
	 * @param image
	 *
	 * @since Java for Mac OS X 10.5 - 1.5
	 * @since Java for Mac OS X 10.5 Update 1 - 1.6
	 */
	public static void setDockIconImage(final Image image) {
		call(application, "setDockIconImage", new Class<?>[] { Image.class }, new Object[]{ image });
	}
	
	/**
	 * Obtains an image of this application's Dock icon.
	 *
	 * @return an image of this application's Dock icon
	 *
	 * @since Java for Mac OS X 10.5 - 1.5
	 * @since Java for Mac OS X 10.5 Update 1 - 1.6
	 */
	public static Image getDockIconImage() {
		return (Image) call("getDockIconImage");
	}
	
	/**
	 * Affixes a small system provided badge to this application's Dock icon. Usually a number.
	 *
	 * @param badge textual label to affix to the Dock icon
	 *
	 * @since Java for Mac OS X 10.5 - 1.5
	 * @since Java for Mac OS X 10.5 Update 1 - 1.6
	 */
	public static void setDockIconBadge(final String badge) {
		call(application, "setDockIconBadge", new Class<?>[]{ String.class }, new Object[]{ badge });
	}
	
	/**
	 * Sets the default menu bar to use when there are no active frames.
	 * Only used when the system property "apple.laf.useScreenMenuBar" is "true", and
	 * the Aqua Look and Feel is active.
	 *
	 * @param menuBar to use when no other frames are active
	 *
	 * @since Java for Mac OS X 10.6 Update 1
	 * @since Java for Mac OS X 10.5 Update 6 - 1.6, 1.5
	 */
	public static void setDefaultMenuBar(final JMenuBar menuBar) {
		call(application, "setDefaultMenuBar", new Class<?>[]{ JMenuBar.class }, new Object[]{ menuBar });
	}
	
	/**
	 * Requests that a {@link Window} should animate into or out of full screen mode.
	 * Only {@link Window}s marked as full screenable by {@link FullScreenUtilities#setWindowCanFullScreen(Window, boolean)} can be toggled.
	 *
	 * @param window to animate into or out of full screen mode
	 *
	 * @since Java for Mac OS X 10.7 Update 1
	 */
	public static void requestToggleFullScreen(final Window window) {
		call(application, "requestToggleFullScreen", new Class<?>[]{ Window.class }, new Object[]{ window });
	}
	
	
	// -- DEPRECATED API --
	
	/**
	 * Adds the specified ApplicationListener as a receiver of callbacks from this class.
	 * This method throws a RuntimeException if the newer About, Preferences, Quit, etc handlers are installed.
	 *
	 * @param listener an implementation of ApplicationListener that handles ApplicationEvents
	 *
	 * @deprecated register individual handlers for each task (About, Preferences, Open, Print, Quit, etc)
	 * @since 1.4
	 */
	@Deprecated
	public static void addApplicationListener(final ApplicationListener listener) {
		if (application != null) {
			try {
				Object handler = APPLICATION_MAP.get(listener);
				if (handler == null) {
					Class<?> proxy = Proxy.getProxyClass(loader, _applicationListenerClass);
					handler = proxy.getConstructor(InvocationHandler.class)
						.newInstance(new AppInvocationHandler<ApplicationListener, ApplicationEvent>(listener, ApplicationEvent.class));
					Object prev = APPLICATION_MAP.putIfAbsent(listener, handler);
					if (prev != null) {
						handler = null;
					}
				}
				call(application, "addApplicationListener", new Class<?>[]{ _applicationListenerClass }, new Object[]{ handler });
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
			| NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Removes the specified ApplicationListener from being a receiver of callbacks from this class.
	 * This method throws a RuntimeException if the newer About, Preferences, Quit, etc handlers are installed.
	 *
	 * @param listener an implementation of ApplicationListener that had previously been registered to handle ApplicationEvents
	 *
	 * @deprecated unregister individual handlers for each task (About, Preferences, Open, Print, Quit, etc)
	 * @since 1.4
	 */
	@Deprecated
	public static void removeApplicationListener(final ApplicationListener listener) {
		if (application != null) {
			Object handler = APPLICATION_MAP.remove(listener);
			
			if (handler != null)
				call(application, "removeApplicationListener", new Class<?>[]{ _applicationListenerClass }, new Object[]{ handler });
		}
	}
	
	/**
	 * Enables the Preferences item in the application menu. The ApplicationListener receives a callback for
	 * selection of the Preferences item in the application menu only if this is set to <code>true</code>.
	 *
	 * If a Preferences item isn't present, this method adds and enables it.
	 *
	 * @param enable specifies whether the Preferences item in the application menu should be enabled (<code>true</code>) or not (<code>false</code>)
	 *
	 * @deprecated no replacement
	 * @since 1.4
	 */
	@Deprecated
	public static void setEnabledPreferencesMenu(final boolean enable) {
		call(application, "setEnabledPreferencesMenu", new Class<?>[]{ Boolean.TYPE }, new Object[]{ Boolean.valueOf(enable) });
	}
	
	/**
	 * Enables the About item in the application menu. The ApplicationListener receives a callback for
	 * selection of the About item in the application menu only if this is set to <code>true</code>. Because AWT supplies
	 * a standard About window when an application may not, by default this is set to <code>true</code>.
	 *
	 * If the About item isn't present, this method adds and enables it.
	 *
	 * @param enable specifies whether the About item in the application menu should be enabled (<code>true</code>) or not (<code>false</code>)
	 *
	 * @deprecated no replacement
	 * @since 1.4
	 */
	@Deprecated
	public static void setEnabledAboutMenu(final boolean enable) {
		call(application, "setEnabledAboutMenu", new Class<?>[]{ Boolean.TYPE }, new Object[]{ Boolean.valueOf(enable) });
	}
	
	/**
	 * Determines if the Preferences item of the application menu is enabled.
	 *
	 * @deprecated no replacement
	 * @since 1.4
	 */
	@Deprecated
	public static boolean getEnabledPreferencesMenu() {
		Boolean enabled = (Boolean) call("getEnabledPreferencesMenu");
		return (enabled == null ? false : enabled.booleanValue());
	}
	
	/**
	 * Determines if the About item of the application menu is enabled.
	 *
	 * @deprecated no replacement
	 * @since 1.4
	 */
	@Deprecated
	public static boolean getEnabledAboutMenu() {
		Boolean enabled = (Boolean) call("getEnabledAboutMenu");
		return (enabled == null ? false : enabled.booleanValue());
	}
	
	/**
	 * Determines if the About item of the application menu is present.
	 *
	 * @deprecated no replacement
	 * @since 1.4
	 */
	@Deprecated
	public static boolean isAboutMenuItemPresent() {
		Boolean present = (Boolean) call("isAboutMenuItemPresent");
		return (present == null ? false : present.booleanValue());
	}
	
	/**
	 * Adds the About item to the application menu if the item is not already present.
	 *
	 * @deprecated use {@link #setAboutHandler(AboutHandler)} with a non-null {@link AboutHandler} parameter
	 * @since 1.4
	 */
	@Deprecated
	public static void addAboutMenuItem() {
		call("addAboutMenuItem");
	}
	
	/**
	 * Removes the About item from the application menu if  the item is present.
	 *
	 * @deprecated use {@link #setAboutHandler(AboutHandler)} with a null parameter
	 * @since 1.4
	 */
	@Deprecated
	public static void removeAboutMenuItem() {
		call("removeAboutMenuItem");
	}
	
	/**
	 * Determines if the About Preferences of the application menu is present. By default there is no Preferences menu item.
	 *
	 * @deprecated no replacement
	 * @since 1.4
	 */
	@Deprecated
	public static boolean isPreferencesMenuItemPresent() {
		Boolean present = (Boolean) call("isPreferencesMenuItemPresent");
		return (present == null ? false : present.booleanValue());
	}
	
	/**
	 * Adds the Preferences item to the application menu if the item is not already present.
	 *
	 * @deprecated use {@link #setPreferencesHandler(PreferencesHandler)} with a non-null {@link PreferencesHandler} parameter
	 * @since 1.4
	 */
	@Deprecated
	public static void addPreferencesMenuItem() {
		call("addPreferencesMenuItem");
	}
	
	/**
	 * Removes the Preferences item from the application menu if that item is present.
	 *
	 * @deprecated use {@link #setPreferencesHandler(PreferencesHandler)} with a null parameter
	 * @since 1.4
	 */
	@Deprecated
	public static void removePreferencesMenuItem() {
		call("removePreferencesMenuItem");
	}
	
	/**
	 * @deprecated Use <code>java.awt.MouseInfo.getPointerInfo().getLocation()</code>.
	 *
	 * @since 1.4
	 */
	@Deprecated
	public static Point getMouseLocationOnScreen() {
		return java.awt.MouseInfo.getPointerInfo().getLocation();
	}
	
	private static void doPrivileged(final AccessibleObject ao) {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			@Override
			public Void run() {
				ao.setAccessible(true);
				return null;
			}
		});
	}
	
	private static Object call(String method) {
		return call(application, method, NULL_TYPES, NULL_ARGS);
	}
    
	// Call the actual com.apple.eawt.Application method with a wrapper to the actual com.apple.eawt object.
	// The wrapper surrounds the user object passed to this Application class.
	// classes are the method parameter class types and args are the arguments to the method.
	private static Object call(Object obj, String method, Class<?>[] classes, Object[] args) {
		if (obj == null) return null;
		try {
			if (classes == null) {
				classes = new Class[args.length];
				for (int i = 0; i < classes.length; i++)
					classes[i] = args[i].getClass();
			}

			Method m = obj.getClass().getMethod(method, classes);
			return m.invoke(obj, args);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	// Wrapper around the actual XXHandler and XXListener objects in com.apple.eawt.
	// Called methods will be forwarded to the user Handler object.
	//
	// H represents the Handler class and E is the Event class that is passed to the Handler's methods.
	static class AppInvocationHandler<H, E> implements InvocationHandler {
		private H _handler;
		private Class<E> _eventClass;
		
		AppInvocationHandler(H handler, Class<E> event) {
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
				if (_quitResponseClass.isInstance(args[i])) {
					args[i] = createQuitResponseWrapper(args[i]);
					types[i] = QuitResponse.class;
				} else {
					types[i] = args[i].getClass();
					if (types[i].getName().contains("$Proxy"))
						types[i] = types[i].getInterfaces()[0]; // All proxy classes used implement one interface
				}
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
	
	// Wrapper around the actual XXEvent object in com.apple.eawt.
	private static <E> E createEventObject(final Object appleEventObj, Class<E> eventClass) {
		Class<?> proxy = Proxy.getProxyClass(Application.class.getClassLoader(), eventClass);
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
	
	private static QuitResponse createQuitResponseWrapper(final Object appleQuitResponseObj) {
		Class<?> proxy = Proxy.getProxyClass(Application.class.getClassLoader(), QuitResponse.class);
		try {
			Object quitResponse = proxy.getConstructor(InvocationHandler.class)
				.newInstance(new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return appleQuitResponseObj.getClass().getMethod(method.getName(), method.getParameterTypes())
							.invoke(appleQuitResponseObj, args);
					}
				});
			return QuitResponse.class.cast(quitResponse);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
		| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}