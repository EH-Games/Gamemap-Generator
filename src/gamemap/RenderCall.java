package gamemap;

import com.ehgames.util.GL;

import gamemap.world.RenderState;

public interface RenderCall {
	/**
	 * Called when the world is destroyed so that any allocated resources can be freed.
	 * @param gl the relevant OpenGL context
	 */
	public default void destroyResources(GL gl) {}
	public void call(RenderState state);
}
