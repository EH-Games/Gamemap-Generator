package gamemap;

public final class World3d extends World {
	public final WorldGroup	root		= new WorldGroup();

	public int				defaultArea	= 0;
	public int				areaCount	= 1;
	public int				firstLayer	= 0;
	public int				lastLayer	= 0;

	@Override
	public void render(Camera camera) {
		for(int layer = firstLayer; layer <= lastLayer; layer++) {
			root.render(camera, false);
		}
		for(int layer = firstLayer; layer <= lastLayer; layer++) {
			root.render(camera, true);
		}
		// TODO Auto-generated method stub

	}
}
