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

import lib.apple.eawt.AppEvent.FullScreenEvent;

/**
 * Abstract adapter class for receiving fullscreen events. This class is provided
 * as a convenience for creating listeners.
 *
 * Subclasses registered with {@link FullScreenUtilities#addFullScreenListenerTo(javax.swing.RootPaneContainer, FullScreenListener)}
 * will receive all entering/entered/exiting/exited full screen events.
 *
 * @see FullScreenUtilities
 *
 * @since Java for Mac OS X 10.7 Update 1
 */
public abstract class FullScreenAdapter implements FullScreenListener {
	@Override
	public void windowEnteringFullScreen(final FullScreenEvent e) { }
	
	@Override
	public void windowEnteredFullScreen(final FullScreenEvent e) { }
	
	@Override
	public void windowExitingFullScreen(final FullScreenEvent e) { }
	
	@Override
	public void windowExitedFullScreen(final FullScreenEvent e) { }
}