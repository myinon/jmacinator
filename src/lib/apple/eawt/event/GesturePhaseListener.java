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

package lib.apple.eawt.event;

/**
 * Listener interface for receiving notification that a continuous gesture
 * (like rotate or scale) has started or stopped.
 *
 * @see MagnificationListener
 * @see RotationListener
 * @see GesturePhaseEvent
 * @see GestureUtilities
 *
 * @since Java for Mac OS X 10.5 Update 7, Java for Mac OS X 10.6 Update 2
 */
public interface GesturePhaseListener extends GestureListener {
	/**
	 * Invoked when the user has started a continuous gesture.
	 * @param event representing the start of a continuous gesture.
	 */
	public void gestureBegan(final GesturePhaseEvent e);
	
	/**
	 * Invoked when the user has stopped a continuous gesture.
	 * @param event representing the end of a continuous gesture.
	 */
	public void gestureEnded(final GesturePhaseEvent e);
}