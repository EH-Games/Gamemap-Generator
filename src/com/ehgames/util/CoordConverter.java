package com.ehgames.util;

/**
 * Interface to assist in converting between coordinate systems.<br>
 * Implementing classes should keep in mind the possibility
 * of modifying in place during conversion
 */
public interface CoordConverter {
	public void convert(Vec3 in, Vec3 out);
	
	public default Vec3 convertToNew(Vec3 in) {
		Vec3 out = new Vec3();
		convert(in, out);
		return out;
	}
	
	public default void convertInPlace(Vec3 v) {
		convert(v, v);
	}
}
