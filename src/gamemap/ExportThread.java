package gamemap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.SharedDrawable;

import com.ehgames.util.GL;
import com.ehgames.util.LWJGL;

import gamemap.world.Camera;
import gamemap.world.World;

public class ExportThread extends Thread {
	static Queue<ExportTask>	taskList	= new LinkedList<>();
	private static int			threadNum;

	private SharedDrawable		drawable;
	private final World			world;
	private final Camera		camera;
	private ExportTask			task;

	ExportThread(Drawable drawable, World world) throws LWJGLException {
		super("Export Thread " + ++threadNum);
		this.drawable = new SharedDrawable(drawable);
		this.world = world;
		camera = new Camera();
		camera.copyWorldProperties(Gamemap.camera);
	}
	
	private void checkGLErrors() {
		while(true) {
			int err = GL11.glGetError();
			if(err == GL11.GL_NO_ERROR) break;
			switch(err) {
				case GL11.GL_INVALID_OPERATION:
					System.err.println("GL_INVALID_OPERATION");
					break;
				default:
					System.err.printf("OpenGL error 0x%04X\n", err);
					break;
			}
		}
	}
	
	private void performSingleTask(GL gl) {
		camera.setupFromExporter(task);
		
		int framebuffer = GL45.glCreateFramebuffers();
		int colorbuffer = GL45.glCreateTextures(GL11.GL_TEXTURE_2D);
		int depthbuffer = GL45.glCreateTextures(GL11.GL_TEXTURE_2D);
		GL45.glTextureStorage2D(colorbuffer, 1, GL11.GL_RGBA8, task.pixelWidth, task.pixelHeight);
		GL45.glTextureParameteri(colorbuffer, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL45.glTextureParameteri(colorbuffer, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		GL45.glTextureStorage2D(depthbuffer, 1, GL30.GL_DEPTH24_STENCIL8, task.pixelWidth, task.pixelHeight);
		GL45.glTextureParameteri(depthbuffer, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL45.glTextureParameteri(depthbuffer, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		GL45.glNamedFramebufferTexture(framebuffer, GL30.GL_COLOR_ATTACHMENT0, colorbuffer, 0);
		checkGLErrors();
		GL45.glNamedFramebufferTexture(framebuffer, GL30.GL_DEPTH_STENCIL_ATTACHMENT, depthbuffer, 0);
		checkGLErrors();
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, framebuffer);
		checkGLErrors();
		
		GL11.glViewport(0, 0, task.pixelWidth, task.pixelHeight);
		GL11.glClearColor(world.backgroundColor.x, world.backgroundColor.y, world.backgroundColor.z, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		world.render(camera, gl);
		GL11.glFinish();
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		
		ByteBuffer tmp = ByteBuffer.allocateDirect(task.pixelCount * Integer.BYTES);
		tmp.order(ByteOrder.LITTLE_ENDIAN);
		int[] pixels = new int[task.pixelCount];
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, framebuffer);
		GL11.glReadPixels(0, 0, task.pixelWidth, task.pixelHeight,
				GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, tmp);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
		GL11.glFinish();
		
		GL45.glNamedFramebufferTexture(framebuffer, GL30.GL_DEPTH_STENCIL_ATTACHMENT, 0, 0);
		GL45.glNamedFramebufferTexture(framebuffer, GL30.GL_COLOR_ATTACHMENT0, 0, 0);
		GL11.glDeleteTextures(depthbuffer);
		GL11.glDeleteTextures(colorbuffer);
		GL30.glDeleteFramebuffers(framebuffer);

		checkGLErrors();
		
		// flipping because OpenGL does bottom up buffers
		IntBuffer itmp = tmp.asIntBuffer();
		for(int y = 1; y <= task.pixelHeight; y++) {
			int off = task.pixelCount - task.pixelWidth * y;
			itmp.get(pixels, off, task.pixelWidth);
		}
		//tmp.asIntBuffer().get(task.pixels);
		task.pixels = pixels;
	}

	private void performTasks() throws LWJGLException {
		GL gl = new LWJGL();
		drawable.makeCurrent();

		GL11.glClearColor(0, 0, 0, 0);
		GL11.glClearDepth(1);
		//GL43.glDebugMessageCallback(new KHRDebugCallback());
		
		while(true) {
			synchronized(taskList) {
				task = taskList.poll();
			}
			if(task == null) break;

			performSingleTask(gl);
		}
		
		drawable.releaseContext();
	}
	
	@Override
	public void run() {
		try {
			performTasks();
		} catch(LWJGLException e) {
			e.printStackTrace();
		}
	}
}
