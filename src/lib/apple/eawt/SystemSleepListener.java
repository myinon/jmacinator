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

import lib.apple.eawt.AppEvent.SystemSleepEvent;

/**
 * Implementors receive notification as the system is entering sleep, and after the system wakes.
 *
 * This notification is useful for disconnecting from network services prior to sleep, or re-establishing a connection if the network configuration has changed during sleep.
 *
 * @see Application#addAppEventListener(AppEventListener)
 *
 * @since Java for Mac OS X 10.6 Update 3
 * @since Java for Mac OS X 10.5 Update 8
 */
public interface SystemSleepListener extends AppEventListener {
	/**
	 * Called when the system is about to sleep.
	 * Note: This message may not be delivered prior to the actual system sleep, and may be processed after the corresponding wake has occurred.
	 * @param e the system sleep event
	 */
	public void systemAboutToSleep(final SystemSleepEvent e);
	
	/**
	 * Called after the system has awoke from sleeping.
	 * @param e the system sleep event
	 */
	public void systemAwoke(final SystemSleepEvent e);
}