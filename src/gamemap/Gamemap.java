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
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import org.lwjgl.LWJGLException;

import com.ehgames.util.Vec3;
import com.google.gson.Gson;

import gamemap.world.Camera;
import gamemap.world.World;

public class Gamemap {
	public static final String				APP_NAME		= "Gamemap Generator 1.0.0";
	private static final Path				CONFIG_FILE		= new File("config.json").toPath();
	private static final int				INVALID_AXIS	= -5;
	// map of key press states to allow smooth motion with active rendering
	// since we can't use LWJGL's Keyboard class with an AWTGLCanvas
	private static final boolean[]			KEY_STATES		= new boolean[1024];

	private static Frame					frame;
	static GMCanvas							canvas;
	private static Config					config			= new Config();

	private static int						zoomLevel		= 0;
	static Camera							camera			= new Camera();
	static World							activeWorld		= null;

	private static Map<FileFilter, Plugin>	pluginsByFilter	= new HashMap<>();
	private static JFileChooser				fileChooser;
	private static ExportDialog				exportDialog;
	
	private static MenuItem					itemExport;
	private static MenuItem					itemOpen;

	private static int						mouseX, mouseY;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(UnsupportedLookAndFeelException | ReflectiveOperationException e) {}
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
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
	
	static void handleKeysActive() {
		// canvas.hasFocus() seems to work fine
		if(activeWorld == null || !camera.isPerspective() || !canvas.hasFocus()) return;
		
		Vec3 total = new Vec3();
		Vec3 tmp = new Vec3();
		
		if(KEY_STATES[config.keys.forward] || KEY_STATES[config.keys.forward_alt]) {
			camera.getViewAxis(2, tmp);
			total.addInPlace(tmp.scaleInPlace(-1));
		}
		if(KEY_STATES[config.keys.backward] || KEY_STATES[config.keys.backward_alt]) {
			camera.getViewAxis(2, tmp);
			total.addInPlace(tmp);
		}
		if(KEY_STATES[config.keys.left] || KEY_STATES[config.keys.left_alt]) {
			camera.getViewAxis(0, tmp);
			total.addInPlace(tmp.scaleInPlace(-1));
		}
		if(KEY_STATES[config.keys.right] || KEY_STATES[config.keys.right_alt]) {
			camera.getViewAxis(0, tmp);
			total.addInPlace(tmp);
		}
		if(KEY_STATES[config.keys.up] || KEY_STATES[config.keys.up_alt]) {
			if(config.global_z_movement) {
				tmp.set(0, 0, 1);
			} else {				
				camera.getViewAxis(1, tmp);
			}
			total.addInPlace(tmp);
		}
		if(KEY_STATES[config.keys.down] || KEY_STATES[config.keys.down_alt]) {
			if(config.global_z_movement) {
				tmp.set(0, 0, -1);
			} else {				
				camera.getViewAxis(1, tmp).scaleInPlace(-1);
			}
			total.addInPlace(tmp);
		}
		
		if(total.x != 0 || total.y != 0 || total.z != 0) {
			boolean shift = KEY_STATES[KeyEvent.VK_SHIFT];
			boolean ctrl = KEY_STATES[KeyEvent.VK_CONTROL];
			
			float speed = config.base_speed;
			if(shift) {
				speed = ctrl ? config.shift_ctrl_speed : config.shift_speed;
			} else if(ctrl) {
				speed = config.ctrl_speed;
			}
			
			total.scaleInPlace(speed);
			camera.move(total);
		}
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
	
	private static int axisFromKey(int key) {
		if(key == config.keys.forward || key == config.keys.forward_alt) {
			return -3;
		}
		if(key == config.keys.backward || key == config.keys.backward_alt) {
			return 2;
		}
		if(key == config.keys.left || key == config.keys.left_alt) {
			return -1;
		}
		if(key == config.keys.right || key == config.keys.right_alt) {
			return 0;
		}
		if(key == config.keys.up || key == config.keys.up_alt) {
			return 1;
		}
		if(key == config.keys.down || key == config.keys.down_alt) {
			return -2;
		}
		return INVALID_AXIS;
	}
	
	static void setWorldLocked(boolean locked) {
		itemOpen.setEnabled(!locked);
		itemExport.setEnabled(!locked);
	}

	private static Frame createFrame() throws LWJGLException {
		Frame frame = new Frame();
		frame.setTitle(APP_NAME);
		
		exportDialog = new ExportDialog(frame);

		MenuBar menus = createMenus();
		frame.setMenuBar(menus);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				attemptToExit();
			}
		});

		canvas = new GMCanvas();
		canvas.setBackground(Color.DARK_GRAY);
		canvas.setPreferredSize(getViewportSize());
		canvas.addMouseWheelListener(e -> {
			int change = e.getWheelRotation();
			if(activeWorld != null && !camera.isPerspective()) {
				if(change > 0) {
					camera.setZoomLevel(++zoomLevel);
				} else {
					camera.setZoomLevel(--zoomLevel);
				}
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
					if(activeWorld != null) {
						camera.onWorldChange(activeWorld);
						canvas.repaint();
					}
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
					if(activeWorld != null) {
						if(camera.isPerspective()) {
							if(dx != 0) {
								camera.rotateY(dx * config.sensitivity_x);
							}
							if(dy != 0) {
								camera.rotateX(-dy * config.sensitivity_y);
							}
						} else {
							double scale = camera.getScale();
							camera.move((float) (dx / scale), (float) (dy / scale), 0);
						}
					}
					canvas.repaint();
				}
			}
		});
		//*
		canvas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(activeWorld == null || !camera.isPerspective()) return;
				
				int keyCode = e.getKeyCode();
				if(keyCode >= 0 && keyCode < KEY_STATES.length) {
					KEY_STATES[keyCode] = true;
				}
				// active rendering has its own movement code that should be much smoother
				if(canvas.activeRendering) return;
				
				int axis = axisFromKey(e.getKeyCode());
				if(axis == INVALID_AXIS) return;

				float speed = config.base_speed;
				boolean ctrl = e.isControlDown();
				if(e.isShiftDown()) {
					speed = ctrl ? config.shift_ctrl_speed : config.shift_speed;
				} else if(ctrl) {
					speed = config.ctrl_speed;
				}

				// negative axis
				if(axis < 0) {
					axis = ~axis;
					speed = -speed;
				}
				
				Vec3 val = new Vec3();
				if(axis == 1 && config.global_z_movement) {
					val.set(0, 0, speed);
				} else {
					camera.getViewAxis(axis, val);
					val.scaleInPlace(speed);
				}
				camera.move(val);
				canvas.repaint();
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if(keyCode >= 0 && keyCode < KEY_STATES.length) {
					KEY_STATES[keyCode] = false;
				}
			}
		});
		//*/
		frame.add(canvas);

		frame.pack();
		frame.setLocationRelativeTo(null);

		return frame;
	}

	private static MenuItem createMenuItem(String text, Menu parent) {
		MenuItem item = new MenuItem(text);
		parent.add(item);
		return item;
	}

	private static MenuBar createMenus() {
		MenuBar menus = new MenuBar();

		Menu file = new Menu("File");
		itemOpen = createMenuItem("Open Game World...", file);
		itemOpen.addActionListener(e -> showFileChooser());
		itemExport = createMenuItem("Render Map", file);
		itemExport.setEnabled(false);
		itemExport.addActionListener(e -> {
			exportDialog.showDialogFor(activeWorld);
		});
		file.addSeparator();
		createMenuItem("Exit", file).addActionListener(e -> attemptToExit());
		menus.add(file);

		Menu view = new Menu("View");
		CheckboxMenuItem ortho = new CheckboxMenuItem("Topdown View", !camera.isPerspective());
		ortho.addItemListener(e -> {
			camera.setPerspective(!ortho.getState());
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
				setWorldLocked(true);
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
								camera.onWorldChange(world);
								activeWorld = world;
								if(!canvas.activeRendering) canvas.repaint();
							}
						} catch(Exception e) {
							JOptionPane.showMessageDialog(frame, "Plugin \"" +
									worlds.plugin.getName() +
									"\" threw an exception creating a gameworld:\n" + e.toString(),
									"Import Exception", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
					System.out.println("World loading complete");
					setWorldLocked(false); 
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