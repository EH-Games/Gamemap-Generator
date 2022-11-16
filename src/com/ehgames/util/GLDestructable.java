package com.ehgames.util;

/**
 * Interface implemented by classes that might have OpenGL resources that need destroyed
 */
public interface GLDestructable {
	/**
	 * Called when the world is destroyed so that renderers can free any allocated resources.
	 * @param gl the relevant OpenGL context
	 */
	public default void destroyResources(GL gl) {}
}
