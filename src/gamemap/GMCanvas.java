package gamemap;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;

import com.ehgames.util.GL;
import com.ehgames.util.LWJGL;
import com.ehgames.util.Vec3;

import gamemap.world.World;
import gamemap.world.World3d;

import static org.lwjgl.opengl.GL11.*;

class GMCanvas extends AWTGLCanvas {
	private int			width;
	private int			height;
	public boolean		activeRendering	= true;
	private GL			gl;
	private World		lastWorld;

	GMCanvas() throws LWJGLException {}
	
	@Override
	protected void initGL() {
		gl = new LWJGL();
		
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
			//System.out.println("Window resized to " + width + " x " + height);
			
			glViewport(0, 0, width, height);

			Gamemap.camera.onViewportResize(width, height);
		}
	}
	
	@Override
	protected void paintGL() {
		handlePotentialResize();
		if(activeRendering) {
			Gamemap.handleKeysActive();
		}
		
		World world = Gamemap.activeWorld;
		if(world != null) {
			Vec3 bgColor = world.backgroundColor;
			if(Gamemap.camera.isPerspective() && world.getClass() == World3d.class) {
				bgColor = ((World3d) world).skyColor;
			}
			glClearColor(bgColor.x, bgColor.y, bgColor.z, 1);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			world.render(Gamemap.camera, gl);
			if(world != lastWorld) {
				if(lastWorld != null) {
					lastWorld.destroyResources(gl);
				}
				lastWorld = world;
			}
		} else {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		}
		
		try {
			swapBuffers();
		} catch(LWJGLException e) {
			e.printStackTrace();
		}
		
		if(activeRendering) {
			repaint();
		}
	}
}