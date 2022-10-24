package gamemap;

import com.ehgames.util.Vec3;

public class Viewer {
	public Vec3		position	= new Vec3();
	public boolean	orthogonal	= true;
	private double	scale		= 1;
	private int		zoomFactor	= 0;

	public float getScale() {
		return (float) scale;
	}
	
	private void calculateScale() {
		double tmp = 1;
		
		// can't calculate as a fraction using integers because we easily overflow data types
		if(zoomFactor > 0) {
			for(int i = 0; i < zoomFactor; i++) {
				tmp *= 1.1;
			}
		} else if(zoomFactor < 0) {
			for(int i = 0; i > zoomFactor; i--) {
				tmp *= 0.9;
			}
		}

		// causes zoom to remain centered on window center
		// we could possibly factor in mouse coordinates as well to make it centered on the mouse
		position.x = (float) (position.x / scale * tmp);
		position.y = (float) (position.y / scale * tmp);
		
		//System.out.println("s = " + tmp);
		scale = tmp;
	}

	public void setZoomFactor(int factor) {
		zoomFactor = factor;
		calculateScale();
	}

	public void incrementZoom() {
		zoomFactor++;
		calculateScale();
	}

	public void decrementZoom() {
		zoomFactor--;
		calculateScale();
	}
}