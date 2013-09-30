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
 * An abstract adapter class for receiving <code>ApplicationEvents</code>.
 *
 * ApplicationEvents are deprecated. Use individual app event listeners or handlers instead.
 *
 * @see Application#addAppEventListener(AppEventListener)
 *
 * @see AboutHandler
 * @see PreferencesHandler
 * @see OpenURIHandler
 * @see OpenFilesHandler
 * @see PrintFilesHandler
 * @see QuitHandler
 *
 * @see AppReOpenedListener
 * @see AppForegroundListener
 * @see AppHiddenListener
 * @see UserSessionListener
 * @see ScreenSleepListener
 * @see SystemSleepListener
 *
 * @deprecated replaced by {@link AboutHandler}, {@link PreferencesHandler}, {@link AppReOpenedListener}, {@link OpenFilesHandler}, {@link PrintFilesHandler}, {@link QuitHandler}, {@link QuitResponse}.
 * @since 1.4
 */
@Deprecated
public class ApplicationAdapter implements ApplicationListener {
	@Deprecated
	@Override
	public void handleAbout(final ApplicationEvent event) { }
	
	@Deprecated
	@Override
	public void handleOpenApplication(final ApplicationEvent event) { }
	
	@Deprecated
	@Override
	public void handleOpenFile(final ApplicationEvent event) { }
	
	@Deprecated
	@Override
	public void handlePreferences(final ApplicationEvent event) { }
	
	@Deprecated
	@Override
	public void handlePrintFile(final ApplicationEvent event) { }
	
	@Deprecated
	@Override
	public void handleQuit(final ApplicationEvent event) { }
	
	@Deprecated
	@Override
	public void handleReOpenApplication(final ApplicationEvent event) { }
}