package gamemap;

import com.ehgames.util.AABB;

public abstract class WorldObject {
	/** The bounds of this object, used for culling */
	public final AABB	bounds	= new AABB();

	// internal flags indicating what cameras have deemed this object passes
	// their culling, time of day, and layer tests
	int					visibilityFlags;

	public abstract void recalculateBounds();
}
