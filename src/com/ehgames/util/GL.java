package com.ehgames.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.EXTTextureCompressionS3TC;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.KHRTextureCompressionAstcLdr;

public interface GL {
	public static final int	POINTS										= 0;		// GL11.GL_POINTS;
	public static final int	LINES										= 1;		// GL11.GL_LINES;
	public static final int	LINE_LOOP									= 2;		// GL11.GL_LINE_LOOP;
	public static final int	LINE_STRIP									= 3;		// GL11.GL_LINE_STRIP;
	public static final int	TRIANGLES									= 4;		// GL11.GL_TRIANGLES;
	public static final int	TRIANGLE_STRIP								= 5;		// GL11.GL_TRIANGLE_STRIP;

	public static final int	CULL_FACE									= 0xB44;
	public static final int	DEPTH_TEST									= 0xB71;	// GL11.GL_DEPTH_TEST;

	public static final int	MODELVIEW									= 0x1700;	// GL11.GL_MODELVIEW;
	public static final int	PROJECTION									= 0x1701;	// GL11.GL_PROJECTION;
	public static final int	TEXTURE										= 0x1702;	// GL11.GL_TEXTURE;

	public static final int	MODELVIEW_MATRIX							= 0xBA6;	// GL11.GL_MODELVIEW_MATRIX;

	public static final int	TEXTURE_MAG_FILTER							= 0x2800;	// GL11.GL_TEXTURE_MAG_FILTER;
	public static final int	TEXTURE_MIN_FILTER							= 0x2801;	// GL11.GL_TEXTURE_MIN_FILTER;
	public static final int	TEXTURE_WRAP_S								= 0x2802;	// GL11.GL_TEXTURE_WRAP_S;
	public static final int	TEXTURE_WRAP_T								= 0x2803;	// GL11.GL_TEXTURE_WRAP_T;

	public static final int	NEAREST										= 0x2600;	// GL11.GL_NEAREST;
	public static final int	LINEAR										= 0x2601;	// GL11.GL_LINEAR;
	public static final int	NEAREST_MIPMAP_NEAREST						= 0x2700;	// GL11.GL_NEAREST_MIPMAP_NEAREST;
	public static final int	LINEAR_MIPMAP_NEAREST						= 0x2701;	// GL11.GL_LINEAR_MIPMAP_NEAREST;
	public static final int	NEAREST_MIPMAP_LINEAR						= 0x2702;	// GL11.GL_NEAREST_MIPMAP_LINEAR;
	public static final int	LINEAR_MIPMAP_LINEAR						= 0x2703;	// GL11.GL_LINEAR_MIPMAP_LINEAR;
	public static final int	REPEAT										= 0x2901;	// GL11.GL_REPEAT;
	public static final int	MIRRORED_REPEAT								= 0x8370;	// GL14.GL_MIRRORED_REPEAT;
	public static final int	CLAMP_TO_BORDER								= 0x812D;	// GL13.GL_CLAMP_TO_BORDER;
	public static final int	CLAMP_TO_EDGE								= 0x812F;	// GL12.GL_CLAMP_TO_EDGE;

	public static final int	TEXTURE_2D									= 0xDE1;	// GL11.GL_TEXTURE_2D;
	public static final int	TEXTURE_BINDING_2D							= 0x8069;	// GL11.GL_TEXTURE_BINDING_2D;
	public static final int	TEXTURE_COMPRESSED							= 0x86A1;	// GL13.GL_TEXTURE_COMPRESSED;
	public static final int	TEXTURE0									= 0x84C0;	// GL13.GL_TEXTURE0;
	
	public static final int	TEXTURE_CUBE_MAP							= 0x8513;	// GL13.GL_TEXTURE_CUBE_MAP;
	public static final int	TEXTURE_BINDING_CUBE_MAP					= 0x8514;	// GL13.GL_TEXTURE_BINDING_CUBE_MAP;
	public static final int	TEXTURE_CUBE_MAP_POSITIVE_X					= 0x8515;	// GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
	public static final int	TEXTURE_CUBE_MAP_NEGATIVE_X					= 0x8516;	// GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
	public static final int	TEXTURE_CUBE_MAP_POSITIVE_Y					= 0x8517;	// GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
	public static final int	TEXTURE_CUBE_MAP_NEGATIVE_Y					= 0x8518;	// GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
	public static final int	TEXTURE_CUBE_MAP_POSITIVE_Z					= 0x8519;	// GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
	public static final int	TEXTURE_CUBE_MAP_NEGATIVE_Z					= 0x851A;	// GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
	
	public static final int	RED											= 0x1903;	// GL11.GL_RED;
	public static final int	GREEN										= 0x1904;	// GL11.GL_GREEN;
	public static final int	BLUE										= 0x1905;	// GL11.GL_BLUE;
	public static final int	ALPHA										= 0x1906;	// GL11.GL_ALPHA;
	public static final int	RGB											= 0x1907;	// GL11.GL_RGB;
	public static final int	RGBA										= 0x1908;	// GL11.GL_RGBA;
	public static final int	LUMINANCE									= 0x1909;	// GL11.GL_LUMINANCE;
	public static final int	LUMINANCE_ALPHA								= 0x190A;	// GL11.GL_LUMINANCE_ALPHA;
	public static final int	BGR											= 0x80E0;	// GL12.GL_BGR;
	public static final int	BGRA										= 0x80E1;	// GL12.GL_BGRA;
	public static final int	RG											= 0x8227;	// GL30.GL_RG;
	public static final int	SRGB										= 0x8C40;	// GL21.GL_SRGB;
	public static final int	SRGB_ALPHA									= 0x8C42;	// GL21.GL_SRGB_ALPHA;

	public static final int	LUMINANCE8									= 0x8040;	// GL11.GL_LUMINANCE8;
	public static final int	LUMINANCE8_ALPHA8							= 0x8045;	// GL11.GL_LUMINANCE8_ALPHA8;
	
	public static final int	DEPTH_COMPONENT								= 0x1902;	// GL11.GL_DEPTH_COMPONENT;
	public static final int	DEPTH_STENCIL								= 0x84F9;	// GL30.GL_DEPTH_STENCIL;
	
	public static final int	R8											= 0x8229;	// GL30.GL_R8;
	public static final int	R16											= 0x822A;	// GL30.GL_R16;
	public static final int	R16F										= 0x822D;	// GL30.GL_R16F;
	public static final int	R32F										= 0x822E;	// GL30.GL_R32F;
	public static final int	RG8											= 0x822B;	// GL30.GL_RG8;
	public static final int	RG8_SNORM									= 0x8F95;	// GL31.GL_RG8_SNORM;
	public static final int	RGB8										= 0x8051;	// GL11.GL_RGB8;
	public static final int	RGBA4										= 0x8056;	// GL11.GL_RGBA4;
	public static final int	RGB5_A1										= 0x8057;	// GL11.GL_RGB5_A1;
	public static final int	R11F_G11F_B10F								= 0x8C3A;	// GL30.GL_R11F_G11F_B10F;
	public static final int	RGBA8										= 0x8058;	// GL11.GL_RGBA8;
	
	public static final int	DEPTH_COMPONENT16							= 0x81A5;	// GL14.GL_DEPTH_COMPONENT16;
	public static final int DEPTH_COMPONENT24							= 0x81A6;	// GL14.GL_DEPTH_COMPONENT24;
	public static final int DEPTH_COMPONENT32							= 0x81A7;	// GL14.GL_DEPTH_COMPONENT32;
	public static final int DEPTH_COMPONENT32F							= 0x8CAC;	// GL30.GL_DEPTH_COMPONENT32F;
	public static final int DEPTH24_STENCIL8							= 0x88F0;	// GL30.GL_DEPTH24_STENCIL8;
	public static final int DEPTH32F_STENCIL8							= 0x8CAD;	// GL30.GL_DEPTH32F_STENCIL8;

	public static final int	SRGB8										= 0x8C41;	// GL21.GL_SRGB8;
	public static final int	SRGB8_ALPHA8								= 0x8C43;	// GL21.GL_SRGB8_ALPHA8;

	// S3TC / DXT - https://registry.khronos.org/OpenGL/extensions/EXT/EXT_texture_compression_s3tc.txt
	public static final int	COMPRESSED_RGB_S3TC_DXT1_EXT				= 0x83F0;
	public static final int	COMPRESSED_RGBA_S3TC_DXT1_EXT				= 0x83F1;
	public static final int	COMPRESSED_RGBA_S3TC_DXT3_EXT				= 0x83F2;
	public static final int	COMPRESSED_RGBA_S3TC_DXT5_EXT				= 0x83F3;

	// SRGB S3TC / DXT - https://registry.khronos.org/OpenGL/extensions/EXT/EXT_texture_compression_s3tc_srgb.txt
	public static final int	COMPRESSED_SRGB_S3TC_DXT1_EXT				= 0x8C4C;
	public static final int	COMPRESSED_SRGB_ALPHA_S3TC_DXT1_EXT			= 0x8C4D;
	public static final int	COMPRESSED_SRGB_ALPHA_S3TC_DXT3_EXT			= 0x8C4E;
	public static final int	COMPRESSED_SRGB_ALPHA_S3TC_DXT5_EXT			= 0x8C4F;

	// RGCT - https://registry.khronos.org/OpenGL/extensions/EXT/EXT_texture_compression_rgtc.txt
	public static final int	COMPRESSED_RED_RGTC1_EXT					= 0x8DBB;
	public static final int	COMPRESSED_SIGNED_RED_RGTC1_EXT				= 0x8DBC;
	public static final int	COMPRESSED_RED_GREEN_RGTC2_EXT				= 0x8DBD;
	public static final int	COMPRESSED_SIGNED_RED_GREEN_RGTC2_EXT		= 0x8DBE;

	// BPCT - https://registry.khronos.org/OpenGL/extensions/EXT/EXT_texture_compression_bptc.txt
	public static final int	COMPRESSED_RGBA_BPTC_UNORM_EXT				= 0x8E8C;
	public static final int	COMPRESSED_SRGB_ALPHA_BPTC_UNORM_EXT		= 0x8E8D;
	public static final int	COMPRESSED_RGB_BPTC_SIGNED_FLOAT_EXT		= 0x8E8E;
	public static final int	COMPRESSED_RGB_BPTC_UNSIGNED_FLOAT_EXT		= 0x8E8F;

	// LATC - https://registry.khronos.org/OpenGL/extensions/EXT/EXT_texture_compression_latc.txt
	public static final int	COMPRESSED_LUMINANCE_LATC1_EXT				= 0x8C70;
	public static final int	COMPRESSED_SIGNED_LUMINANCE_LATC1_EXT		= 0x8C71;
	public static final int	COMPRESSED_LUMINANCE_ALPHA_LATC2_EXT		= 0x8C72;
	public static final int	COMPRESSED_SIGNED_LUMINANCE_ALPHA_LATC2_EXT	= 0x8C73;

	// ASTC - https://registry.khronos.org/OpenGL/extensions/KHR/KHR_texture_compression_astc_hdr.txt
	public static final int	COMPRESSED_RGBA_ASTC_4x4_KHR				= 0x93B0;
	public static final int	COMPRESSED_RGBA_ASTC_5x4_KHR				= 0x93B1;
	public static final int	COMPRESSED_RGBA_ASTC_5x5_KHR				= 0x93B2;
	public static final int	COMPRESSED_RGBA_ASTC_6x5_KHR				= 0x93B3;
	public static final int	COMPRESSED_RGBA_ASTC_6x6_KHR				= 0x93B4;
	public static final int	COMPRESSED_RGBA_ASTC_8x5_KHR				= 0x93B5;
	public static final int	COMPRESSED_RGBA_ASTC_8x6_KHR				= 0x93B6;
	public static final int	COMPRESSED_RGBA_ASTC_8x8_KHR				= 0x93B7;
	public static final int	COMPRESSED_RGBA_ASTC_10x5_KHR				= 0x93B8;
	public static final int	COMPRESSED_RGBA_ASTC_10x6_KHR				= 0x93B9;
	public static final int	COMPRESSED_RGBA_ASTC_10x8_KHR				= 0x93BA;
	public static final int	COMPRESSED_RGBA_ASTC_10x10_KHR				= 0x93BB;
	public static final int	COMPRESSED_RGBA_ASTC_12x10_KHR				= 0x93BC;
	public static final int	COMPRESSED_RGBA_ASTC_12x12_KHR				= 0x93BD;

	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_4x4_KHR		= 0x93D0;
	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_5x4_KHR		= 0x93D1;
	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_5x5_KHR		= 0x93D2;
	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_6x5_KHR		= 0x93D3;
	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_6x6_KHR		= 0x93D4;
	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_8x5_KHR		= 0x93D5;
	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_8x6_KHR		= 0x93D6;
	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_8x8_KHR		= 0x93D7;
	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_10x5_KHR		= 0x93D8;
	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_10x6_KHR		= 0x93D9;
	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_10x8_KHR		= 0x93DA;
	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_10x10_KHR		= 0x93DB;
	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_12x10_KHR		= 0x93DC;
	public static final int	COMPRESSED_SRGB8_ALPHA8_ASTC_12x12_KHR		= 0x93DD;

	public static final int	ARRAY_BUFFER								= 0x8892;	// GL15.GL_ARRAY_BUFFER;
	public static final int	ELEMENT_ARRAY_BUFFER						= 0x8893;	// GL15.GL_ELEMENT_ARRAY_BUFFER;

	public static final int	VERTEX_ARRAY								= 0x8074;	// GL11.GL_VERTEX_ARRAY;
	public static final int	NORMAL_ARRAY								= 0x8075;	// GL11.GL_NORMAL_ARRAY;
	public static final int	COLOR_ARRAY									= 0x8076;	// GL11.GL_COLOR_ARRAY;
	public static final int	TEXTURE_COORD_ARRAY							= 0x8078;	// GL11.GL_TEXTURE_COORD_ARRAY;

	public static final int	BYTE										= 0x1400;	// GL11.GL_BYTE;
	public static final int	UNSIGNED_BYTE								= 0x1401;	// GL11.GL_UNSIGNED_BYTE;
	public static final int	SHORT										= 0x1402;	// GL11.GL_SHORT;
	public static final int	UNSIGNED_SHORT								= 0x1403;	// GL11.GL_UNSIGNED_SHORT;
	public static final int	INT											= 0x1404;	// GL11.GL_INT;
	public static final int	UNSIGNED_INT								= 0x1405;	// GL11.GL_UNSIGNED_INT;
	public static final int	FLOAT										= 0x1406;	// GL11.GL_FLOAT;
	public static final int	DOUBLE										= 0x140A;	// GL11.GL_DOUBLE;
	public static final int	HALF_FLOAT									= 0x140B;	// GL30.GL_HALF_FLOAT;
	public static final int	UNSIGNED_SHORT_4_4_4_4						= 0x8033;	// GL12.GL_UNSIGNED_SHORT_4_4_4_4;
	public static final int	UNSIGNED_SHORT_4_4_4_4_REV					= 0x8365;	// GL12.GL_UNSIGNED_SHORT_4_4_4_4_REV;
	public static final int	UNSIGNED_SHORT_5_5_5_1						= 0x8034;	// GL12.GL_UNSIGNED_SHORT_5_5_5_1;
	public static final int	UNSIGNED_SHORT_1_5_5_5_REV					= 0x8366;	// GL12.GL_UNSIGNED_SHORT_1_5_5_5_REV;
	public static final int	UNSIGNED_SHORT_5_6_5						= 0x8363;	// GL12.GL_UNSIGNED_SHORT_5_6_5;
	public static final int	UNSIGNED_SHORT_5_6_5_REV					= 0x8364;	// GL12.GL_UNSIGNED_SHORT_5_6_5_REV;
	public static final int	UNSIGNED_INT_8_8_8_8						= 0x8035;	// GL12.GL_UNSIGNED_INT_8_8_8_8;
	public static final int	UNSIGNED_INT_8_8_8_8_REV					= 0x8367;
	public static final int	UNSIGNED_INT_10F_11F_11F_REV				= 0x8C3B;	// GL30.GL_UNSIGNED_INT_10F_11F_11F_REV;

	public static final int	NO_ERROR									= 0;		// GL11.GL_NO_ERROR;
	public static final int	INVALID_ENUM								= 0x500;	// GL11.GL_INVALID_ENUM;
	public static final int	INVALID_VALUE								= 0x501;	// GL11.GL_INVALID_VALUE;
	public static final int	INVALID_OPERATION							= 0x502;	// GL11.GL_INVALID_OPERATION;

	public boolean isCoreProfile();

	public int getError();
	
	public int getInteger(int pname);
	
	public void getFloatv(int pname, FloatBuffer params);

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
	
	public void translated(double x, double y, double z);
	
	public void scalef(float x, float y, float z);
	
	public void scaled(double x, double y, double z);
	
	public void rotatef(float angle, float x, float y, float z);
	
	public void rotated(double angle, double x, double y, double z);
	
	public void multMatrixf(FloatBuffer matrix);
	
	public void loadMatrixf(FloatBuffer matrix);
	
	public void loadIdentity();
	
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