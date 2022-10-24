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
	
	public void set(Vec3 v) {
		set(v.x, v.y, v.z);
	}
	
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
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
	
	public Vec3 add(Vec3 v) {
		return new Vec3(x + v.x, y + v.y, z + v.z);
	}
	
	public Vec3 addInPlace(Vec3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}
	
	public void add(Vec3 in, Vec3 out) {
		out.x = x + in.x;
		out.y = y + in.y;
		out.z = z + in.z;
	}
}