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
		x = 50 + (camera.position.x - globalBounds.min.x) * scale;
		y = 50 + (globalBounds.max.y - camera.position.y) * scale;
		glVertex2f(x - 3, y - 3);
		glVertex2f(x + 3, y + 3);
		glVertex2f(x - 3, y + 3);
		glVertex2f(x + 3, y - 3);
	}
	
	private void drawXAt(Vec3 v, float scale) {
		drawXAt(v.x, v.y, scale);
	}
	
	private void drawCullingInfo() {
		// setup
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		glLoadIdentity();
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, 1280, 720, 0, -1, 1);
		
		
		glBegin(GL_LINES);
		// boundaries of all content
		float left = 50;
		float top = 50;
		float height = globalBounds.getHeight();
		float width = globalBounds.getWidth();
		float scale = 500 / height;
		float btm = top + height * scale;
		float right = left + width * scale;
		glColor3f(1, 0, 0);
		glVertex2f(left, top);
		glVertex2f(left, btm);
		glVertex2f(left, btm);
		glVertex2f(right, btm);
		glVertex2f(right, btm);
		glVertex2f(right, top);
		glVertex2f(right, top);
		glVertex2f(left, top);
		
		// boundaries of frustum
		glColor3f(0, 1, 0);
		width = camera.bounds.getWidth();
		height = camera.bounds.getHeight();
		left = 50 + (camera.bounds.min.x - globalBounds.min.x) * scale;
		right = left + width * scale;
		top = 50 + (globalBounds.max.y - camera.bounds.max.y) * scale;
		btm = top + height * scale;
		glVertex2f(left, top);
		glVertex2f(left, btm);
		glVertex2f(left, btm);
		glVertex2f(right, btm);
		glVertex2f(right, btm);
		glVertex2f(right, top);
		glVertex2f(right, top);
		glVertex2f(left, top);

		// frustum points
		/*
		glColor3f(1, 1, 0);
		for(Vec3 v : camera.frustumPoints) {
			drawXAt(v, scale);
		}
		//*/
		
		// camera position
		glColor3f(1, 1, 1);
		drawXAt(camera.position, scale);
		glEnd();
		
		// cleanup
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
		glEnable(GL_TEXTURE_2D);
	}
	
	public void applyCameraFixedFunc() {
		gl.useProgram(0);
		
		glMatrixMode(GL_PROJECTION);
		glLoadMatrix(camera.projection.m);
		glMatrixMode(GL_MODELVIEW);
		glLoadMatrix(camera.view.m);

		// makes isometric
//		glRotatef(-45, 1, 0, 0);
//		glRotatef(45, 0, 0, 1);
		
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glClearDepth(1);
		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, 0);
		gl.activeTexture(GL.TEXTURE0);
		//glPolygonMode(GL_FRONT, GL_FILL);
		//glPolygonMode(GL_BACK, GL_LINE);
		
		//drawCulling = false;
		if(camera.perspective && drawCulling) {
			drawCullingInfo();
		}
	}
}
