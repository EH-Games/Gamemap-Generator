package gamemap;

import java.awt.*;
import java.awt.event.*;

import org.lwjgl.LWJGLException;

public class Gamemap {
	public static final String	APP_NAME	= "Gamemap Generator 1.0.0";

	private static Frame		frame;
	private static GMCanvas		canvas;

	public static Viewer		viewer		= new Viewer();
	public static World			activeWorld	= null;
	
	private static int			mouseX, mouseY;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				frame = createFrame();
				frame.setVisible(true);
				frame.createBufferStrategy(2);
			} catch(LWJGLException e) {
				e.printStackTrace();
			}
		});
	}

	private static Frame createFrame() throws LWJGLException {
		Frame frame = new Frame();
		frame.setTitle(APP_NAME);

		MenuBar menus = createMenus();
		frame.setMenuBar(menus);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				attemptToExit();
			}
		});

		canvas = new GMCanvas();
		canvas.setBackground(Color.DARK_GRAY);
		canvas.setPreferredSize(getDefaultCanvasSize());
		canvas.addMouseWheelListener(e -> {
			int change = e.getWheelRotation();
			if(change > 0) {
				viewer.incrementZoom();
			} else {
				viewer.decrementZoom();
			}
			canvas.repaint();
		});
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int btn = e.getButton();
				if(btn == MouseEvent.BUTTON1) {
					mouseX = e.getX();
					mouseY = e.getY();
				} else if(btn == MouseEvent.BUTTON3) {
					viewer.position.x = 0;
					viewer.position.y = 0;
					canvas.repaint();
				}
			}
		});
		canvas.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
					int x = e.getX();
					int y = e.getY();
					int dx = mouseX - x;
					int dy = y - mouseY;
					mouseX = x;
					mouseY = y;
					viewer.position.x += dx * 0.02;
					viewer.position.y += dy * 0.02;
					canvas.repaint();
				}
			}
		});
		frame.add(canvas);

		frame.pack();
		frame.setLocationRelativeTo(null);

		return frame;
	}

	private static Dimension getDefaultCanvasSize() {
		// TODO determine size based on last window size or check what size works on the current display
		//return new Dimension(1600, 900);
		return new Dimension(1280, 720);
	}

	private static MenuItem createMenuItem(String text, Menu parent) {
		MenuItem item = new MenuItem(text);
		parent.add(item);
		return item;
	}

	private static MenuBar createMenus() {
		MenuBar menus = new MenuBar();

		Menu file = new Menu("File");
		createMenuItem("Open Game World...", file);
		createMenuItem("Render Map", file);
		file.addSeparator();
		createMenuItem("Exit", file).addActionListener(e -> attemptToExit());
		menus.add(file);

		Menu view = new Menu("View");
		CheckboxMenuItem ortho = new CheckboxMenuItem("Orthogonal", viewer.orthogonal);
		ortho.addItemListener(e -> {
			viewer.orthogonal = ortho.getState();
			canvas.repaint();
		});
		view.add(ortho);
		CheckboxMenuItem passive = new CheckboxMenuItem("Passive Rendering", false);
		passive.addItemListener(e -> {
			canvas.activeRendering = !passive.getState();
			if(canvas.activeRendering) canvas.repaint();
		});
		view.add(passive);
		menus.add(view);

		return menus;
	}

	private static void attemptToExit() {
		frame.setVisible(false);
		frame.dispose();
	}
}