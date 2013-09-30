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
 * Listener interface for receiving swipe events. A single swipe event
 * may be both vertical and horizontal simultaneously, invoking both
 * a vertical and horizontal method.
 *
 * @see SwipeEvent
 * @see GestureUtilities
 *
 * @since Java for Mac OS X 10.5 Update 7, Java for Mac OS X 10.6 Update 2
 */
public interface SwipeListener extends GestureListener {
	/**
	 * Invoked when an upwards swipe gesture is performed by the user.
	 * @param event representing the occurrence of a swipe.
	 */
	public void swipedUp(final SwipeEvent e);
	
	/**
	 * Invoked when a downward swipe gesture is performed by the user.
	 * @param event representing the occurrence of a swipe.
	 */
	public void swipedDown(final SwipeEvent e);
	
	/**
	 * Invoked when a leftward swipe gesture is performed by the user.
	 * @param event representing the occurrence of a swipe.
	 */
	public void swipedLeft(final SwipeEvent e);
	
	/**
	 * Invoked when a rightward swipe gesture is performed by the user.
	 * @param event representing the occurrence of a swipe.
	 */
	public void swipedRight(final SwipeEvent e);
}