package gamemap.types;

public class MapViewRenderer {
	public final String	type	= "image";
	/** Filename of the image without any path component */
	public String		url		= "out.png";
	/** The position of the top left corner of the image in world coordinates */
	public final Point	origin	= new Point();
	/**
	 * The size of the image in world units.<br>
	 * {@link Size#height height} should be negative if positive Y is north.
	 */
	public final Size	size	= new Size();
}
