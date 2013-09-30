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

import lib.apple.eawt.AppEvent.AppForegroundEvent;

/**
 * Implementors are notified when the app becomes the foreground app and when it resigns being the foreground app.
 * This notification is useful for hiding and showing transient UI like palette windows which should be hidden when the app is in the background.
 *
 * @see Application#addAppEventListener(AppEventListener)
 *
 * @since Java for Mac OS X 10.6 Update 3
 * @since Java for Mac OS X 10.5 Update 8
 */
public interface AppForegroundListener extends AppEventListener {
	/**
	 * Called when the app becomes the foreground app.
	 * @param e the app became foreground notification.
	 */
	public void appRaisedToForeground(final AppForegroundEvent e);
	
	/**
	 * Called when the app resigns to the background and another app becomes the foreground app.
	 * @param e the app resigned foreground notification.
	 */
	public void appMovedToBackground(final AppForegroundEvent e);
}