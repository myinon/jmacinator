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

import lib.apple.eawt.AppEvent.AppReOpenedEvent;

/**
 * Implementors receive notification when the app has been asked to open again.
 * Re-open events occur when the user clicks on the running app's Dock icon.
 * Re-open events also occur when the app is double-clicked in the Finder and the app is already running.
 *
 * This notification is useful for showing a new document when your app has no open windows.
 *
 * @see Application#addAppEventListener(AppEventListener)
 *
 * @since Java for Mac OS X 10.6 Update 3
 * @since Java for Mac OS X 10.5 Update 8
 */
public interface AppReOpenedListener extends AppEventListener {
	/**
	 * Called when the app has been re-opened (it's Dock icon was clicked on, or was double-clicked in the Finder)
	 * @param e the request to re-open the app
	 */
	public void appReOpened(final AppReOpenedEvent e);
}