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
 * Abstract adapter class for receiving gesture events. This class is provided
 * as a convenience for creating listeners.
 *
 * Subclasses registered with {@link GestureUtilities#addGestureListenerTo()}
 * will receive all phase, magnification, rotation, and swipe events.
 *
 * @see GestureUtilities
 *
 * @since Java for Mac OS X 10.5 Update 7, Java for Mac OS X 10.6 Update 2
 */
public abstract class GestureAdapter implements GesturePhaseListener, MagnificationListener, RotationListener, SwipeListener {
	@Override
	public void gestureBegan(final GesturePhaseEvent e) { }
	
	@Override
	public void gestureEnded(final GesturePhaseEvent e) { }
	
	@Override
	public void magnify(final MagnificationEvent e) { }
	
	@Override
	public void rotate(final RotationEvent e) { }
	
	@Override
	public void swipedDown(final SwipeEvent e) { }
	
	@Override
	public void swipedLeft(final SwipeEvent e) { }
	
	@Override
	public void swipedRight(final SwipeEvent e) { }
	
	@Override
	public void swipedUp(final SwipeEvent e) { }
}