package gamemap;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

import com.google.gson.Gson;

import gamemap.world.Camera;
import gamemap.world.World;

public class Gamemap {
	public static final String				APP_NAME		= "Gamemap Generator 1.0.0";
	private static final Path				CONFIG_FILE		= new File("config.json").toPath();

	private static Frame					frame;
	private static GMCanvas					canvas;
	private static boolean					privelegedCode	= false;
	private static Config					config			= new Config();

	public static Viewer					viewer			= new Viewer();
	static Camera							camera			= new Camera();
	static World							activeWorld		= null;

	private static Map<FileFilter, Plugin>	pluginsByFilter	= new HashMap<>();
	private static JFileChooser				fileChooser;

	private static int						mouseX, mouseY;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(UnsupportedLookAndFeelException | ReflectiveOperationException e) {}
		
		// needs to come here so it gets l&f set
		fileChooser = new JFileChooser();
		
		loadConfig();
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
	
	private static void loadConfig() {
		boolean read = false;
		if(Files.isReadable(CONFIG_FILE)) {
			try {
				byte[] bytes = Files.readAllBytes(CONFIG_FILE);
				String json = new String(bytes, StandardCharsets.UTF_8);
				Gson gson = new Gson();
				config = gson.fromJson(json, Config.class);
				read = true;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		if(read) {
			File dir = new File(config.last_directory);
			if(dir.exists()) fileChooser.setCurrentDirectory(dir);
		} else {
			config.last_directory = fileChooser.getCurrentDirectory().getAbsolutePath();
		}
	}
	
	private static Dimension getViewportSize() {
		// TODO determine size based on what size works on the current display
		int width = config.viewport_width;
		if(width < 1) width = Config.DEFAULT_WIDTH;
		int height = config.viewport_height;
		if(height < 1) height = Config.DEFAULT_HEIGHT;
		return new Dimension(width, height);
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
		canvas.setPreferredSize(getViewportSize());
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
	
	/**
	 * Flag to indicate between packages that code is allowed to run.<br>
	 * This exists so that plugin's can't call internal code
	 * without having everything in a single package.
	 */
	public static boolean isPrivelegedCode() {
		return privelegedCode;
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
								privelegedCode = true;
								camera.onWorldChange(world);
								privelegedCode = false;
								activeWorld = world;
								if(!canvas.activeRendering) canvas.repaint();
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
	
	private static void saveConfig() {
		config.last_directory = fileChooser.getCurrentDirectory().getAbsolutePath();
		if(frame.getExtendedState() == Frame.NORMAL) {
			config.viewport_width = canvas.getWidth();
			config.viewport_height = canvas.getHeight();
		}
		
		try {
			Gson gson = new Gson();
			String json = gson.toJson(config);
			byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
			Files.write(CONFIG_FILE, bytes);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void attemptToExit() {
		saveConfig();
		frame.setVisible(false);
		frame.dispose();
	}
}