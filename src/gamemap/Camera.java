package gamemap;

import com.ehgames.util.AABB;
import com.ehgames.util.Vec3;

public class Camera {
	Vec3	position	= new Vec3();
	AABB	viewBounds	= new AABB();
	
	float	nearClip	= 0.1f;
	float	farClip		= 1000;
	boolean	perspective	= false;
	float	fovY;
	
	/** internal flag used for marking objects as visible */
	int		cameraFlag;
	
	/** a single bit indicating which area should be rendered */
	int		areaFlag;
	
	int		time;
	
	void copyWorldProperties(Camera camera) {
		areaFlag = camera.areaFlag;
	}
	
	void copyCameraProperties(Camera camera) {
		fovY = camera.fovY;
		nearClip = camera.nearClip;
		farClip = camera.farClip;
		perspective = camera.perspective;
	}
	
	void calculateBounds() {
		// TODO implement
		
	}
}
