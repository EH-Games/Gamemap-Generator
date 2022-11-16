package gamemap.world;

import static org.lwjgl.opengl.GL11.*;

import com.ehgames.util.AABB;
import com.ehgames.util.GL;
import com.ehgames.util.Mat4;
import com.ehgames.util.Vec3;

public class RenderState {
	static boolean			drawCulling = false;
	
	AABB					globalBounds;
	
	public final GL			gl;
	public final World		world;
	public final boolean	transparent;
	final Camera			camera;
	public WorldObject		object;

	/**
	 * Variable that plugins can use to pass data to renderers.<br>
	 * Do so by setting the variable using the {@link #World3d.userSetup}
	 */
	public Object userData;

	RenderState(GL gl, boolean transparent, Camera camera, World world) {
		this.gl = gl;
		this.transparent = transparent;
		this.camera = camera;
		this.world = world;
	}
	
	public void getCameraPerspective(Mat4 out) {
		out.set(camera.projection);
	}
	
	public void getCameraView(Mat4 out) {
		out.set(camera.view);
	}
	
	public boolean isBackgroundThread() {
		return camera.background;
	}
	
	public boolean isTopDown() {
		return !camera.perspective;
	}
	
	private void drawXAt(float x, float y, float scale) {
		x = 50 + (x - globalBounds.min.x) * scale;
		y = 50 + (globalBounds.max.y - y) * scale;
		gl.vertex2f(x - 3, y - 3);
		gl.vertex2f(x + 3, y + 3);
		gl.vertex2f(x - 3, y + 3);
		gl.vertex2f(x + 3, y - 3);
	}
	
	//*
	private void drawPointAt(Vec3 v, float scale) {
		float x = 50 + (v.x - globalBounds.min.x) * scale;
		float y = 50 + (globalBounds.max.y - v.y) * scale;
		gl.vertex2f(x, y);
	}
	//*/
	
	private void drawXAt(Vec3 v, float scale) {
		drawXAt(v.x, v.y, scale);
	}
	
	private void drawCullingInfoImpl() {
		// setup
		gl.disable(GL.TEXTURE_2D);
		gl.pushMatrix();
		gl.loadIdentity();
		gl.matrixMode(GL_PROJECTION);
		gl.pushMatrix();
		gl.loadIdentity();
		gl.ortho(0, 1280, 720, 0, -1, 1);
		
		gl.begin(GL.LINES);
		// boundaries of all content
		float left = 50;
		float top = 50;
		float height = globalBounds.getHeight();
		float width = globalBounds.getWidth();
		float scale = 500 / height;
		float btm = top + height * scale;
		float right = left + width * scale;
		gl.color3f(1, 0, 0);
		gl.vertex2f(left, top);
		gl.vertex2f(left, btm);
		gl.vertex2f(left, btm);
		gl.vertex2f(right, btm);
		gl.vertex2f(right, btm);
		gl.vertex2f(right, top);
		gl.vertex2f(right, top);
		gl.vertex2f(left, top);
		
		// boundaries of frustum
		gl.color3f(0, 1, 0);
		width = camera.bounds.getWidth();
		height = camera.bounds.getHeight();
		left = 50 + (camera.bounds.min.x - globalBounds.min.x) * scale;
		right = left + width * scale;
		top = 50 + (globalBounds.max.y - camera.bounds.max.y) * scale;
		btm = top + height * scale;
		gl.vertex2f(left, top);
		gl.vertex2f(left, btm);
		gl.vertex2f(left, btm);
		gl.vertex2f(right, btm);
		gl.vertex2f(right, btm);
		gl.vertex2f(right, top);
		gl.vertex2f(right, top);
		gl.vertex2f(left, top);

		// frustum itself
		if(camera.perspective) {
			glColor3f(1, 1, 0);
			drawPointAt(camera.fpWorld[7], scale);
			drawPointAt(camera.fpWorld[4], scale);
			drawPointAt(camera.fpWorld[0], scale);
			drawPointAt(camera.fpWorld[4], scale);
			drawPointAt(camera.fpWorld[3], scale);
			drawPointAt(camera.fpWorld[7], scale);
			drawPointAt(camera.fpWorld[1], scale);
			drawPointAt(camera.fpWorld[5], scale);
			drawPointAt(camera.fpWorld[2], scale);
			drawPointAt(camera.fpWorld[6], scale);
			drawPointAt(camera.fpWorld[6], scale);
			drawPointAt(camera.fpWorld[7], scale);
			drawPointAt(camera.fpWorld[4], scale);
			drawPointAt(camera.fpWorld[5], scale);
			drawPointAt(camera.fpWorld[5], scale);
			drawPointAt(camera.fpWorld[6], scale);
		}
		
		// camera position
		gl.color3f(1, 1, 1);
		drawXAt(camera.position, scale);
		gl.end();
		
		// cleanup
		gl.popMatrix();
		gl.matrixMode(GL.MODELVIEW);
		gl.popMatrix();
		gl.enable(GL.TEXTURE_2D);
	}
	
	public void applyCameraFixedFunc() {
		gl.useProgram(0);
		
		gl.matrixMode(GL.PROJECTION);
		gl.loadMatrixf(camera.projection);
		gl.matrixMode(GL.MODELVIEW);
		gl.loadMatrixf(camera.view);

		// makes isometric
//		gl.rotatef(-45, 1, 0, 0);
//		gl.rotatef(45, 0, 0, 1);
		
		gl.enable(GL.CULL_FACE);
		gl.enable(GL.DEPTH_TEST);
		gl.depthFunc(GL.LEQUAL);
		gl.clearDepth(1);
		gl.enable(GL.ALPHA_TEST);
		gl.alphaFunc(GL.GREATER, 0);
		gl.activeTexture(GL.TEXTURE0 + 1);
		gl.disable(GL.TEXTURE_2D);
		gl.activeTexture(GL.TEXTURE0);
		gl.color3f(1, 1, 1);
//		gl.polygonMode(GL.FRONT, GL.FILL);
//		gl.polygonMode(GL.BACK, GL.LINE);
		
		//drawCulling = false;
		if(camera.perspective && drawCulling) {
			drawCullingInfoImpl();
		}
	}
	
	public void drawCullingInfo() {
		gl.useProgram(0);
		gl.activeTexture(GL.TEXTURE0 + 1);
		gl.disable(GL.TEXTURE_2D);
		gl.activeTexture(GL.TEXTURE0);
		gl.disable(GL.TEXTURE_2D);
		drawCullingInfoImpl();
		gl.enable(GL.TEXTURE_2D);
	}
}
