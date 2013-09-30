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

package lib.apple.eio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Provides functionality to query and modify Mac-specific file attributes. The methods in this class are based on Finder
 * attributes. These attributes in turn are dependent on HFS and HFS+ file systems. As such, it is important to recognize
 * their limitation when writing code that must function well across multiple platforms.<p>
 *
 * In addition to file name suffixes, Mac OS X can use Finder attributes like file <code>type</code> and <code>creator</code> codes to
 * identify and handle files. These codes are unique 4-byte identifiers. The file <code>type</code> is a string that describes the
 * contents of a file. For example, the file type <code>APPL</code> identifies the file as an application and therefore
 * executable. A file type of <code>TEXT</code>  means that the file contains raw text. Any application that can read raw
 * text can open a file of type <code>TEXT</code>. Applications that use proprietary file types might assign their files a proprietary
 * file <code>type</code> code.
 * <p>
 * To identify the application that can handle a document, the Finder can look at the <code>creator</code>. For example, if a user
 * double-clicks on a document with the <code>ttxt</code> <code>creator</code>, it opens up in Text Edit, the application registered
 * with the <code>ttxt</code> <code>creator</code> code. Note that the <code>creator</code>
 * code can be set to any application, not necessarily the application that created it. For example, if you
 * use an editor to create an HTML document, you might want to assign a browser's <code>creator</code> code for the file rather than
 * the HTML editor's <code>creator</code> code. Double-clicking on the document then opens the appropriate browser rather than the
 *HTML editor.
 *<p>
 * If you plan to publicly distribute your application, you must register its creator and any proprietary file types with the Apple
 * Developer Connection to avoid collisions with codes used by other developers. You can register a codes online at the
 * <a target=_blank href=http://developer.apple.com/dev/cftype/>Creator Code Registration</a> site.
 *
 * @since 1.4
 */
public final class FileManager {
	private static Method _osTypeToInt = null;
	private static Method _setFileTypeAndCreator = null;
	private static Method _setFileType = null;
	private static Method _setFileCreator = null;
	private static Method _getFileType = null;
	private static Method _getFileCreator = null;
	private static Method _findFolder = null;
	private static Method _openURL = null;
	private static Method _getResourceFromBundle = null;
	private static Method _getPathToApplicationBundle = null;
	private static Method _moveToTrash = null;
	private static Method _revealInFinder = null;
	
	static {
		try {
			Path java = Paths.get("/System/Library/Java");
			if (Files.exists(java, LinkOption.NOFOLLOW_LINKS)) {
				ClassLoader sys_loader = ClassLoader.getSystemClassLoader();
				if (URLClassLoader.class.isAssignableFrom(sys_loader.getClass())) {
					Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
					doPrivileged(addUrl);
					addUrl.invoke(sys_loader, java.toUri().toURL());
				}
			}
			
			Class<?> fmClass = Class.forName("com.apple.eio.FileManager");
			_osTypeToInt = fmClass.getMethod("OSTypeToInt", String.class);
			_setFileTypeAndCreator = fmClass.getMethod("setFileTypeAndCreator", String.class, Integer.TYPE, Integer.TYPE);
			_setFileType = fmClass.getMethod("setFileType", String.class, Integer.TYPE);
			_setFileCreator = fmClass.getMethod("setFileCreator", String.class, Integer.TYPE);
			_getFileType = fmClass.getMethod("getFileType", String.class);
			_getFileCreator = fmClass.getMethod("getFileCreator", String.class);
			_findFolder = fmClass.getMethod("findFolder", Short.TYPE, Integer.TYPE, Boolean.TYPE);
			_openURL = fmClass.getMethod("openURL", String.class);
			_getResourceFromBundle = fmClass.getMethod("getResourceFromBundle", String.class, String.class, String.class);
			_getPathToApplicationBundle = fmClass.getMethod("getPathToApplicationBundle");
			_moveToTrash = fmClass.getMethod("moveToTrash", File.class);
			_revealInFinder = fmClass.getMethod("revealInFinder", File.class);
		} catch (ClassNotFoundException e) {
		} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * The default
	 * @since Java for Mac OS X 10.5 - 1.5
	 * @since Java for Mac OS X 10.5 Update 1 - 1.6
	 */
	public final static short kOnAppropriateDisk = -32767;
	
	/**
	 * Read-only system hierarchy.
	 * @since Java for Mac OS X 10.5 - 1.5
	 * @since Java for Mac OS X 10.5 Update 1 - 1.6
	 */
	public final static short kSystemDomain = -32766;
	
	/**
	 * All users of a single machine have access to these resources.
	 * @since Java for Mac OS X 10.5 - 1.5
	 * @since Java for Mac OS X 10.5 Update 1 - 1.6
	 */
	public final static short kLocalDomain = -32765;
	
	/**
	 * All users configured to use a common network server has access to these resources.
	 * @since Java for Mac OS X 10.5 - 1.5
	 * @since Java for Mac OS X 10.5 Update 1 - 1.6
	 */
	public final static short kNetworkDomain = -32764;
	
	/**
	 * Read/write. Resources that are private to the user.
	 * @since Java for Mac OS X 10.5 - 1.5
	 * @since Java for Mac OS X 10.5 Update 1 - 1.6
	 */
	public final static short kUserDomain = -32763;
	
	/**
	 * Converts an OSType (e.g. "macs" from <CarbonCore/Folders.h>) into an int.
	 *
	 * @param type the 4 character type to convert.
	 * @return an int representing the 4 character value
	 *
	 * @since Java for Mac OS X 10.5 - 1.5
	 * @since Java for Mac OS X 10.5 Update 1 - 1.6
	 */
	public static int OSTypeToInt(String type) {
		if (_osTypeToInt != null) {
			return ((Integer) call(_osTypeToInt, type)).intValue();
		}
		return 0;
	}
	
	/**
	 * Sets the file <code>type</code> and <code>creator</code> codes for a file or folder.
	 *
	 * @since 1.4
	 */
	public static void setFileTypeAndCreator(String filename, int type, int creator) throws IOException {
		if (_setFileTypeAndCreator != null) {
			call(_setFileTypeAndCreator, filename, type, creator);
		}
	}
	
	/**
	 * Sets the file <code>type</code> code for a file or folder.
	 *
	 * @since 1.4
	 */
	public static void setFileType(String filename, int type) throws IOException {
		if (_setFileType != null) {
			call(_setFileType, filename, type);
		}
	}
	
	/**
	 * Sets the file <code>creator</code> code for a file or folder.
	 *
	 * @since 1.4
	 */
	public static void setFileCreator(String filename, int creator) throws IOException {
		if (_setFileCreator != null) {
			call(_setFileCreator, filename, creator);
		}
	}
	
	/**
	 * Obtains the file <code>type</code> code for a file or folder.
	 *
	 * @since 1.4
	 */
	public static int getFileType(String filename) throws IOException {
		if (_getFileType != null) {
			return ((Integer) call(_getFileType, filename)).intValue();
		}
		throw new IOException("Not on a Mac");
	}
	
	/**
	 * Obtains the file <code>creator</code> code for a file or folder.
	 *
	 * @since 1.4
	 */
	public static int getFileCreator(String filename) throws IOException {
		if (_getFileCreator != null) {
			return ((Integer) call(_getFileCreator, filename)).intValue();
		}
		throw new IOException("Not on a Mac");
	}
	
	/**
	 * Locates a folder of a particular type. Mac OS X recognizes certain specific folders that have distinct purposes.
	 * For example, the user's desktop or temporary folder. These folders have corresponding codes. Given one of these codes,
	 * this method returns the path to that particular folder. Certain folders of a given type may appear in more than
	 * one domain. For example, although there is only one <code>root</code> folder, there are multiple <code>pref</code>
	 * folders. If this method is called to find the <code>pref</code> folder, it will return the first one it finds,
	 * the user's preferences folder in <code>~/Library/Preferences</code>. To explicitly locate a folder in a certain
	 * domain use <code>findFolder(short domain, int folderType)</code> or <code>findFolder(short domain, int folderType,
	 * boolean createIfNeeded)</code>.
	 *
	 * @return the path to the folder searched for
	 *
	 * @since 1.4
	 */
	public static String findFolder(int folderType) throws FileNotFoundException {
		return findFolder(kOnAppropriateDisk, folderType);
	}
	
	/**
	 * Locates a folder of a particular type, within a given domain. Similar to <code>findFolder(int folderType)</code>
	 * except that the domain to look in can be specified. Valid values for <code>domain</code>include:
	 * <dl>
	 * <dt>user</dt>
	 * <dd>The User domain contains resources specific to the user who is currently logged in</dd>
	 * <dt>local</dt>
	 * <dd>The Local domain contains resources shared by all users of the system but are not needed for the system
	 * itself to run.</dd>
	 * <dt>network</dt>
	 * <dd>The Network domain contains resources shared by users of a local area network.</dd>
	 * <dt>system</dt>
	 * <dd>The System domain contains the operating system resources installed by Apple.</dd>
	 * </dl>
	 *
	 * @return the path to the folder searched for
	 *
	 * @since 1.4
	 */
	public static String findFolder(short domain, int folderType) throws FileNotFoundException {
		return findFolder(domain, folderType, false);
	}
	
	/**
	 * Locates a folder of a particular type within a given domain and optionally creating the folder if it does
	 * not exist. The behavior is similar to <code>findFolder(int folderType)</code> and
	 * <code>findFolder(short domain, int folderType)</code> except that it can create the folder if it does not already exist.
	 *
	 * @param createIfNeeded
	 *            set to <code>true</code>, by setting to <code>false</code> the behavior will be the
	 *            same as <code>findFolder(short domain, int folderType, boolean createIfNeeded)</code>
	 * @return the path to the folder searched for
	 *
	 * @since 1.4
	 */
	public static String findFolder(short domain, int folderType, boolean createIfNeeded) throws FileNotFoundException {
		if (_findFolder != null) {
			return (String) call(_findFolder, domain, folderType, createIfNeeded);
		}
		throw new FileNotFoundException("Can't find folder: " + Integer.toHexString(folderType));
	}
	
	/**
	 * Opens the path specified by a URL in the appropriate application for that URL. HTTP URL's (<code>http://</code>)
	 * open in the default browser as set in the Internet pane of System Preferences. File (<code>file://</code>) and
	 * FTP URL's (<code>ftp://</code>) open in the Finder. Note that opening an FTP URL will prompt the user for where
	 * they want to save the downloaded file(s).
	 *
	 * @param url
	 *            the URL for the file you want to open, it can either be an HTTP, FTP, or file url
	 *
	 * @deprecated this functionality has been superseded by java.awt.Desktop.browse() and java.awt.Desktop.open()
	 *
	 * @since 1.4
	 */
	@Deprecated
	public static void openURL(String url) throws IOException {
		if (_openURL != null) {
			call(_openURL, url);
		}
	}
	
	/**
	 * @return full pathname for the resource identified by a given name.
	 *
	 * @since 1.4
	 */
	public static String getResource(String resourceName) throws FileNotFoundException {
		return getResourceFromBundle(resourceName, null, null);
	}
	
	/**
	 * @return full pathname for the resource identified by a given name and located in the specified bundle subdirectory.
	 *
	 * @since 1.4
	 */
	public static String getResource(String resourceName, String subDirName) throws FileNotFoundException {
		return getResourceFromBundle(resourceName, subDirName, null);
	}
	
	/**
	 * Returns the full pathname for the resource identified by the given name and file extension
	 * and located in the specified bundle subdirectory.
	 *
	 * If extension is an empty string or null, the returned pathname is the first one encountered where the
	 * file name exactly matches name.
	 *
	 * If subpath is null, this method searches the top-level nonlocalized resource directory (typically Resources)
	 * and the top-level of any language-specific directories. For example, suppose you have a modern bundle and
	 * specify "Documentation" for the subpath parameter. This method would first look in the
	 * Contents/Resources/Documentation directory of the bundle, followed by the Documentation subdirectories of
	 * each language-specific .lproj directory. (The search order for the language-specific directories
	 * corresponds to the user's preferences.) This method does not recurse through any other subdirectories at
	 * any of these locations. For more details see the AppKit NSBundle documentation.
	 *
	 * @return full pathname for the resource identified by the given name and file extension and located in the specified bundle subdirectory.
	 *
	 * @since 1.4
	 */
	public static String getResource(String resourceName, String subDirName, String type) throws FileNotFoundException {
		return getResourceFromBundle(resourceName, subDirName, type);
	}
	
	private static String getResourceFromBundle(String resourceName, String subDirName, String type) throws FileNotFoundException {
		if (_getResourceFromBundle != null) {
			return (String) call(_getResourceFromBundle, resourceName, subDirName, type);
		}
		throw new FileNotFoundException(resourceName);
	}
	
	/**
	 * Obtains the path to the current application's NSBundle, may not
	 * return a valid path if Java was launched from the command line.
	 *
	 * @return full pathname of the NSBundle of the current application executable.
	 *
	 * @since Java for Mac OS X 10.5 Update 1 - 1.6
	 * @since Java for Mac OS X 10.5 Update 2 - 1.5
	 */
	public static String getPathToApplicationBundle() {
		if (_getPathToApplicationBundle != null) {
			return (String) call(_getPathToApplicationBundle);
		}
		return null;
	}
	
	/**
	 * Moves the specified file to the Trash
	 *
	 * @param file
	 * @return returns true if the NSFileManager successfully moved the file to the Trash.
	 * @throws FileNotFoundException
	 *
	 * @since Java for Mac OS X 10.6 Update 1 - 1.6
	 * @since Java for Mac OS X 10.5 Update 6 - 1.6, 1.5
	 */
	public static boolean moveToTrash(final File file) throws FileNotFoundException {
		if (_moveToTrash != null) {
			return ((Boolean) call(_moveToTrash, file)).booleanValue();
		}
		throw new FileNotFoundException();
	}
	
	/**
	 * Reveals the specified file in the Finder
	 *
	 * @param file
	 *            the file to reveal
	 * @return returns true if the NSFileManager successfully revealed the file in the Finder.
	 * @throws FileNotFoundException
	 *
	 * @since Java for Mac OS X 10.6 Update 1 - 1.6
	 * @since Java for Mac OS X 10.5 Update 6 - 1.6, 1.5
	 */
	public static boolean revealInFinder(final File file) throws FileNotFoundException {
		if (_revealInFinder != null) {
			return ((Boolean) call(_revealInFinder, file)).booleanValue();
		}
		throw new FileNotFoundException();
	}
	
	private static void doPrivileged(final Method m) {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			@Override
			public Void run() {
				m.setAccessible(true);
				return null;
			}
		});
	}
	
	// Call the actual com.apple.eio.FileManager method.
	private static Object call(Method method, Object... args) {
		if (method == null) return null;
		try {
			return method.invoke(null, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}