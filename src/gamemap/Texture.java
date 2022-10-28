package gamemap;

import static org.lwjgl.opengl.GL11.*;

public class Texture {
	private int texture;
	
	private boolean transparent;
	
	/**
	 * @return A flag indicating if this texture either has translucency
	 * or uses linear filtering while having any non-opaque pixels.
	 */
	public boolean isTransparent() {
		return transparent;
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, texture);
	}
}
