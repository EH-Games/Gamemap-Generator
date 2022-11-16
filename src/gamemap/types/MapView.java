package gamemap.types;

public class MapView {
	public static enum Type {
		// breaking java naming conventions so it ends up as
		// lowercase in json without employing additional code 
		gps, satellite, terrain;
	}
	
	/**
	 * The name that will appear in a drop down specific to the game or game map.<br>
	 * This is often a properly cased version of {@link #type}.
	 * Examples include "Radar", "GPS", "Satellite", "Terrain", "Caves", and "Interiors".
	 */
	public String					name;
	/**
	 * The type of map.<br>
	 * Used for the purpose of allowing users to have a preferred map type.
	 */
	public Type						type	= Type.satellite;
	/**
	 * If true, indicates that this view is not something that should be defaulted to.
	 * One example is an interior view as opposed to an overworld view.
	 */
	public boolean					specialized = false;
	/**
	 * A javascript color code in the form "#RRGGBB", "#RGB", or a named color.<br>
	 * This is set automatically by the export logic from
	 * {@link gamemap.world.World#backgroundColor World.backgroundColor}<br>
	 * For clarity and because of the limited selection, named colors are not advised.
	 */
	public String					bg_color;
	// bg_tile and bg_tile_size are recommended to be removed from
	// the resulting json file if only a background color is used to reduce file size
	public String					bg_tile;
	public float					bg_tile_size;
	/**
	 * The type of filtering that is applied to all bitmap images referenced by {@link #items}
	 */
	public ImageFilter				filter	= ImageFilter.linear;
	/**
	 * An array of bitmap images, svgs or tile maps to be rendered for this view.<br>
	 * For the purposes of Gamemap Generator, a single bitmap image(type="image") is exported.
	 */
	public final MapViewRenderer[]	items	= { new MapViewRenderer() };
}
