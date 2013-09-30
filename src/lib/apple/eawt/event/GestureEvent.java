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
 * Abstract event all gestures inherit from.
 *
 * Note: GestureEvent is not subclass of {@link AWTEvent} and is not dispatched
 * directly from the {@link EventQueue}. This is an intentional design decision
 * to prevent collision with an official java.awt.* gesture event types subclassing
 * {@link InputEvent}.
 *
 * {@link GestureListener}s are only notified from the AWT Event Dispatch thread.
 *
 * @see GestureUtilities
 *
 * @since Java for Mac OS X 10.5 Update 7, Java for Mac OS X 10.6 Update 2
 */
public interface GestureEvent {
	/**
	 * Consuming an event prevents listeners later in the chain or higher in the
	 * component hierarchy from receiving the event.
	 */
	public void consume();
}