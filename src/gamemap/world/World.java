package gamemap.world;

import com.ehgames.util.Vec3;

public abstract class World {
	/** The time value at which the day should rollover to 0 (midnight) */
	public int			timeUnitsPerDay;

	public final Vec3	backgroundColor	= new Vec3();
	/** location where the camera should be after initially loading the world */
	public final Vec3	initialPos		= new Vec3();
	public int			defaultArea		= 0;
	public int			areaCount		= 1;

	// package protected so theres no funny subclasses
	World() {}

	public abstract void render(Camera camera);
}