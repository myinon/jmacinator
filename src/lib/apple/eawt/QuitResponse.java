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

/**
 * Used to respond to a request to quit the application.
 * The QuitResponse may be used after the {@link QuitHandler#handleQuitRequestWith(AppEvent.QuitEvent, QuitResponse)} method has returned, and may be used from any thread.
 *
 * @see Application#setQuitHandler(QuitHandler)
 * @see QuitHandler
 * @see Application#setQuitStrategy(QuitStrategy)
 *
 * @since Java for Mac OS X 10.6 Update 3
 * @since Java for Mac OS X 10.5 Update 8
 */
public interface QuitResponse {
	/**
	 * Notifies the external quit requester that the quit will proceed, and performs the default {@link QuitStrategy}.
	 */
	public void performQuit();
	
	/**
	 * Notifies the external quit requester that the user has explicitly canceled the pending quit, and leaves the application running.
	 * <b>Note: this will cancel a pending log-out, restart, or shutdown.</b>
	 */
	public void cancelQuit();
}