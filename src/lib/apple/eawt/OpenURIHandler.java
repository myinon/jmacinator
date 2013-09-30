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

import lib.apple.eawt.AppEvent.OpenURIEvent;

/**
 * An implementor is notified when the application is asked to open a URI.
 * The application only sends {@link com.apple.eawt.EAWTEvent.OpenURIEvent}s when it has been launched as a bundled Mac application, and it's Info.plist claims URL schemes in it's <code>CFBundleURLTypes</code> entry.
 * See the <a href="http://developer.apple.com/mac/library/documentation/General/Reference/InfoPlistKeyReference">Info.plist Key Reference</a> for more information about adding a <code>CFBundleURLTypes</code> key to your app's Info.plist.
 *
 * @see Application#setOpenURIHandler(OpenURIHandler)
 *
 * @since Java for Mac OS X 10.6 Update 3
 * @since Java for Mac OS X 10.5 Update 8
 */
public interface OpenURIHandler {
	/**
	 * Called when the application is asked to open a URI
	 * @param e the request to open a URI
	 */
	public void openURI(final OpenURIEvent e);
}