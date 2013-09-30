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
 * The strategy use to shut down the application, if Sudden Termination is not enabled.
 *
 * @see Application#setQuitHandler(QuitHandler)
 * @see Application#setQuitStrategy(QuitStrategy)
 * @see Application#enableSuddenTermination()
 * @see Application#disableSuddenTermination()
 *
 * @since Java for Mac OS X 10.6 Update 3
 * @since Java for Mac OS X 10.5 Update 8
 */
public enum QuitStrategy {
	/**
	 * Shuts down the application by calling <code>System.exit(0)</code>. This is the default strategy.
	 */
	SYSTEM_EXIT_0,
	
	/**
	 * Shuts down the application by closing each window from back-to-front.
	 */
	CLOSE_ALL_WINDOWS
}