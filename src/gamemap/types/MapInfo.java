package gamemap.types;

import java.util.ArrayList;
import java.util.List;

/** structure to be exported as json for use in Gamemap */
public class MapInfo {
	public final Point			axes	= new Point(1, 1);
	public final MapView[]		views	= { new MapView() };
	// format tbd
	public final List<Object>	areas	= new ArrayList<>();
	/**
	 * id of a saved user map that contains the locations of things like collectibles.
	 * This should be the same as the id of the game if possible for the purpose of
	 * hotlinking (ex: {@link <a href="https://ehgames.com/gamemap/gta_vc">gta_vc</a>}).<br>
	 * If there is no user map with this info, this variable should be set to {@literal null}
	 */
	public String				usermap;
	// inclusion tbd
	public final List<Object>	icons	= new ArrayList<>();
}
