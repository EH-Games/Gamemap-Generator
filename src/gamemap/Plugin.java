package gamemap;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import gamemap.types.MapInfo;
import gamemap.world.World;

public interface Plugin {
	/** Gets a string to be used to identify the plugin in errors displayed to the user */
	public String getName();
	
	/**
	 * Specifies what file types this plugin handles.
	 * This method must return a valid array with at least one item
	 * for the program to properly identify this plugin as being capable of loading files.
	 */
	public FileFilter[] getFileFilters();
	
	/**
	 * Gets a list of possible worlds that can be loaded from the given file.<br>
	 * Returning null or an empty array will be interpreted as
	 * the plugin being unable to load any worlds.<br>
	 * If there is more than one item in the returned array,
	 * the user will be presented with a dialog to select which world they want to load.<br>
	 * If the array consists of only a single value, that option will be selected automatically.
	 * This singular item can be anything, even an empty string or null.<br>
	 * The plugin should perform at least a minor amount of testing to see if it can parse the file
	 * before returning anything so that it doesn't give the idea that it can handle things it can't
	 * and prevent other plugins that might be able to in the process.
	 * @param file The user-selected file that has been accepted by one of this plugin's FileFilters
	 * @see #getFileFilters()
	 */
	public String[] getWorldList(File file);
	
	/**
	 * Creates a world object from the file specified if possible.<br>
	 * All objects in the world should be present immediately,
	 * however graphical resources for them may be left unloaded until actually needed. 
	 * @param file The user-selected file that has been accepted by one of this plugin's FileFilters
	 * @param worldName The name of the world selected by the user
	 * from the list returned by {@link #getWorldList(File)}
	 * @see #getFileFilters()
	 * @see #getWorldList(File)
	 */
	public World loadWorld(File file, String worldName);
	
	/**
	 * Allows a plugin to fill in any additional details that will be written to json on export.
	 * @param info The data that will be written to a file named "map.json"
	 */
	public default void modifyBeforeWrite(MapInfo info) {}
}