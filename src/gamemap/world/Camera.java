package gamemap.world;

import java.util.LinkedList;

import com.ehgames.util.AABB;
import com.ehgames.util.Mat4;
import com.ehgames.util.Vec3;

public class Camera {
	private static final float					ORTHO_Z_VAL		= 2000;
	private static final LinkedList<Integer>	FREE_CAMERAS	= new LinkedList<>();

	static {
		for(int i = 0; i < 28; i++) {
			FREE_CAMERAS.add(1 << i);
		}
	}

	final Vec3		position			= new Vec3();
	final AABB		bounds				= new AABB();

	float			nearClip			= 0.1f;
	float			farClip				= 1000;
	float			nearClipSq			= 0.01f;
	float			farClipSq			= 1000_000;
	boolean			perspective			= false;
	float			fovY;

	private Vec3[]	frustumPoints		= new Vec3[8];
	Mat4			projection			= new Mat4();
	Mat4			view				= new Mat4();

	double			pixelsPerUnit		= 1;
	float			halfWidth;
	float			halfHeight;

	/** internal flag used for marking objects as visible */
	int				cameraFlag;

	/** a single bit indicating which area should be rendered */
	int				areaFlag;

	/** a single bit indicating which layer should be rendered */
	int				layerFlag;

	int				time;

	boolean			blockUntilLoaded	= false;
	
	public Camera() {
		Integer flag = FREE_CAMERAS.poll();
		if(flag != null) cameraFlag = flag;
	}

	public void onWorldChange(World world) {
		position.set(world.initialPos);
		int area = world.defaultArea;
		if(area >= world.areaCount) area = 0;
		areaFlag = 1 << world.defaultArea;
		calculateBounds();
	}

	public void onViewportResize(int width, int height) {
		halfWidth = width * 0.5f;
		halfHeight = height * 0.5f;
		
		if(perspective) {
			// TODO implement	
		} else {
			bounds.minZ = -ORTHO_Z_VAL;
			bounds.maxZ = ORTHO_Z_VAL;
		}
		calculateBounds();
	}
	
	public boolean isPerspective() {
		return perspective;
	}
	
	public void move(float x, float y, float z) {
		position.x += x;
		position.y += y;
		position.z += z;
		calculateBounds();
	}
	
	public void setZoomLevel(int level) {
		// much cleaner code when you use the standard library method that does
		// the same exact thing as the 20 lines of code you wrote -EH (10/28/22)
		double ppuNew = Math.pow(1.1, level);

		// causes zoom to remain centered on window center
		// we could possibly factor in mouse coordinates as well to make it centered on the mouse
		//position.x = (float) (position.x / pixelsPerUnit * ppuNew);
		//position.y = (float) (position.y / pixelsPerUnit * ppuNew);
		
		//System.out.println("s = " + ppuNew);
		pixelsPerUnit = ppuNew;
		calculateBounds();
	}

	/** gets the scale in pixels per unit for when this camera uses an orthographic projection */
	public double getScale() {
		return pixelsPerUnit;
	}

	void copyWorldProperties(Camera camera) {
		areaFlag = camera.areaFlag;
		time = camera.time;
	}

	void copyCameraProperties(Camera camera) {
		fovY = camera.fovY;
		nearClip = camera.nearClip;
		farClip = camera.farClip;
		perspective = camera.perspective;
		
		halfWidth = camera.halfWidth;
		halfHeight = camera.halfHeight;
	}
	
	void calculateBounds() {
		if(perspective) {
			// TODO implement
			// build frustum from near clip, far clip & fov(along with matrix)
			// above can be cached and only updaed when needed
			bounds.prepForBuild();
			Vec3 transformed = new Vec3();
			for(Vec3 fp : frustumPoints) {
				// rotate point using pitch and yaw
				transformed.addInPlace(position);
				bounds.add(transformed);
			}
		} else {
			// min and max z assumed to already be set to
			// user-defined minimum and maximum values
			// if width is 640 and the scale is 2 pixels per unit,
			// then the bounds should be 360 units wide, hence division
			final float xHalf = (float) (halfWidth / pixelsPerUnit);
			final float yHalf = (float) (halfHeight / pixelsPerUnit);
			bounds.minX = position.x - xHalf;
			bounds.maxX = position.x + xHalf;
			bounds.maxY = position.y + yHalf;
			bounds.minY = position.y - yHalf;
			
			projection.setOrtho(-xHalf, xHalf, -yHalf, yHalf, -ORTHO_Z_VAL, ORTHO_Z_VAL);
			view.setIdentity();
			view.m.put(12, -position.x);
			view.m.put(13, -position.y);
		}
	}
	
	public void finalize() {
		if(cameraFlag != 0) {
			FREE_CAMERAS.add(cameraFlag);
		}
	}
}
