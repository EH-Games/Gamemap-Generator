package com.ehgames.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Mat4 {
	public final FloatBuffer m = BufferUtils.createFloatBuffer(16);

	public Mat4() {
		setIdentity();
	}

	public Mat4(Mat4 other) {
		set(other);
	}
	
	public float[] toArray() {
		float[] f = new float[16];
		m.clear();
		m.get(f).clear();
		return f;
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
				2 / rx, 0, 0, 0,
				0, 2 / ry, 0, 0,
				0, 0, -2 / rz, 0,
				tx, ty, tz, 1
		}).clear();
	}
	
	public void setPerspective(float fovY, float aspect, float zNear, float zFar) {
		float f = (float) (1 / Math.tan(fovY / 2));
		float zRange = zNear - zFar;
		m.clear();
		m.put(new float[] {
				f / aspect, 0, 0, 0,
				0, f, 0, 0,
				0, 0, (zFar + zNear) / zRange, -1,
				0, 0, (2 * zFar * zNear) / zRange, 0
		}).clear();
	}
	
	public void rotateX(double radians) {
		float c = (float) Math.cos(radians);
		float s = (float) Math.sin(radians);
		setIdentity();
		// good compared to OpenGL results
		m.put(5, c).put(10, c);
		m.put(6, s).put(9, -s);
	}
	
	public void rotateY(double radians) {
		float c = (float) Math.cos(radians);
		float s = (float) Math.sin(radians);
		// good compared to OpenGL results
		m.put(0, c).put(10, c);
		m.put(2, -s).put(8, s);
	}
	
	public void rotateZ(double radians) {
		float c = (float) Math.cos(radians);
		float s = (float) Math.sin(radians);
		// good compared to OpenGL results
		m.put(0, c).put(5, c);
		m.put(1, s).put(4, -s);
	}
	
	public Vec3 getAxis(int axis) {
		if(axis < 0 || axis > 2) {
			throw new IllegalArgumentException("Axis must be between 0 & 2 inclusive");
		}
		Vec3 v = new Vec3();
		return getAxisImpl(axis, v);
	}
	
	public Vec3 getAxis(int axis, Vec3 out) {
		if(axis < 0 || axis > 2) {
			throw new IllegalArgumentException("Axis must be between 0 & 2 inclusive");
		}
		return getAxisImpl(axis, out);
	}
	
	private Vec3 getAxisImpl(int axis, Vec3 out) {
		// XXX might be wrong. if so, change to axis * 4 + i
		return out.set(m.get(axis), m.get(axis + 4), m.get(axis + 8));
	}
	
	public float determinant() {
		// XXX needs checked somehow
		float det = m.get(0) * m.get(5) * m.get(10) * m.get(15);
		det += m.get(4) * m.get(9) * m.get(14) * m.get(3);
		det += m.get(8) * m.get(13) * m.get(2) * m.get(7);
		det += m.get(12) * m.get(1) * m.get(6) * m.get(11);
		det -= m.get(3) * m.get(6) * m.get(9) * m.get(12);
		det -= m.get(7) * m.get(10) * m.get(13) * m.get(0);
		det -= m.get(11) * m.get(14) * m.get(1) * m.get(4);
		det -= m.get(15) * m.get(2) * m.get(5) * m.get(8);
		return det;
	}
	
	public Mat4 invert() {
		Mat4 out = new Mat4();
		invert(out);
		return out;
	}
	
	public void invert(Mat4 out) {
		float det = determinant();
		// don't need epsilon equals. near zero won't give division errors
		if(det == 0) {
			throw new IllegalArgumentException("Matrix is not invertible");
		}
		// TODO finish
	}
	
	public Mat4 mult(Mat4 other) {
		Mat4 out = new Mat4();
		mult(other, out);
		return out;
	}
	
	public void mult(Mat4 in, Mat4 out) {
		float[] a = toArray();
		float[] b = in.toArray();
		for(int col = 0; col < 4; col++) {
			for(int row = 0; row < 4; row++) {
				float val = 0;
				for(int i = 0; i < 4; i++) {
					// good compared to OpenGL results
					val += a[(i << 2) + row] * b[(col << 2) + i];
				}
				out.m.put((col << 2) + row, val);
			}
		}
	}
	
	public Vec3 transform(Vec3 in) {
		Vec3 out = new Vec3();
		transform(in, out);
		return out;
	}
	
	public void transform(Vec3 in, Vec3 out) {
		// XXX needs checked somehow
		/*
		out.x = in.x * m.get(0) + in.y * m.get(4) + in.z * m.get(8) + m.get(12);
		out.y = in.x * m.get(1) + in.y * m.get(5) + in.z * m.get(9) + m.get(13);
		out.z = in.x * m.get(2) + in.y * m.get(6) + in.z * m.get(10) + m.get(14);
		/*/
		out.x = in.x * m.get(0) + in.y * m.get(1) + in.z * m.get(2) + m.get(12);
		out.y = in.x * m.get(4) + in.y * m.get(5) + in.z * m.get(6) + m.get(13);
		out.z = in.x * m.get(8) + in.y * m.get(9) + in.z * m.get(10) + m.get(14);
		//*/
	}
	
	/*
	@Override
	public String toString() {
		return String.format("/% .2f, % .2f, % .2f, % .2f\\\n", m.get(0), m.get(4), m.get(8), m.get(12));
	}
	//*/
}
