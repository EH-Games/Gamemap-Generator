package gamemap;

import java.awt.image.BufferedImage;

public class ExportTask {
	public float	minX;
	public float	minY;
	public float	maxX;
	public float	maxY;
	int				pixelX;
	int				pixelY;
	int				pixelWidth;
	int				pixelHeight;
	int				pixelCount;
	int[]			pixels;

	void merge(BufferedImage img) {
		img.setRGB(pixelX, pixelY, pixelWidth, pixelHeight, pixels, 0, pixelWidth);
	}
}
