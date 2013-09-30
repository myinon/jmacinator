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

import lib.apple.eawt.AppEvent.UserSessionEvent;

/**
 * Implementors receive notification when Fast User Switching changes the user session.
 *
 * This notification is useful for discontinuing a costly animation, or indicating that the user is no longer present on a network service.
 *
 * @see Application#addAppEventListener(AppEventListener)
 *
 * @since Java for Mac OS X 10.6 Update 3
 * @since Java for Mac OS X 10.5 Update 8
 */
public interface UserSessionListener extends AppEventListener {
	/**
	 * Called when the user session has been switched away.
	 * @param e the user session switch event
	 */
	public void userSessionDeactivated(final UserSessionEvent e);
	
	/**
	 * Called when the user session has been switched to.
	 * @param e the user session switch event
	 */
	public void userSessionActivated(final UserSessionEvent e);
}