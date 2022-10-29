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
	
	public void applyCameraFixedFunc() {
		glMatrixMode(GL_PROJECTION);
		glLoadMatrix(camera.projection.m);
		glMatrixMode(GL_MODELVIEW);
		glLoadMatrix(camera.view.m);
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, 0);
	}
}
