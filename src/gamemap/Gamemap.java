package gamemap;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import org.lwjgl.LWJGLException;

public class Gamemap {
	public static final String				APP_NAME		= "Gamemap Generator 1.0.0";

	private static Frame					frame;
	private static GMCanvas					canvas;

	public static Viewer					viewer			= new Viewer();
	public static World						activeWorld		= null;

	private static Map<FileFilter, Plugin>	pluginsByFilter	= new HashMap<>();
	private static JFileChooser				fileChooser;

	private static int						mouseX, mouseY;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(UnsupportedLookAndFeelException | ReflectiveOperationException e) {}
		
		// needs to come here so it gets l&f set
		fileChooser = new JFileChooser();
		loadPlugins();
		
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
	
	private static void loadPlugins() {
		ServiceLoader<Plugin> loader =  ServiceLoader.load(
				Plugin.class, new PluginClassLoader());
		int loadedCount = 0;
		for(Plugin plugin : loader) {
			boolean loaded = false;
			try {
				FileFilter[] filters = plugin.getFileFilters();
				if(filters != null) {
					for(FileFilter filter : filters) {
						if(filter != null) {
							pluginsByFilter.put(filter, plugin);
							fileChooser.addChoosableFileFilter(filter);
							loaded = true;
						}
					}
				}
			} catch(Throwable t) {}
			if(loaded) loadedCount++;
		}
		
		StringBuilder str = new StringBuilder("Loaded ").append(loadedCount).append(" plugin");
		if(loadedCount != 1) str.append('s');
		System.out.println(str);
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
		createMenuItem("Open Game World...", file).addActionListener(e -> showFileChooser());
		createMenuItem("Render Map", file);
		file.addSeparator();
		createMenuItem("Exit", file).addActionListener(e -> attemptToExit());
		menus.add(file);

		Menu view = new Menu("View");
		CheckboxMenuItem ortho = new CheckboxMenuItem("Topdown View", viewer.orthogonal);
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
	
	private static class PluginWorlds {
		Plugin		plugin;
		String[]	worldNames;
		String		selected;
	}

	private static PluginWorlds getPluginForFile(File file) {
		for(Map.Entry<FileFilter, Plugin> entry : pluginsByFilter.entrySet()) {
			if(entry.getKey().accept(file)) {
				Plugin polledPlugin = entry.getValue();
				try {
					String[] names = polledPlugin.getWorldList(file);
					if(names != null && names.length > 0) {
						PluginWorlds worlds = new PluginWorlds();
						worlds.plugin = polledPlugin;
						worlds.worldNames = names;
						return worlds;
					}
				} catch(Throwable t) {}
			}
		}
		return null;
	}
	
	private static void showFileChooser() {
		if(pluginsByFilter.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "You don't have any plugins to load things with!",
					"No Plugins", JOptionPane.ERROR_MESSAGE);
		} else {
			int result = fileChooser.showOpenDialog(frame);
			if(result == JFileChooser.APPROVE_OPTION) {
				// TODO disable loading buttons
				final File file = fileChooser.getSelectedFile();
				// TODO save directory to a config file
				CompletableFuture.supplyAsync(() -> getPluginForFile(file)).thenApplyAsync(worlds -> {
					if(worlds == null) {
						JOptionPane.showMessageDialog(frame, "No plugins found that support this file",
								"Unsupported File", JOptionPane.ERROR_MESSAGE);
						return null;
					} else {
						if(worlds.worldNames.length > 1) {
							Object selection = JOptionPane.showInputDialog(frame,
									"Select world to load", "Select World", JOptionPane.PLAIN_MESSAGE,
									null, worlds.worldNames, worlds.worldNames[0]);
							if(selection != null) worlds.selected = selection.toString();
						} else {
							worlds.selected = worlds.worldNames[0];
						}
						return worlds;
					}
				}).thenAcceptAsync(worlds -> {
					if(worlds != null) {
						try {
							World world = worlds.plugin.loadWorld(file, worlds.selected);
							if(world == null) {
								JOptionPane.showMessageDialog(frame, "Plugin \"" + 
										worlds.plugin.getName() + "\" did not return a gameworld",
										"No Gameworld", JOptionPane.ERROR_MESSAGE);
							} else {
								// TODO set world
							}
						} catch(Exception e) {
							JOptionPane.showMessageDialog(frame, "Plugin \"" +
									worlds.plugin.getName() +
									"\" threw an exception creating a gameworld:\n" + e.toString(),
									"Import Exception", JOptionPane.ERROR_MESSAGE);
						}
					}
					// TODO reenable loading buttons 
				});
			}
		}
	}

	private static void attemptToExit() {
		frame.setVisible(false);
		frame.dispose();
	}
}