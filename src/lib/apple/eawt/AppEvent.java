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
import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * AppEvents are sent to listeners and handlers installed on the {@link Application}.
 *
 * @since Java for Mac OS X 10.6 Update 3
 * @since Java for Mac OS X 10.5 Update 8
 */
public interface AppEvent {
	/**
	 * The object on which the Event initially occurred.
	 * @return The object on which the Event initially occurred.
	 */
	public Object getSource();
	
	/**
	 * Returns a String representation of this EventObject.
	 * @return A a String representation of this EventObject.
	 */
	public String toString();
	
	/**
	 * Contains a list of files.
	 */
	public static interface FilesEvent extends AppEvent {
		/**
		 * @return the list of files
		 */
		public List<File> getFiles();
	}
	
	/**
	 * Event sent when the app is asked to open a list of files.
	 *
	 * @see OpenFilesHandler#openFiles(OpenFilesEvent)
	 */
	public static interface OpenFilesEvent extends FilesEvent {
		/**
		 * If the files were opened using the Spotlight search menu or a Finder search window, this method obtains the search term used to find the files.
		 * This is useful for highlighting the search term in the documents when they are opened.
		 * @return the search term used to find the files
		 */
		public String getSearchTerm();
	}
	
	/**
	 * Event sent when the app is asked to print a list of files.
	 *
	 * @see PrintFilesHandler#printFiles(PrintFilesEvent)
	 */
	public static interface PrintFilesEvent extends FilesEvent { }
	
	/**
	 * Event sent when the app is asked to open a URI.
	 *
	 * @see OpenURIHandler#openURI(OpenURIEvent)
	 */
	public static interface OpenURIEvent extends AppEvent {
		/**
		 * @return the URI the app was asked to open
		 */
		public URI getURI();
	}
	
	/**
	 * Event sent when the application is asked to open it's about window.
	 *
	 * @see AboutHandler#handleAbout()
	 */
	public static interface AboutEvent extends AppEvent { }
	
	/**
	 * Event sent when the application is asked to open it's preferences window.
	 *
	 * @see PreferencesHandler#handlePreferences()
	 */
	public static interface PreferencesEvent extends AppEvent { }
	
	/**
	 * Event sent when the application is asked to quit.
	 *
	 * @see QuitHandler#handleQuitRequestWith(QuitEvent, QuitResponse)
	 */
	public static interface QuitEvent extends AppEvent { }
	
	/**
	 * Event sent when the application is asked to re-open itself.
	 *
	 * @see AppReOpenedListener#appReOpened(AppReOpenedEvent)
	 */
	public static interface AppReOpenedEvent extends AppEvent { }
	
	/**
	 * Event sent when the application has become the foreground app, and when it has resigned being the foreground app.
	 *
	 * @see AppForegroundListener#appRaisedToForeground(AppForegroundEvent)
	 * @see AppForegroundListener#appMovedToBackground(AppForegroundEvent)
	 */
	public static interface AppForegroundEvent extends AppEvent { }
	
	/**
	 * Event sent when the application has been hidden or shown.
	 *
	 * @see AppHiddenListener#appHidden(AppHiddenEvent)
	 * @see AppHiddenListener#appUnhidden(AppHiddenEvent)
	 */
	public static interface AppHiddenEvent extends AppEvent { }
	
	/**
	 * Event sent when the user session has been changed via Fast User Switching.
	 *
	 * @see UserSessionListener#userSessionActivated(UserSessionEvent)
	 * @see UserSessionListener#userSessionDeactivated(UserSessionEvent)
	 */
	public static interface UserSessionEvent extends AppEvent { }
	
	/**
	 * Event sent when the displays attached to the system enter and exit power save sleep.
	 *
	 * @see ScreenSleepListener#screenAboutToSleep(ScreenSleepEvent)
	 * @see ScreenSleepListener#screenAwoke(ScreenSleepEvent)
	 */
	public static interface ScreenSleepEvent extends AppEvent { }
	
	/**
	 * Event sent when the system enters and exits power save sleep.
	 *
	 * @see SystemSleepListener#systemAboutToSleep(SystemSleepEvent)
	 * @see SystemSleepListener#systemAwoke(SystemSleepEvent)
	 */
	public static interface SystemSleepEvent extends AppEvent { }
	
	/**
	 * Event sent when a window is entering/exiting or has entered/exited full screen state.
	 *
	 * @see FullScreenUtilities
	 *
	 * @since Java for Mac OS X 10.7 Update 1
	 */
	public static interface FullScreenEvent extends AppEvent {
		/**
		 * @return window transitioning between full screen states
		 */
		public Window getWindow();
	}
}