package com.ehgames.util;

/**
 * Standard 3 component, floating point vector/point type
 */
public class Vec3 {
	public float x, y, z;
	
	public Vec3() {
		this(0, 0, 0);
	}
	
	public Vec3(Vec3 v) {
		set(v);
	}
	
	public Vec3(float x, float y, float z) {
		set(x, y, z);
	}
	
	public Vec3 set(Vec3 v) {
		return set(v.x, v.y, v.z);
	}
	
	public Vec3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public boolean isWithinDistance(Vec3 point, float distance) {
		return distanceSq(point) <= distance * distance;
	}
	
	public float distanceSq(Vec3 point) {
		float dx = x - point.x;
		float dy = y - point.y;
		float dz = z - point.z;
		return dx * dx + dy * dy + dz * dz;
	}
	
	public float distance(Vec3 point) {
		float distSq = distanceSq(point);
		return (float) Math.sqrt(distSq);
	}
	
	public boolean isWithinDistance2D(Vec3 point, float distance) {
		return distanceSq2D(point) <= distance * distance;
	}
	
	public float distanceSq2D(Vec3 point) {
		float dx = x - point.x;
		float dy = y - point.y;
		return dx * dx + dy * dy;
	}
	
	public float distance2D(Vec3 point) {
		float distSq = distanceSq2D(point);
		return (float) Math.sqrt(distSq);
	}
	
	public float magnitudeSq() {
		// technically the dot product of itself and itself
		return x * x + y * y + z * z;
	}
	
	public float magnitude() {
		float mag = magnitudeSq();
		return mag == 0 ? 0 : (float) Math.sqrt(mag);
	}
	
	public Vec3 normalize() {
		Vec3 out = new Vec3();
		normalize(out);
		return out;
	}
	
	public Vec3 normalizeInPlace() {
		normalize(this);
		return this;
	}
	
	public void normalize(Vec3 out) {
		float mag = magnitude();
		div(mag, out);
	}
	
	public Vec3 cross(Vec3 v) {
		Vec3 out = new Vec3();
		cross(v, out);
		return out;
	}
	
	public void cross(Vec3 v, Vec3 out) {
		out.x = y * v.z - z * v.y;
		out.y = z * v.x - x * v.z;
		out.z = x * v.y - y * v.x;
	}
	
	public float dot(Vec3 v) {
		return x * v.x + y * v.y + z * v.z;
	}
	
	public Vec3 add(Vec3 v) {
		return new Vec3(x + v.x, y + v.y, z + v.z);
	}
	
	public Vec3 addInPlace(Vec3 v) {
		return addInPlace(v.x, v.y, v.z);
	}
	
	public Vec3 addInPlace(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public void add(Vec3 in, Vec3 out) {
		out.set(x + in.x, y + in.y, z + in.z);
	}
	
	public Vec3 sub(Vec3 v) {
		return new Vec3(x - v.x, y - v.y, z - v.z);
	}
	
	public Vec3 subInPlace(Vec3 v) {
		return subInPlace(v.x, v.y, v.z);
	}
	
	public Vec3 subInPlace(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}
	
	public void sub(Vec3 in, Vec3 out) {
		out.set(x - in.x, y - in.y, z - in.z);
	}
	
	public Vec3 scale(float f) {
		return new Vec3(x * f, y * f, z * f);
	}
	
	public Vec3 scaleInPlace(float f) {
		x *= f;
		y *= f;
		z *= f;
		return this;
	}
	
	public void scale(float f, Vec3 out) {
		out.set(x * f, y * f, z * f);
	}
	
	public Vec3 mult(float f) {
		return scale(f);
	}
	
	public Vec3 multInPlace(float f) {
		return scaleInPlace(f);
	}
	
	public void mult(float f, Vec3 out) {
		scale(f, out);
	}
	
	public Vec3 div(float f) {
		return scale(1 / f);
	}
	
	public Vec3 divInPlace(float f) {
		return scaleInPlace(1 / f);
	}
	
	public void div(float f, Vec3 out) {
		scale(f, out);
	}
	
	@Override
	public String toString() {
		return String.format("(%.2f, %.2f, %.2f)", x, y, z);
	}
}