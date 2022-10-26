package gamemap;

import java.util.Map;

public class Model {
	private int vbo, ibo;
	
	public boolean isLoaded() {
		return false;
	}
	
	public boolean hasTransparency() {
		return false;
	}
	
	public void render(boolean transparent, Map<String, Texture> textures) {
		
	}
}
