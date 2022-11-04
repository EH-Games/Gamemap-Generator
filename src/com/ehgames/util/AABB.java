package com.ehgames.util;

public class AABB {
	public final Vec3 min = new Vec3();
	public final Vec3 max = new Vec3();
	
	public void revalidate() {
		if(min.x > max.x) {
			float tmp = min.x;
			min.x = max.x;
			max.x = tmp;
		}
		if(min.y > max.y) {
			float tmp = min.y;
			min.y = max.y;
			max.y = tmp;
		}
		if(min.z > max.z) {
			float tmp = min.z;
			min.z = max.z;
			max.z = tmp;
		}
	}
	
	public void clear() {
		min.x = min.y = min.z = 0;
		max.x = max.y = max.z = 0;
	}
	
	public float getWidth() {
		return max.x - min.x;
	}
	
	public float getHeight() {
		return max.y - min.y;
	}
	
	public float getDepth() {
		return max.z - min.z;
	}
	
	/**
	 * Sets up this AABB so that calling {@code add} on it with another AABB
	 * will result in a copy of that AABB.
	 */
	public void prepForBuild() {
		min.x = min.y = min.z = Float.MAX_VALUE;
		max.x = max.y = max.z = Float.MIN_VALUE;
	}
	
	public void set(Vec3 min, Vec3 max) {
		if(min == null) throw new IllegalArgumentException("min can not be null");
		if(max == null) throw new IllegalArgumentException("max can not be null");
		
		this.min.set(min);
		this.max.set(max);
	}
	
	public void set(AABB other) {
		if(other == null) throw new IllegalArgumentException("other can not be null");
		min.set(other.min);
		max.set(other.max);
	}
	
	public void add(AABB other) {
		// dont change to add calls as that would have redundant checks
		// with checking min against max and vice versa
		min.x = Math.min(min.x, other.min.x);
		min.y = Math.min(min.y, other.min.y);
		min.z = Math.min(min.z, other.min.z);
		
		max.x = Math.max(max.x, other.max.x);
		max.y = Math.max(max.y, other.max.y);
		max.z = Math.max(max.z, other.max.z);
	}
	
	public void add(Vec3 p) {
		min.x = Math.min(min.x, p.x);
		min.y = Math.min(min.y, p.y);
		min.z = Math.min(min.z, p.z);
		
		max.x = Math.max(max.x, p.x);
		max.y = Math.max(max.y, p.y);
		max.z = Math.max(max.z, p.z);
	}
	
	public boolean contains2d(Vec3 point) {
		return
				point.x >= min.x && point.x <= max.x &&
				point.y >= min.y && point.y <= max.y;
	}
	
	public boolean contains(Vec3 point) {
		return
				point.x >= min.x && point.x <= max.x &&
				point.y >= min.y && point.y <= max.y &&
				point.z >= min.z && point.z <= min.z;
	}
	
	public boolean intersects2d(AABB other) {
		return
				min.x < other.max.x && max.x > other.min.x &&
				min.y < other.max.y && max.y > other.min.y;
	}
	
	public boolean intersects(AABB other) {
		return
				min.x < other.max.x && max.x > other.min.x &&
				min.y < other.max.y && max.y > other.min.y &&
				min.z < other.max.z && max.z > other.min.z;
	}
	
	public void convertInPlace(CoordConverter converter) {
		converter.convertInPlace(min);
		converter.convertInPlace(max);
		revalidate();
	}
}