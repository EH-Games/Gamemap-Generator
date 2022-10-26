package gamemap.world;

import com.ehgames.util.AABB;
import com.ehgames.util.Vec3;

import gamemap.Gamemap;

public class Camera {
	final Vec3		position		= new Vec3();
	final AABB		bounds			= new AABB();

	float			nearClip		= 0.1f;
	float			farClip			= 1000;
	float			nearClipSq		= 0.01f;
	float			farClipSq		= 1000_000;
	boolean			perspective		= false;
	float			fovY;

	private Vec3[]	frustumPoints	= new Vec3[8];
	
	float			halfWidth;
	float			halfHeight;

	/** internal flag used for marking objects as visible */
	int				cameraFlag;

	/** a single bit indicating which area should be rendered */
	int				areaFlag;

	int				time;
	
	/** Internal method. do not call */
	public void onWorldChange(World world) {
		if(!Gamemap.isPrivelegedCode()) return;
		
		position.set(world.initialPos);
		int area = world.defaultArea;
		if(area >= world.areaCount) area = 0;
		areaFlag = 1 << world.defaultArea;

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
			bounds.minX = position.x - halfWidth;
			bounds.maxX = position.x + halfWidth;
			bounds.maxY = position.y + halfHeight;
			bounds.minY = position.y - halfHeight;
		}
	}
}
