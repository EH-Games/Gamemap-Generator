package gamemap;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

class GMCanvas extends AWTGLCanvas {
	private int width;
	private int height;
	private FloatBuffer projection = BufferUtils.createFloatBuffer(16);
	public boolean activeRendering = true;
	
	GMCanvas() throws LWJGLException {
		
		// TODO need to check for resizes and screen changes to trigger a repaint
	}
	
	@Override
	protected void initGL() {
		glClearColor(0, 0, 0, 0);
		glClearDepth(1);
		
		System.out.println("Initialized");
	}
	
	private void handlePotentialResize() {
		int newWidth = getWidth();
		int newHeight = getHeight();
		if(width != newWidth || height != newHeight) {
			width = newWidth;
			height = newHeight;
			System.out.println("Window resized to " + width + " x " + height);
			
			glViewport(0, 0, width, height);
			
			rebuildPerspective();
		}
	}
	
	private void rebuildPerspective() {
		double h = height == 0 ? 1 : height;
		double aspect = width / h;
		
		// I borrowed all this code from another project of mine
		double fov = 120;
		float nearClip = 0.9f;
		float farClip = 2000;
		
		double f = Math.toRadians(fov * aspect / 2);
		projection.put(0, (float) (f / aspect));
		projection.put(5, (float) f);
		float zRange = (nearClip - farClip);
		projection.put(10, (nearClip + farClip) / zRange);
		projection.put(11, -1);
		projection.put(14, (2 * nearClip * farClip) / zRange);
		
		projection.put(0, (float) (f / aspect));
		projection.put(5, (float) f);
	}
	
	@Override
	protected void paintGL() {
		handlePotentialResize();
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		Viewer viewer = Gamemap.viewer;
		
		// camera projection
		glMatrixMode(GL_PROJECTION);
		if(viewer.orthogonal) {
			glLoadIdentity();
			glOrtho(width / -100.0f, width / 100.0f, height / -100.0f, height / 100.0f, -500, 500);
		} else {
			glLoadMatrix(projection);
		}
		
		// camera view
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		float z = -viewer.position.z;
		if(!viewer.orthogonal) z -= 10; // temporary until I have a better default position and elevation control
		glTranslatef(-viewer.position.x, -viewer.position.y, z);
		float scale = viewer.getScale();
		glScalef(scale, scale, scale);
		
		glDisable(GL_DEPTH_TEST);

		drawTestObjectAt(0, 0, 0);
		drawTestObjectAt(4, 0, 0);
		drawTestObjectAt(0, 4, 0);
		drawTestObjectAt(-4, -4, 0);
		
		try {
			swapBuffers();
		} catch(LWJGLException e) {
			e.printStackTrace();
		}
		
		if(activeRendering) {
			repaint();
		}
	}

	public static void drawTestObjectAt(float x, float y, float z) {
		glPushMatrix();
		glTranslatef(x, y, z);
		drawTestObject();
		glPopMatrix();
	}
	
	public static void drawTestObject() {
		glBegin(GL_TRIANGLES);
		glColor3f(1, 0, 0);
		glVertex2f(0, 0.5f);
		glColor3f(0, 1, 0);
		glVertex2f(-0.5f, -0.5f);
		glColor3f(0, 0, 1);
		glVertex2f(0.5f, -0.5f);
		glEnd();
	}
}