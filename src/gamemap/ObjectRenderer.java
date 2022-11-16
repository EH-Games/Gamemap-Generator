package gamemap;

import com.ehgames.util.GLDestructable;

import gamemap.world.RenderState;

public interface ObjectRenderer extends GLDestructable {
	/**
	 * Indicates that parts of this renderer have translucency
	 * or have bitmask transparency with linear filtering
	 */
	public default boolean hasComplexTransparency() {
		return true;
	}
	
	/**
	 * Indicates that parts of this renderer has no transparency
	 * or have bitmask transparency with nearest filtering
	 */
	public default boolean hasSimpleOpacity() {
		return true;
	}
	
	/**
	 * Method called by background exporting threads to know if
	 * all models within the area they're rendering is loaded.
	 * These threads will wait until all object renderers in the area return true
	 * before trying to render anything to prevent an incomplete render.<br>
	 * This method being called should be seen as a cue to begin loading resources
	 * if that hasn't already been done.
	 * Unloading of no longer used resources is advised to be done by keeping
	 * track of the last time an object was rendered and unloading the objects in
	 * {@link gamemap.world.World3d#userSetup World3d.userSetup}
	 * in a synchronized manner if the time is past a certain expiry time
	 * @return true if the object is fully loaded or has failed to load.
	 * 		false if has yet to be loaded or is in the process of loading.
	 */
	public default boolean requestLoad() {
		return true;
	}
	
	public void render(RenderState state);
}
