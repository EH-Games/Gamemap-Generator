package gamemap.world;

import com.ehgames.util.GL;
import com.ehgames.util.Vec3;

public abstract class World {
	/** The time value at which the day should rollover to 0 (midnight) */
	public int			timeUnitsPerDay;

	public final Vec3	backgroundColor			= new Vec3();
	/** location where the camera should be after initially loading the world */
	public final Vec3	initialPos				= new Vec3();
	public int			defaultArea				= 0;
	public int			areaCount				= 1;

	/** used for exporting and coordinate display only. not used for rendering */
	public boolean		positiveYIsDown			= false;
	/**
	 * used for exporting and coordinate display
	 * to translate between world coordinates and user coordinates
	 *  */
	public double		worldUnitsPerMapUnit	= 1;

	public String		friendlyName			= "Unknown";
	public String		mapId					= "unknown";

	// package protected so theres no funny subclasses
	World() {}

	public abstract void render(Camera camera, GL gl);

	public abstract void destroyResources(GL gl);
}