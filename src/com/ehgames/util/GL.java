package com.ehgames.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

//import org.lwjgl.opengl.GL11;

public interface GL {
	public static final int	TRIANGLES		= 4;	// GL11.GL_TRIANGLES;
	public static final int	TRIANGLE_STRIP	= 5;	// GL11.GL_TRIANGLE_STRIP;

	public boolean isCoreProfile();
	
	public void begin(int mode);
	
	public void end();
	
	public void vertex2s(short x, short y);
	
	public void vertex2i(int x, int y);
	
	public void vertex2f(float x, float y);
	
	public void vertex3f(float x, float y, float z);
	
	public void texCoord2f(float u, float v);
	
	public void normal3f(float x, float y, float z);

	public void color3f(float red, float green, float blue);

	public void color4f(float red, float green, float blue, float alpha);
	
	public void color3ub(byte red, byte green, byte blue);
	
	public void color4ub(byte red, byte green, byte blue, byte alpha);

	public void vertexAttrib1f(int index, float v0);
	
	public void vertexAttrib2f(int index, float v0, float v1);
	
	public void vertexAttrib3f(int index, float v0, float v1, float v2);
	
	public void vertexAttrib4f(int index, float v0, float v1, float v2, float v3);
	
	public void vertexAttrib2s(int index, short v0, short v1);
	
	public void vertexPointer(int size, int type, int stride, ByteBuffer pointer);
	
	public void vertexPointer(int size, int type, int stride, int offset);
	
	public void texCoordPointer(int size, int type, int stride, ByteBuffer pointer);
	
	public void texCoordPointer(int size, int type, int stride, int offset);
	
	public void colorPointer(int size, int type, int stride, ByteBuffer pointer);
	
	public void colorPointer(int size, int type, int stride, int offset);
	
	public void normalPointer(int type, int stride, ByteBuffer pointer);
	
	public void normalPointer(int type, int stride, int offset);
	
	public void vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, ByteBuffer pointer);
	
	public void vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, int offset);
	
	public void bindAttribLocation(int program, int index, String name);
	
	public int getAttribLocation(int program, String name);
	
	public void pushMatrix();
	
	public void popMatrix();
	
	public void translatef(float x, float y, float z);
	
	public void multMatrixf(FloatBuffer matrix);
	
	public void loadMatrixf(FloatBuffer matrix);
	
	/**
	 * 
	 * @param mode Specifies what kind of primitives to render. Symbolic constants POINTS, LINE_STRIP, LINE_LOOP, LINES, TRIANGLE_STRIP, TRIANGLE_FAN, TRIANGLES, QUAD_STRIP, QUADS, and POLYGON are accepted. 
	 * @param first Specifies the starting index in the enabled arrays.
	 * @param count Specifies the number of indices to be rendered.
	 */
	public void drawArrays(int mode, int first, int count);
	
	public void drawElements(int mode, int count, int type, ByteBuffer indices);
	
	public void drawElements(int mode, int count, int type, int offset);
	
	/**
	 * 
	 * @param target Specifies the target to which the texture is bound. Must be either TEXTURE_1D, TEXTURE_2D, TEXTURE_3D, or TEXTURE_CUBE_MAP.
	 * @param texture Specifies the name of a texture.
	 */
	public void bindTexture(int target, int texture);
	
	public int genTexture();
	
	public void texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer data);
	
	public void compressedTexImage2D(int target, int level, int internalFormat, int width, int height, int border, ByteBuffer data);
	
	public void texParameterf(int target, int pname, int param);
	
	public void texParameteri(int target, int pname, int param);
	
	public void deleteTexture(int texture);
	
	public int genBuffer();
	
	public void deleteBuffer(int buffer);
	
	public void bindBuffer(int target, int buffer);
	
	public void bufferData(int target, ByteBuffer data, int usage);
	
	public void bufferSubData(int target, int offset, ByteBuffer data);
	
	public int createShader(int shaderType);
	
	public void shaderSource(int shader, String source);

	public void attachShader(int program, int shader);

	public void detachShader(int program, int shader);
	
	public void compileShader(int shader);
	
	public String getShaderInfoLog(int shader);
	
	public void deleteShader(int shader);
	
	public int createProgram();
	
	public void linkProgram(int program);
	
	public void validateProgram(int program);
	
	public void useProgram(int program);
	
	public void deleteProgram(int program);
}