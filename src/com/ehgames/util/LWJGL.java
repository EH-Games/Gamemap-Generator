package com.ehgames.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GL45;

public final class LWJGL implements GL {

	@Override
	public boolean isCoreProfile() {
		//GL11.glGet
		return false;
	}
	
	@Override
	public int getError() {
		return GL11.glGetError();
	}
	
	@Override
	public int getInteger(int pname) {
		return GL11.glGetInteger(pname);
	}

	@Override
	public void getFloatv(int pname, FloatBuffer params) {
		GL11.glGetFloat(pname, params);
	}
	
	@Override
	public void depthFunc(int func) {
		GL11.glDepthFunc(func);
	}

	@Override
	public void clearDepth(double depth) {
		GL11.glClearDepth(depth);
	}

	@Override
	public void clearDepthf(float depth) {
		GL41.glClearDepthf(depth);
	}

	@Override
	public void alphaFunc(int func, float ref) {
		GL11.glAlphaFunc(func, ref);
	}

	@Override
	public void begin(int mode) {
		GL11.glBegin(mode);
	}

	@Override
	public void end() {
		GL11.glEnd();
	}

	@Override
	public void vertex2s(short x, short y) {
		// XXX doing this for now because missing method afaict.
		// this is one of those cases of LWJGL devs trying to be smarter than end users and providing 90% of a spec isn't it?
		GL11.glVertex2i(x, y);
	}

	@Override
	public void vertex2i(int x, int y) {
		GL11.glVertex2i(x, y);
	}

	@Override
	public void vertex2f(float x, float y) {
		GL11.glVertex2f(x, y);
	}

	@Override
	public void vertex3f(float x, float y, float z) {
		GL11.glVertex3f(x, y, z);
	}

	@Override
	public void texCoord2f(float u, float v) {
		GL11.glTexCoord2f(u, v);
	}

	@Override
	public void normal3f(float x, float y, float z) {
		GL11.glNormal3f(x, y, z);
	}

	@Override
	public void color3f(float red, float green, float blue) {
		GL11.glColor3f(red, green, blue);
	}

	@Override
	public void color4f(float red, float green, float blue, float alpha) {
		GL11.glColor4f(red, green, blue, alpha);
	}

	@Override
	public void color3ub(byte red, byte green, byte blue) {
		GL11.glColor3ub(red, green, blue);
	}

	@Override
	public void color4ub(byte red, byte green, byte blue, byte alpha) {
		GL11.glColor4ub(red, green, blue, alpha);
	}

	@Override
	public void vertexAttrib1f(int index, float v0) {
		GL20.glVertexAttrib1f(index, v0);
	}

	@Override
	public void vertexAttrib2f(int index, float v0, float v1) {
		GL20.glVertexAttrib2f(index, v0, v1);
	}

	@Override
	public void vertexAttrib3f(int index, float v0, float v1, float v2) {
		GL20.glVertexAttrib3f(index, v0, v1, v2);
	}

	@Override
	public void vertexAttrib4f(int index, float v0, float v1, float v2, float v3) {
		GL20.glVertexAttrib4f(index, v0, v1, v2, v3);
	}

	@Override
	public void vertexAttrib2s(int index, short v0, short v1) {
		GL20.glVertexAttrib2s(index, v0, v1);
	}

	@Override
	public void vertexPointer(int size, int type, int stride, ByteBuffer pointer) {
		GL11.glVertexPointer(size, type, stride, pointer);
	}

	@Override
	public void vertexPointer(int size, int type, int stride, int offset) {
		// using int rather than long because I'd like to hear of a use case where you need more than a 2gb offset for your attributes
		GL11.glVertexPointer(size, type, stride, offset);
	}

	@Override
	public void texCoordPointer(int size, int type, int stride, ByteBuffer pointer) {
		GL11.glTexCoordPointer(size, type, stride, pointer);
	}

	@Override
	public void texCoordPointer(int size, int type, int stride, int offset) {
		GL11.glTexCoordPointer(size, type, stride, offset);
	}

	@Override
	public void colorPointer(int size, int type, int stride, ByteBuffer pointer) {
		GL11.glColorPointer(size, type, stride, pointer);
	}

	@Override
	public void colorPointer(int size, int type, int stride, int offset) {
		GL11.glColorPointer(size, type, stride, offset);
	}

	@Override
	public void normalPointer(int type, int stride, ByteBuffer pointer) {
		GL11.glNormalPointer(type, stride, pointer);
	}

	@Override
	public void normalPointer(int type, int stride, int offset) {
		GL11.glNormalPointer(type, stride, offset);
	}

	@Override
	public void vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, ByteBuffer pointer) {
		GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
	}

	@Override
	public void vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, int offset) {
		GL20.glVertexAttribPointer(index, size, type, normalized, stride, offset);
	}

	@Override
	public void bindAttribLocation(int program, int index, String name) {
		GL20.glBindAttribLocation(program, index, name);
	}

	@Override
	public int getAttribLocation(int program, String name) {
		return GL20.glGetAttribLocation(program, name);
	}
	
	@Override
	public void uniform1f(int location, float v0) {
		GL20.glUniform1f(location, v0);
	}
	
	@Override
	public void uniform2f(int location, float v0, float v1) {
		GL20.glUniform2f(location, v0, v1);
	}
	
	@Override
	public void uniform3f(int location, float v0, float v1, float v2) {
		GL20.glUniform3f(location, v0, v1, v2);
	}

	@Override
	public void uniform4f(int location, float v0, float v1, float v2, float v3) {
		GL20.glUniform4f(location, v0, v1, v2, v3);
	}

	@Override
	public void uniform1i(int location, int v0) {
		GL20.glUniform1i(location, v0);
	}

	@Override
	public void uniform2i(int location, int v0, int v1) {
		GL20.glUniform2i(location, v0, v1);
	}

	@Override
	public void uniform3i(int location, int v0, int v1, int v2) {
		GL20.glUniform3i(location, v0, v1, v2);
	}

	@Override
	public void uniform4i(int location, int v0, int v1, int v2, int v3) {
		GL20.glUniform4i(location, v0, v1, v2, v3);
	}
	
	@Override
	public void uniformMatrix4fv(int location, boolean transpose, FloatBuffer value) {
		GL20.glUniformMatrix4(location, transpose, value);
	}
	
	@Override
	public int getUniformLocation(int program, String name) {
		return GL20.glGetUniformLocation(program, name);
	}
	
	@Override
	public void enable(int cap) {
		GL11.glEnable(cap);
	}

	@Override
	public void disable(int cap) {
		GL11.glDisable(cap);
	}

	@Override
	public void enableClientState(int cap) {
		GL11.glEnableClientState(cap);
	}

	@Override
	public void disableClientState(int cap) {
		GL11.glDisableClientState(cap);
	}

	@Override
	public void enableVertexAttribArray(int index) {
		GL20.glEnableVertexAttribArray(index);
	}

	@Override
	public void disableVertexAttribArray(int index) {
		GL20.glDisableVertexAttribArray(index);
	}

	@Override
	public void enableVertexArrayAttrib(int vaobj, int index) {
		GL45.glEnableVertexArrayAttrib(vaobj, index);
	}

	@Override
	public void disableVertexArrayAttrib(int vaobj, int index) {
		GL45.glDisableVertexArrayAttrib(vaobj, index);
	}

	@Override
	public void pushMatrix() {
		GL11.glPushMatrix();
	}

	@Override
	public void popMatrix() {
		GL11.glPopMatrix();
	}

	@Override
	public void translatef(float x, float y, float z) {
		GL11.glTranslatef(x, y, z);
	}

	@Override
	public void translated(double x, double y, double z) {
		GL11.glTranslated(x, y, z);
	}

	@Override
	public void scalef(float x, float y, float z) {
		GL11.glScalef(x, y, z);
	}

	@Override
	public void scaled(double x, double y, double z) {
		GL11.glScaled(x, y, z);
	}

	@Override
	public void rotatef(float angle, float x, float y, float z) {
		GL11.glRotatef(angle, x, y, z);
	}

	@Override
	public void rotated(double angle, double x, double y, double z) {
		GL11.glRotated(angle, x, y, z);
	}

	@Override
	public void multMatrixf(FloatBuffer matrix) {
		GL11.glMultMatrix(matrix);
	}

	@Override
	public void loadMatrixf(FloatBuffer matrix) {
		GL11.glLoadMatrix(matrix);
	}
	
	@Override
	public void loadIdentity() {
		GL11.glLoadIdentity();
	}
	
	@Override
	public void matrixMode(int mode) {
		GL11.glMatrixMode(mode);
	}

	@Override
	public void drawArrays(int mode, int first, int count) {
		GL11.glDrawArrays(mode, first, count);
	}

	@Override
	public void drawElements(int mode, int count, int type, ByteBuffer indices) {
		GL11.glDrawElements(mode, count, type, indices);
	}

	@Override
	public void drawElements(int mode, int count, int type, int offset) {
		GL11.glDrawElements(mode, count, type, offset);
	}

	@Override
	public void bindTexture(int target, int texture) {
		GL11.glBindTexture(target, texture);
	}

	@Override
	public void bindTextureUnit(int unit, int texture) {
		GL45.glBindTextureUnit(unit, texture);
	}

	@Override
	public void bindTextures(int first, int count, IntBuffer textures) {
		GL44.glBindTextures(first, count, textures);
	}

	@Override
	public void activeTexture(int texture) {
		GL13.glActiveTexture(texture);
	}

	@Override
	public int genTexture() {
		return GL11.glGenTextures();
	}

	@Override
	public void texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer data) {
		GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, data);
	}

	@Override
	public void compressedTexImage2D(int target, int level, int internalFormat, int width, int height, int border, ByteBuffer data) {
		GL13.glCompressedTexImage2D(target, level, internalFormat, width, height, border, data);
	}

	@Override
	public void texParameterf(int target, int pname, int param) {
		GL11.glTexParameterf(target, pname, param);
	}

	@Override
	public void texParameteri(int target, int pname, int param) {
		GL11.glTexParameteri(target, pname, param);
	}

	@Override
	public void deleteTexture(int texture) {
		GL11.glDeleteTextures(texture);
	}

	@Override
	public int genBuffer() {
		return GL15.glGenBuffers();
	}
	
	@Override
	public int createBuffer() {
		return GL45.glCreateBuffers();
	}

	@Override
	public void deleteBuffer(int buffer) {
		GL15.glDeleteBuffers(buffer);
	}

	@Override
	public void bindBuffer(int target, int buffer) {
		GL15.glBindBuffer(target, buffer);
	}
	
	@Override
	public void bufferStorage(int target, ByteBuffer data, int flags) {
		GL44.glBufferStorage(target, data, flags);
	}
	
	@Override
	public void namedBufferStorage(int buffer, ByteBuffer data, int flags) {
		GL45.glNamedBufferStorage(buffer, data, flags);
	}

	@Override
	public void bufferData(int target, ByteBuffer data, int usage) {
		GL15.glBufferData(target, data, usage);
	}

	@Override
	public void bufferSubData(int target, int offset, ByteBuffer data) {
		GL15.glBufferSubData(target, offset, data);
	}

	@Override
	public int createShader(int shaderType) {
		return GL20.glCreateShader(shaderType);
	}

	@Override
	public void shaderSource(int shader, String source) {
		GL20.glShaderSource(shader, source);
	}

	@Override
	public void attachShader(int program, int shader) {
		GL20.glAttachShader(program, shader);
	}

	@Override
	public void detachShader(int program, int shader) {
		GL20.glDetachShader(program, shader);
	}

	@Override
	public void compileShader(int shader) {
		GL20.glCompileShader(shader);
	}
	
	@Override
	public int getShaderi(int shader, int pname) {
		return GL20.glGetShaderi(shader, pname);
	}

	@Override
	public String getShaderInfoLog(int shader) {
		return GL20.glGetShaderInfoLog(shader, 8192);
	}
	
	@Override
	public void deleteShader(int shader) {
		GL20.glDeleteShader(shader);
	}

	@Override
	public int createProgram() {
		return GL20.glCreateProgram();
	}

	@Override
	public void linkProgram(int program) {
		GL20.glLinkProgram(program);
	}

	@Override
	public void validateProgram(int program) {
		GL20.glValidateProgram(program);
	}

	@Override
	public int getProgrami(int program, int pname) {
		return GL20.glGetProgrami(program, pname);
	}
	
	@Override
	public String getProgramInfoLog(int program) {
		return GL20.glGetProgramInfoLog(program, 8192);
	}

	@Override
	public void useProgram(int program) {
		GL20.glUseProgram(program);
	}

	@Override
	public void deleteProgram(int program) {
		GL20.glDeleteProgram(program);
	}
}