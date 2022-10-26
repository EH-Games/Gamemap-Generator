package gamemap.world;

public final class World3d extends World {
	public final WorldGroup	root		= new WorldGroup();

	public int				firstLayer	= 0;
	public int				lastLayer	= 0;

	@Override
	public void render(Camera camera) {
		if(camera.perspective) {
			root.testVisibility(camera);
		} else {
			root.testVisibility2d(camera);
		}
		
		for(int layer = firstLayer; layer <= lastLayer; layer++) {
			root.render(camera, false);
		}
		for(int layer = firstLayer; layer <= lastLayer; layer++) {
			root.render(camera, true);
		}
	}
}
