package com.ehgames.util;

public class AABB {
	public float minX, minY, minZ;
	public float maxX, maxY, maxZ;
	
	public void revalidate() {
		if(minX > maxX) {
			float tmp = minX;
			minX = maxX;
			maxX = tmp;
		}
		if(minY > maxY) {
			float tmp = minY;
			minY = maxY;
			maxY = tmp;
		}
		if(minZ > maxZ) {
			float tmp = minZ;
			minZ = maxZ;
			maxZ = tmp;
		}
	}
	
	public void clear() {
		minX = minY = minZ = 0;
		maxX = maxY = maxZ = 0;
	}
	
	/**
	 * Sets up this AABB so that calling {@code add} on it with another AABB
	 * will result in a copy of that AABB.
	 */
	public void prepForBuild() {
		minX = minY = minZ = Float.MAX_VALUE;
		maxX = maxY = maxZ = Float.MIN_VALUE;
	}
	
	public void set(Vec3 min, Vec3 max) {
		if(min == null) throw new IllegalArgumentException("min can not be null");
		if(max == null) throw new IllegalArgumentException("max can not be null");
		
		minX = min.x;
		minY = min.y;
		minZ = min.z;
		maxX = max.x;
		maxY = max.y;
		maxZ = max.z;
	}
	
	public void add(AABB other) {
		minX = Math.min(minX, other.minX);
		minY = Math.min(minY, other.minY);
		minZ = Math.min(minZ, other.minZ);
		
		maxX = Math.max(maxX, other.maxX);
		maxY = Math.max(maxY, other.maxY);
		maxZ = Math.max(maxZ, other.maxZ);
	}
	
	public boolean contains2D(Vec3 point) {
		return
				point.x >= minX && point.x <= maxX &&
				point.y >= minY && point.y <= maxY;
	}
	
	public boolean contains(Vec3 point) {
		return
				point.x >= minX && point.x <= maxX &&
				point.y >= minY && point.y <= maxY &&
				point.z >= minZ && point.z <= minZ;
	}
	
	public boolean intersects2d(AABB other) {
		return
				minX < other.maxX && maxX > other.minX &&
				minY < other.maxY && maxY > other.minY;
	}
	
	public boolean intersects(AABB other) {
		return
				minX < other.maxX && maxX > other.minX &&
				minY < other.maxY && maxY > other.minY &&
				minZ < other.maxZ && maxZ > other.minZ;
	}
}