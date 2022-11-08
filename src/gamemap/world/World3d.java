package gamemap.world;

import com.ehgames.util.GL;

import gamemap.RenderCall;

public final class World3d extends World {
	public final WorldGroup	root					= new WorldGroup();

	public int				firstLayer				= 0;
	public int				lastLayer				= 0;

	public double			preferredUnitsPerPixel	= 1;

	/**
	 * A user supplied method to perform global OpenGL rendering setup
	 * at the beginning of each frame
	 */
	public RenderCall		userSetup				= null;

	@Override
	public void render(Camera camera, GL gl) {
		if(camera.perspective) {
			root.testVisibility(camera);
		} else {
			root.testVisibility2d(camera);
		}
		
		RenderState state = new RenderState(gl, false, camera);
		if(userSetup != null) {
			state.globalBounds = root.bounds;
			userSetup.call(state);
		}
		
		for(int layer = firstLayer; layer <= lastLayer; layer++) {
			camera.layerFlag = 1 << firstLayer;
			root.render(state);
		}
		Object userData = state.userData;
		state = new RenderState(gl, true, camera);
		state.userData = userData;
		for(int layer = firstLayer; layer <= lastLayer; layer++) {
			camera.layerFlag = 1 << firstLayer;
			root.render(state);
		}
	}
	
	@Override
	public void destroyResources(GL gl) {
		root.destroyResources(gl);
		if(userSetup != null) {
			userSetup.destroyResources(gl);
		}
	}
}
