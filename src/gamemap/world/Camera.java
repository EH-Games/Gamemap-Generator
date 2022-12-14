package gamemap.world;

import java.util.LinkedList;

import com.ehgames.util.AABB;
import com.ehgames.util.Mat4;
import com.ehgames.util.Vec3;

import gamemap.ExportTask;

public class Camera {
	private static final float					ORTHO_Z_VAL		= 2000;
	private static final int					MAX_CAMERAS		= Integer.SIZE - 1;
	private static final LinkedList<Integer>	FREE_CAMERAS	= new LinkedList<>();

	static {
		for(int i = 0; i < MAX_CAMERAS; i++) {
			FREE_CAMERAS.add(1 << i);
		}
	}

	final Vec3			position		= new Vec3();
	final AABB			bounds			= new AABB();

	float				nearClip		= 0.1f;
	float				farClip			= 1000;
	float				nearClipSq		= 0.01f;
	float				farClipSq		= 1000_000;
	boolean				perspective		= false;
	float				fovY			= (float) Math.toRadians(80);

	// the 4 sides + far
	FrustumPlane[]		frustumPlanes	= new FrustumPlane[5];
	/** frustum points in view space */
	private Vec3[]		frustumPoints	= new Vec3[8];
	/** frustum points in world space */
	Vec3[]				fpWorld			= new Vec3[8];
	Mat4				projection		= new Mat4();
	Mat4				view			= new Mat4();
	Mat4				rotation		= new Mat4();
	Mat4				invRotation		= new Mat4();

	double				pixelsPerUnit	= 1;
	float				halfWidth;
	float				halfHeight;
	int					width;
	int					height;
	private boolean		rebuildBounds	= true;

	private final Vec3	tempVec			= new Vec3();

	/** internal flag used for marking objects as visible */
	int					cameraFlag;

	/** a single bit indicating which area should be rendered */
	int					areaFlag;

	/** a single bit indicating which layer should be rendered */
	int					layerFlag;

	int					time;

	/** background threads block until all relevant object renderers are loaded before rendering */
	final boolean		background;

	public Camera() {
		this(false);
	}
	
	public Camera(boolean background) {
		this.background = background;
		Integer flag = FREE_CAMERAS.poll();
		if(flag != null) cameraFlag = flag;
		for(int i = 0; i < frustumPoints.length; i++) {
			frustumPoints[i] = new Vec3();
			fpWorld[i] = new Vec3();
		}
		for(int i = 0; i < frustumPlanes.length; i++) {
			frustumPlanes[i] = new FrustumPlane();
		}
	}

	public void onWorldChange(World world) {
		position.set(world.initialPos);
		int area = world.defaultArea;
		if(area >= world.areaCount) area = 0;
		areaFlag = 1 << world.defaultArea;
		resetRotation();
	}
	
	private void updateProjection() {
		if(perspective) {
			float aspect = width / (float) height;
			projection.setPerspective(fovY, aspect, nearClip, farClip);
			
			buildFrustum(aspect);
		} else {
			bounds.min.z = -ORTHO_Z_VAL;
			bounds.max.z = ORTHO_Z_VAL;
		}
		rebuildBounds = true;
		calculateBounds();
	}

	public void onViewportResize(int width, int height) {
		if(height == 0) height = 1;
		this.width = width;
		this.height = height;
		halfWidth = width * 0.5f;
		halfHeight = height * 0.5f;
		updateProjection();
	}
	
	private void buildFrustum(float aspect) {
		float f = (float) Math.tan(fovY / 2); 
		float yNear = f * nearClip;
		float yFar = f * farClip;
		float xNear = yNear * aspect;
		float xFar = yFar * aspect;
		frustumPoints[0].set(-xNear, yNear, -nearClip);
		frustumPoints[1].set(-xNear, -yNear, -nearClip);
		frustumPoints[2].set(xNear, -yNear, -nearClip);
		frustumPoints[3].set(xNear, yNear, -nearClip);
		frustumPoints[4].set(-xFar, yFar, -farClip);
		frustumPoints[5].set(-xFar, -yFar, -farClip);
		frustumPoints[6].set(xFar, -yFar, -farClip);
		frustumPoints[7].set(xFar, yFar, -farClip);
		
		frustumPlanes[0].initSide(frustumPoints[4], frustumPoints[5]); // left
		frustumPlanes[1].initSide(frustumPoints[6], frustumPoints[7]); // right
		frustumPlanes[2].initSide(frustumPoints[7], frustumPoints[4]); // top
		frustumPlanes[3].initSide(frustumPoints[5], frustumPoints[6]); // bottom
		frustumPlanes[4].initFarClip(farClip);
	}
	
	public Vec3 getViewAxis(int axis, Vec3 out) {
		return view.getAxis(axis, out);
	}
	
	public void resetRotation() {
		rotation.setIdentity();
		invRotation.setIdentity();
		
		rotateX(Math.toRadians(80));
	}
	
	// having rotation be the same as inverse rotation to fix culling makes no sense
	// rotation transforms the view frustum into world space
	// inverse rotation transforms models into view space
	// they should be opposites to my knowledge -EH (11/1/22)
	
	public void rotateX(double rot) {
		Mat4 tmp = new Mat4();
		tmp.rotateX(-rot);
		tmp.mult(rotation, rotation);
		tmp.rotateX(-rot);
		tmp.mult(invRotation, invRotation);

		rebuildBounds = true;
		calculateBounds();
	}
	
	public void rotateY(double rot) {
		// premultiplying with z gives a much nicer effect than postmultiplying with y
		Mat4 tmp = new Mat4();
		tmp.rotateZ(-rot);
		rotation.mult(tmp, rotation);
		tmp.rotateZ(-rot);
		invRotation.mult(tmp, invRotation);

		rebuildBounds = true;
		calculateBounds();
	}
	
	public boolean isPerspective() {
		return perspective;
	}
	
	public void setPerspective(boolean perspective) {
		this.perspective = perspective;
		updateProjection();
	}
	
	public void move(Vec3 v) {
		move(v.x, v.y, v.z);
	}
	
	public void move(float x, float y, float z) {
		position.x += x;
		position.y += y;
		position.z += z;
		rebuildBounds = true;
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
		rebuildBounds = true;
		calculateBounds();
	}

	/** gets the scale in pixels per unit for when this camera uses an orthographic projection */
	public double getScale() {
		return pixelsPerUnit;
	}
	
	public Vec3 getPosition() {
		return position;
	}

	public void copyWorldProperties(Camera camera) {
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
	
	public void setupFromExporter(ExportTask task) {
		bounds.min.set(task.minX, task.minY, -ORTHO_Z_VAL);
		bounds.max.set(task.maxX, task.maxY, ORTHO_Z_VAL);

		float xHalf = (bounds.max.x - bounds.min.x) / 2;
		float yHalf = (bounds.max.y - bounds.min.y) / 2;
		
		position.x = bounds.min.x + xHalf;
		position.y = bounds.min.y + yHalf;
		
		projection.setOrtho(-xHalf, xHalf, -yHalf, yHalf, -ORTHO_Z_VAL, ORTHO_Z_VAL);
		view.setTranslation(-position.x, -position.y, 0);
	}
	
	void calculateBounds() {
		if(perspective) {
			bounds.prepForBuild();
			for(int i = 0; i < 8; i++) {
				Vec3 v = fpWorld[i];
				rotation.transform(frustumPoints[i], v);
				v.addInPlace(position);
				bounds.add(v);
			}
			for(FrustumPlane plane : frustumPlanes) {
				plane.setWorldSpace(rotation, position);
			}
			view.setTranslation(-position.x, -position.y, -position.z);
			invRotation.mult(view, view);
		} else {
			// min and max z assumed to already be set to
			// user-defined minimum and maximum values
			// if width is 640 and the scale is 2 pixels per unit,
			// then the bounds should be 360 units wide, hence division
			final float xHalf = (float) (halfWidth / pixelsPerUnit);
			final float yHalf = (float) (halfHeight / pixelsPerUnit);
			bounds.min.x = position.x - xHalf;
			bounds.max.x = position.x + xHalf;
			bounds.max.y = position.y + yHalf;
			bounds.min.y = position.y - yHalf;
			
			projection.setOrtho(-xHalf, xHalf, -yHalf, yHalf, -ORTHO_Z_VAL, ORTHO_Z_VAL);
			view.setTranslation(-position.x, -position.y, 0);
		}
	}
	
	boolean occluded(AABB box) {
		if(!bounds.intersects(box)) return true;
		for(FrustumPlane plane : frustumPlanes) {
			if(plane.isOutside(box, tempVec)) return true;
		}
		return false;
	}
	
	boolean testVisibility(AABB box) {
		return !occluded(box);
	}
	
	@Override
	public void finalize() {
		if(cameraFlag != 0) {
			FREE_CAMERAS.add(cameraFlag);
		}
	}
}
