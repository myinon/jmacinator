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
 * The class of events sent to the deprecated ApplicationListener callbacks.
 *
 * @deprecated replaced by {@link AboutHandler}, {@link PreferencesHandler}, {@link AppReOpenedListener}, {@link OpenFilesHandler}, {@link PrintFilesHandler}, {@link QuitHandler}, {@link QuitResponse}
 * @since 1.4
 */
@Deprecated
public interface ApplicationEvent {
	/**
	 * The object on which the Event initially occurred.
	 * @return The object on which the Event initially occurred.
	 */
	public Object getSource();
	
	/**
	 * Returns a String representation of this EventObject.
	 * @return A a String representation of this EventObject.
	 */
	public String toString();
	
	/**
	 * Determines whether an ApplicationListener has acted on a particular event.
	 * An event is marked as having been handled with <code>setHandled(true)</code>.
	 *
	 * @return <code>true</code> if the event has been handled, otherwise <code>false</code>
	 *
	 * @since 1.4
	 * @deprecated
	 */
	@Deprecated
	public boolean isHandled();
	
	/**
	 * Marks the event as handled.
	 * After this method handles an ApplicationEvent, it may be useful to specify that it has been handled.
	 * This is usually used in conjunction with <code>getHandled()</code>.
	 * Set to <code>true</code> to designate that this event has been handled. By default it is <code>false</code>.
	 *
	 * @param state <code>true</code> if the event has been handled, otherwise <code>false</code>.
	 *
	 * @since 1.4
	 * @deprecated
	 */
	@Deprecated
	public void setHandled(final boolean state);
	
	/**
	 * Provides the filename associated with a particular AppleEvent.
	 * When the ApplicationEvent corresponds to an AppleEvent that needs to act on a particular file, the ApplicationEvent carries the name of the specific file with it.
	 * For example, the Print and Open events refer to specific files.
	 * For these cases, this returns the appropriate file name.
	 *
	 * @return the full path to the file associated with the event, if applicable, otherwise <code>null</code>
	 *
	 * @since 1.4
	 * @deprecated use {@link OpenFilesHandler} or {@link PrintFilesHandler} instead
	 */
	@Deprecated
	public String getFilename();
}