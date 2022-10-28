package gamemap;

import gamemap.world.RenderState;

public interface ObjectRenderer {
	/**
	 * Indicates that parts of this renderer have translucency
	 * or have bitmask transparency with linear filtering
	 */
	public boolean hasComplexTransparency();
	
	/**
	 * Indicates that parts of this renderer has no transparency
	 * or have bitmask transparency with nearest filtering
	 */
	public boolean hasSimpleOpacity();
	
	public void render(RenderState state);
}
