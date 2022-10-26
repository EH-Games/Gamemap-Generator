package gamemap;

import static org.lwjgl.opengl.GL11.*;

public class Texture {
	private int texture;
	
	private boolean transparent;
	
	
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, texture);
	}
}
