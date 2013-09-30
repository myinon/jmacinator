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

import lib.apple.eawt.AppEvent.QuitEvent;

/**
 * An implementor determines if requests to quit this application should proceed or cancel.
 *
 * @see Application#setQuitHandler(QuitHandler)
 * @see Application#setQuitStrategy(QuitStrategy)
 *
 * @since Java for Mac OS X 10.6 Update 3
 * @since Java for Mac OS X 10.5 Update 8
 */
public interface QuitHandler {
	/**
	 * Invoked when the application is asked to quit.
	 *
	 * Implementors must call either {@link QuitResponse#cancelQuit()}, {@link QuitResponse#performQuit()}, or ensure the application terminates.
	 * The process (or log-out) requesting this app to quit will be blocked until the {@link QuitResponse} is handled.
	 * Apps that require complex UI to shutdown may call the {@link QuitResponse} from any thread.
	 * Your app may be asked to quit multiple times before you have responded to the initial request.
	 * This handler is called each time a quit is requested, and the same {@link QuitResponse} object is passed until it is handled.
	 * Once used, the {@link QuitResponse} cannot be used again to change the decision.
	 *
	 * @param e the request to quit this application.
	 * @param response the one-shot response object used to cancel or proceed with the quit action.
	 */
	public void handleQuitRequestWith(final QuitEvent e, final QuitResponse response);
}