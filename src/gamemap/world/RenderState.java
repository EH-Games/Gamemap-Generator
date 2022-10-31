package gamemap.world;

import static org.lwjgl.opengl.GL11.*;

import com.ehgames.util.GL;
import com.ehgames.util.Mat4;

public class RenderState {
	public final GL gl;
	public final boolean transparent;
	final Camera camera;
	public WorldObject object;
	
	RenderState(GL gl, boolean transparent, Camera camera) {
		this.gl = gl;
		this.transparent = transparent;
		this.camera = camera;
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
	
	public void applyCameraFixedFunc() {
		glMatrixMode(GL_PROJECTION);
		glLoadMatrix(camera.projection.m);
		glMatrixMode(GL_MODELVIEW);
		glLoadMatrix(camera.view.m);

		// makes isometric
//		glRotatef(-45, 1, 0, 0);
//		glRotatef(45, 0, 0, 1);
		
		glEnable(GL_CULL_FACE);
		//glDisable(GL_CULL_FACE);
		//glCullFace(GL_BACK);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glClearDepth(1);
		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, 0);
		//glPolygonMode(GL_FRONT, GL_FILL);
		//glPolygonMode(GL_BACK, GL_LINE);
	}
}
