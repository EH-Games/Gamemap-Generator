package gamemap.world;

import com.ehgames.util.AABB;
import com.ehgames.util.Mat4;
import com.ehgames.util.Vec3;

/**
 * A 3D plane. Used for frustum culling
 */
public class FrustumPlane {
	private final Vec3	normalView	= new Vec3();
	private final Vec3	normalWorld	= new Vec3();
	private float		distView;
	private float		distWorld;
	
	public void initFarClip(float farClip) {
		normalView.set(0, 0, -1);
		distView = farClip;
	}
	
	public void initSide(Vec3 a, Vec3 b) {
		a.cross(b, normalView);
		normalView.normalizeInPlace();
		distView = 0;
	}
	
	public void setWorldSpace(Mat4 rotation, Vec3 position) {
		// I can only assume this is correct. only time will tell
		rotation.transform(normalView, normalWorld);
		distWorld = distView + normalWorld.dot(position);
	}
	
	public boolean isOutside(Vec3 v) {
		return normalWorld.dot(v) > distWorld;
	}
	
	public boolean isOutside(AABB box, Vec3 tmp) {
		tmp.x = normalWorld.x < 0 ? box.max.x : box.min.x;
		tmp.y = normalWorld.y < 0 ? box.max.y : box.min.y;
		tmp.z = normalWorld.z < 0 ? box.max.z : box.min.z;
		return isOutside(tmp);
	}
}
