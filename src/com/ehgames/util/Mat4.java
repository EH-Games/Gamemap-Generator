package com.ehgames.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Mat4 {
	private static FloatBuffer createMatrix() {
		ByteBuffer buf = ByteBuffer.allocateDirect(64);
		buf.order(ByteOrder.nativeOrder());
		return buf.asFloatBuffer();
	}

	public final FloatBuffer	m		= createMatrix();
	
	public Mat4() {
		setIdentity();
	}
	
	public Mat4(Mat4 other) {
		set(other);
	}
	
	public void set(Mat4 other) {
		other.m.clear();
		m.clear();
		m.put(other.m).clear();
		other.m.clear();
	}
	
	public void setIdentity() {
		for(int i = 0; i < 16; i++) {
			m.put(i, i % 5 == 0 ? 1 : 0);
		}
	}
	
	public void setOrtho(float left, float right, float bottom, float top, float near, float far) {
		float rx = right - left;
		float ry = top - bottom;
		float rz = far - near;
		float tx = -(right + left) / rx;
		float ty = -(top + bottom) / ry;
		float tz = -(far + near) / rz;
		m.clear();
		m.put(new float[] {
				2 / rx, 0, 0, tx,
				0, 2 / ry, 0, ty,
				0, 0, -2 / rz, tz,
				0, 0, 0, 1
		}).clear();
	}
}
