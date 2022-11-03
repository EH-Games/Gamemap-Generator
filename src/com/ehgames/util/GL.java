package com.ehgames.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public interface GL {
	public static final int	POINTS						= 0;		// GL11.GL_POINTS;
	public static final int	LINES						= 1;		// GL11.GL_LINES;
	public static final int LINE_LOOP					= 2;		// GL11.GL_LINE_LOOP;
	public static final int	LINE_STRIP					= 3;		// GL11.GL_LINE_STRIP;
	public static final int	TRIANGLES					= 4;		// GL11.GL_TRIANGLES;
	public static final int	TRIANGLE_STRIP				= 5;		// GL11.GL_TRIANGLE_STRIP;
	
	public static final int	CULL_FACE					= 0xB44;
	
	public static final int	MODELVIEW					= 0x1700;	// GL11.GL_MODELVIEW;
	public static final int	PROJECTION					= 0x1701;	// GL11.GL_PROJECTION;
	public static final int	TEXTURE						= 0x1702;	// GL11.GL_TEXTURE;

	public static final int	TEXTURE_MAG_FILTER			= 0x2800;	// GL11.GL_TEXTURE_MAG_FILTER;
	public static final int	TEXTURE_MIN_FILTER			= 0x2801;	// GL11.GL_TEXTURE_MIN_FILTER;
	
	public static final int	NEAREST						= 0x2600;	// GL11.GL_NEAREST;
	public static final int	LINEAR						= 0x2601;	// GL11.GL_LINEAR;

	public static final int	TEXTURE_2D					= 0xDE1;	// GL11.GL_TEXTURE_2D;
	public static final int	TEXTURE0					= 0x84C0;	// GL13.GL_TEXTURE0;

	public static final int	RGB							= 0x1907;	// GL11.GL_RGB;
	public static final int	RGBA						= 0x1908;	// GL11.GL_RGBA;
	public static final int	RGBA8						= 0x8058;	// GL11.GL_RGBA8;
	public static final int	BGR							= 0x80E0;	// GL12.GL_BGR;
	public static final int	BGRA						= 0x80E1;	// GL12.GL_BGRA;

	public static final int	ARRAY_BUFFER				= 0x8892;	// GL15.GL_ARRAY_BUFFER;
	public static final int	ELEMENT_ARRAY_BUFFER		= 0x8893;	// GL15.GL_ELEMENT_ARRAY_BUFFER;
	
	public static final int VERTEX_ARRAY				= 0x8074;	// GL11.GL_VERTEX_ARRAY;
	public static final int NORMAL_ARRAY				= 0x8075;	// GL11.GL_NORMAL_ARRAY;
	public static final int COLOR_ARRAY					= 0x8076;	// GL11.GL_COLOR_ARRAY;
	public static final int TEXTURE_COORD_ARRAY			= 0x8078;	// GL11.GL_TEXTURE_COORD_ARRAY;

	public static final int HALF_FLOAT					= 0x140B;	// GL30.GL_HALF_FLOAT;
	public static final int	FLOAT						= 0x1406;	// GL11.GL_FLOAT;
	public static final int	DOUBLE						= 0x140A;	// GL11.GL_DOUBLE;
	public static final int	BYTE						= 0x1400;	// GL11.GL_BYTE;
	public static final int	UNSIGNED_BYTE				= 0x1401;	// GL11.GL_UNSIGNED_BYTE;
	public static final int SHORT						= 0x1402;	// GL11.GL_SHORT;
	public static final int	UNSIGNED_SHORT				= 0x1403;	// GL11.GL_UNSIGNED_SHORT;
	public static final int	INT							= 0x1404;	// GL11.GL_INT;
	public static final int UNSIGNED_INT				= 0x1405;	// GL11.GL_UNSIGNED_INT;
	public static final int	UNSIGNED_INT_8_8_8_8_REV	= 0x8367;

	//static int a = GL12.GL_UNSIGNED_INT_8_8_8_8_REV;

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
	
	public int getUniformLocation(int program, String name);
	
	public void pushMatrix();
	
	public void popMatrix();
	
	public void translatef(float x, float y, float z);
	
	public void multMatrixf(FloatBuffer matrix);
	
	public void loadMatrixf(FloatBuffer matrix);
	
	public void matrixMode(int mode);
	
	/**
	 * 
	 * @param mode Specifies what kind of primitives to render. Symbolic constants POINTS, LINE_STRIP, LINE_LOOP, LINES, TRIANGLE_STRIP, TRIANGLE_FAN, and TRIANGLES are accepted. 
	 * @param first Specifies the starting index in the enabled arrays.
	 * @param count Specifies the number of indices to be rendered.
	 */
	public void drawArrays(int mode, int first, int count);
	
	public void drawElements(int mode, int count, int type, ByteBuffer indices);
	
	public void drawElements(int mode, int count, int type, int offset);
	
	public void enable(int cap);
	
	public void disable(int cap);
	
	public void enableClientState(int cap);
	
	public void disableClientState(int cap);
	
	public void enableVertexAttribArray(int index);
	
	public void disableVertexAttribArray(int index);
	
	public void enableVertexArrayAttrib(int vaobj, int index);
	
	public void disableVertexArrayAttrib(int vaobj, int index);
	
	/**
	 * 
	 * @param target Specifies the target to which the texture is bound. Must be either TEXTURE_1D, TEXTURE_2D, TEXTURE_3D, or TEXTURE_CUBE_MAP.
	 * @param texture Specifies the name of a texture.
	 */
	public void bindTexture(int target, int texture);
	
	public void bindTextures(int first, int count, IntBuffer textures);
	
	public void activeTexture(int texture);
	
	public int genTexture();
	
	public void texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer data);
	
	public void compressedTexImage2D(int target, int level, int internalFormat, int width, int height, int border, ByteBuffer data);
	
	public void texParameterf(int target, int pname, int param);
	
	public void texParameteri(int target, int pname, int param);
	
	public void deleteTexture(int texture);
	
	public int genBuffer();
	
	public int createBuffer();
	
	public void deleteBuffer(int buffer);
	
	public void bindBuffer(int target, int buffer);
	
	public void bufferStorage(int target, ByteBuffer data, int flags);
	
	public void namedBufferStorage(int buffer, ByteBuffer data, int flags);
	
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