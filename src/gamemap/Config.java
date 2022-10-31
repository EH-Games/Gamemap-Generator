package gamemap;

import java.awt.event.KeyEvent;

public class Config {
	public static final int	DEFAULT_WIDTH		= 1280;
	public static final int	DEFAULT_HEIGHT		= 720;

	public static class Keys {
		int forward = KeyEvent.VK_W;
		int backward = KeyEvent.VK_S;
		int left = KeyEvent.VK_A;
		int right = KeyEvent.VK_D;
		int up = KeyEvent.VK_E;
		int down = KeyEvent.VK_Q;
		
		int forward_alt = KeyEvent.VK_UP;
		int backward_alt = KeyEvent.VK_DOWN;
		int left_alt = KeyEvent.VK_LEFT;
		int right_alt = KeyEvent.VK_RIGHT;
		// so much better than winapi and lwjgl calling them prior and next
		int up_alt = KeyEvent.VK_PAGE_UP;
		int down_alt = KeyEvent.VK_PAGE_DOWN;
	}
	
	public int				viewport_width		= -1;
	public int				viewport_height		= -1;
	public double			sensitivity_x		= 0.05;
	/** can be negative to invert y */
	public double			sensitivity_y		= 0.05;
	/**
	 * If true, perspective camera movement uses the global z axis for up & down.
	 * Otherwise, it uses the camera-local y axis.
	 */
	public boolean			global_z_movement	= true;
	/** speed with no modifier keys held */
	public float			base_speed			= 0.3f;
	/** speed when shift is held */
	public float			shift_speed			= 1;
	/** speed when control is held */
	public float			ctrl_speed			= 2;
	/** speed when both shift & control are held */
	public float			shift_ctrl_speed	= 5;
	
	public String			last_directory		= System.getProperty("user.home");
	public Keys				keys				= new Keys();
}
