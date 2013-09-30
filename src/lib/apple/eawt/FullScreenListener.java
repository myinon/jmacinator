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

import java.util.EventListener;

import lib.apple.eawt.AppEvent.FullScreenEvent;

/**
 *
 *
 * @since Java for Mac OS X 10.7 Update 1
 */
public interface FullScreenListener extends EventListener {
	/**
	 * Invoked when a window has started to enter full screen.
	 * @param event containing the specific window entering full screen.
	 */
	public void windowEnteringFullScreen(final FullScreenEvent e);
	
	/**
	 * Invoked when a window has fully entered full screen.
	 * @param event containing the specific window which has entered full screen.
	 */
	public void windowEnteredFullScreen(final FullScreenEvent e);
	
	/**
	 * Invoked when a window has started to exit full screen.
	 * @param event containing the specific window exiting full screen.
	 */
	public void windowExitingFullScreen(final FullScreenEvent e);
	
	/**
	 * Invoked when a window has fully exited full screen.
	 * @param event containing the specific window which has exited full screen.
	 */
	public void windowExitedFullScreen(final FullScreenEvent e);
}