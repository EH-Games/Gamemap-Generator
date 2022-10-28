package gamemap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Model {
	public static final int	POLYGON_TYPE_TRIANGLES		= GL_TRIANGLES;
	public static final int	POLYGON_TYPE_TRIANGLE_STRIP	= GL_TRIANGLE_STRIP;

	public static final int	INDEX_TYPE_NONE				= 0;
	public static final int	INDEX_TYPE_BYTE				= GL_UNSIGNED_BYTE;
	public static final int	INDEX_TYPE_SHORT			= GL_UNSIGNED_SHORT;
	public static final int	INDEX_TYPE_INT				= GL_UNSIGNED_INT;
	
	public class VertexAttrib {
		
	}

	public class DrawCall {
		public String	textureName;
		/**
		 * For indexed meshes, the byte offset to the first index in the index buffer.<br>
		 * For raw arrays, the index of the first vertex.
		 */
		public int		offset;
		public int		vertexCount;
		public int		polygonType	= POLYGON_TYPE_TRIANGLES;
		public int		indexType	= INDEX_TYPE_NONE;

		void draw() {
			if(indexType != 0) {
				glDrawElements(polygonType, vertexCount, indexType, offset);
			} else {
				glDrawArrays(polygonType, offset, vertexCount);
			}
		}
	}

	private int					vbo, ibo;
	public final List<DrawCall>	drawCalls	= new ArrayList<>();

	public boolean isLoaded() {
		return false;
	}

	public boolean hasTransparency(Map<String, Texture> textures) {
		for(DrawCall drawCall : drawCalls) {
			if(drawCall.textureName != null) {
				Texture tex = textures.get(drawCall.textureName);
				if(tex != null && tex.isTransparent()) return true;
			}
		}
		return false;
	}
	
	public void render(boolean transparent, Map<String, Texture> textures) {
		if(ibo != 0) {
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		}
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		// TODO borrow buffer binding code from other current projects perhaps
		
		for(DrawCall drawCall : drawCalls) {
			boolean trans = false;
			Texture tex = null;
			if(drawCall.textureName != null) {
				tex = textures.get(drawCall.textureName);
				if(tex != null) {
					trans = tex.isTransparent();
				}
			}
			if(trans == transparent) {
				if(tex != null) tex.bind();
				drawCall.draw();
			}
		}
	}
}
