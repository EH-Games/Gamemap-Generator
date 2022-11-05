package gamemap;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.lwjgl.LWJGLException;

import gamemap.world.World;
import gamemap.world.World3d;

public class ExportDialog extends Dialog {
	private static final String	INVALID_PATH_CHARS	= "\\/:*?\"<>|";
	private static final int	MARGIN_SIZE			= 8;

	private static JPanel makeTable(String title, JComponent... items) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		if(title != null) {
			Border line = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			panel.setBorder(BorderFactory.createTitledBorder(line, title));
		}

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0, MARGIN_SIZE / 2, MARGIN_SIZE, MARGIN_SIZE);
		gbc.gridx = 0;
		gbc.gridy = 0;
		for(int i = 0; i < items.length; i += 2) {
			panel.add(items[i], gbc);
			gbc.gridy++;
		}

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 0, MARGIN_SIZE, MARGIN_SIZE);
		gbc.gridx = 1;
		gbc.gridy = 0;
		for(int i = 1; i < items.length; i += 2) {
			panel.add(items[i], gbc);
			gbc.gridy++;
		}
		
		return panel;
	}
	
	private SpinnerNumberModel	threadCountMdl	= new SpinnerNumberModel(2, 1, 4, 1);
	private JSpinner			threadCountSel	= new JSpinner(threadCountMdl);
	private JTextField			friendlyName	= new JTextField(12);
	private JTextField			mapId			= new JTextField(12);
	private JFormattedTextField	unitsPerPixel	= new JFormattedTextField();
	private JFormattedTextField	dstFolderEntry	= new JFormattedTextField();
	private String				dstFolderName;
	private File				dstFolder;
	private JFormattedTextField	minXEntry		= new JFormattedTextField();
	private JFormattedTextField	minYEntry		= new JFormattedTextField();
	private JFormattedTextField	maxXEntry		= new JFormattedTextField();
	private JFormattedTextField	maxYEntry		= new JFormattedTextField();
	private JCheckBox			adjustBounds	= new JCheckBox("Adjust bounds to pixels", true);
	private double				minX, minY;
	private double				maxX, maxY;
	private double				width, height;
	
	private World				lastWorld;
	private boolean				is3d;

	public ExportDialog(Frame owner) {
		super(owner);
		
		setTitle("Export Rendered Map");
		setModal(true);
		setResizable(false);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
		
		mapId.setEnabled(false);
		
		JPanel general = makeTable("General",
				new JLabel("Friendly Name"), friendlyName,
				new JLabel("Folder Name"), dstFolderEntry,
				new JLabel("Map Id"), mapId,
				new JLabel("Max Threads"), threadCountSel);
		
		unitsPerPixel.setColumns(8);
		
		JPanel sizing = makeTable("Sizing",
				new JLabel("Units Per Pixel"), unitsPerPixel,
				new JLabel("Min X"), minXEntry,
				new JLabel("Min Y"), minYEntry,
				new JLabel("Max X"), maxXEntry,
				new JLabel("Max Y"), maxYEntry);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, MARGIN_SIZE, MARGIN_SIZE / 2, MARGIN_SIZE);
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		sizing.add(adjustBounds, gbc);

		JButton exportBtn = new JButton("Export");
		exportBtn.addActionListener(e -> {
			String error = validateFields();
			if(error != null) {
				JOptionPane.showMessageDialog(this, error, "Invalid Fields", JOptionPane.ERROR_MESSAGE);
			} else {
				Gamemap.setWorldLocked(true);
				setVisible(false);

				dstFolder = new File("out/" + lastWorld.mapId + '/' + dstFolderName);
				dstFolder.mkdirs();
				
				if(is3d) {
					export3d();
				} else {
					exportTiles();
				}
			}
		});

		JPanel content = new JPanel();
		content.setLayout(new GridBagLayout());
		
		gbc.insets = new Insets(MARGIN_SIZE / 2, MARGIN_SIZE, MARGIN_SIZE / 2, MARGIN_SIZE / 2);
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 4;
		content.add(general, gbc);

		gbc.insets = new Insets(MARGIN_SIZE / 2, 0, MARGIN_SIZE, MARGIN_SIZE);
		gbc.gridheight = 6;
		gbc.gridx = 1;
		content.add(sizing, gbc);

		gbc.insets = new Insets(0, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE / 2);
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 5;
		content.add(exportBtn, gbc);
		
		add(content);
		pack();
	}
	
	/**
	 * @return An error string telling what fields are invalid
	 * or null if everything is ok
	 */
	private String validateFields() {
		dstFolderName = dstFolderEntry.getText();
		if(dstFolderName.isEmpty()) return "Destination folder not specified";
		if(dstFolderName.charAt(0) == '.' || dstFolderName.endsWith(".")) {
			return "Destination folder can not start or end with a '.'";
		}
		for(char c : dstFolderName.toCharArray()) {
			if(INVALID_PATH_CHARS.indexOf(c) != -1) {
				return "Destination folder has illegal character: " + c;
			}
		}

		if(is3d) {
			double scale = lastWorld.worldUnitsPerMapUnit;
			// bounds, translated back to world units
			minX = ((Number) minXEntry.getValue()).doubleValue() * scale;
			maxX = ((Number) maxXEntry.getValue()).doubleValue() * scale;
			if(lastWorld.positiveYIsDown) {
				maxY = ((Number) minYEntry.getValue()).doubleValue() * -scale;
				minY = ((Number) maxYEntry.getValue()).doubleValue() * -scale;
			} else {
				minY = ((Number) minYEntry.getValue()).doubleValue() * scale;
				maxY = ((Number) maxYEntry.getValue()).doubleValue() * scale;
			}
			width = maxX - minX;
			height = maxY - minY;
			if(width <= 0) return "Max X must be greater than Min X";
			if(height <= 0) return "Max Y must be greater than Min Y";
			
			double upp = ((Number) unitsPerPixel.getValue()).doubleValue();
			if(upp <= 0) return "Units Per Pixel must be a positive value";
		}
		
		//return "Exporting not yet supported";
		return null;
	}
	
	public void showDialogFor(World world) {
		int maxThreads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
		// max at 30 so we always have enough bits for the visibility flags
		// assuming there's no old cameras still lingering in memory
		// the alternative would be to make visibility flags a long and limit to 62
		// (always keep 2 unusable for both the viewport camera and sign bit)
		if(maxThreads > 30) maxThreads = 30;
		threadCountMdl.setMaximum(maxThreads);
		int threads = ((Number) threadCountMdl.getValue()).intValue();
		if(threads > maxThreads) {
			threadCountMdl.setValue(maxThreads);
		}
		
		mapId.setText(world.mapId);
		is3d = world instanceof World3d;
		minXEntry.setEnabled(is3d);
		minYEntry.setEnabled(is3d);
		maxXEntry.setEnabled(is3d);
		maxYEntry.setEnabled(is3d);
		unitsPerPixel.setEnabled(is3d);
		threadCountSel.setEnabled(is3d);
		adjustBounds.setEnabled(is3d);

		// we only want to fill stuff in if this is a new world
		// otherwise, we're resetting values the user might have changed
		if(world != lastWorld) {
			friendlyName.setText(world.friendlyName);
			if(is3d) {
				World3d w3d = (World3d) world;
				unitsPerPixel.setValue(w3d.preferredUnitsPerPixel);
				// bounds, translated to user units
				minXEntry.setValue(w3d.root.bounds.min.x / w3d.worldUnitsPerMapUnit);
				maxXEntry.setValue(w3d.root.bounds.max.x / w3d.worldUnitsPerMapUnit);
				double miny = w3d.root.bounds.min.y / w3d.worldUnitsPerMapUnit;
				double maxy = w3d.root.bounds.max.y / w3d.worldUnitsPerMapUnit;
				if(w3d.positiveYIsDown) {
					minYEntry.setValue(-maxy);
					maxYEntry.setValue(-miny);
				} else {
					minYEntry.setValue(miny);
					maxYEntry.setValue(maxy);
				}
			}
			lastWorld = world;
		}
		
		setLocationRelativeTo(getParent());
		setVisible(true);
	}
	
	private void exportTiles() {
		// TODO implement
	}
	
	private void export3d() {
		// attempt to force finalize to be called on any old Cameras to release their flags
		System.gc();
		
		double upp = ((Number) unitsPerPixel.getValue()).doubleValue();
		double pwd = width / upp;
		double phd = height / upp;
		int pixelWidth = (int) Math.ceil(pwd);
		int pixelHeight = (int) Math.ceil(phd);
		if(adjustBounds.isSelected()) {
			if(minX < 0 && maxX > 0) {
				// adjust min and max, keeping origin at a pixel boundary
				minX = Math.floor(minX / upp) * upp;
				maxX = Math.ceil(maxX / upp) * upp;
				width = maxX - minX;
				pwd = width / upp;
				pixelWidth = (int) pwd;
			} else {
				double diff = pixelWidth - pwd;
				// keep maxX at 0 if that's where it is
				if(Math.abs(maxX) < 0.00001) {
					minX -= diff;
				} else {
					maxX += diff;
				}
			}
			if(minY < 0 && maxY > 0) {
				// adjust min and max, keeping origin at a pixel boundary
				minY = Math.floor(minY / upp) * upp;
				maxY = Math.ceil(maxY / upp) * upp;
				height = maxY - minY;
				phd = height / upp;
				pixelHeight = (int) phd;
			} else {
				double diff = pixelHeight - phd;
				// keep maxY at 0 if that's where it is
				if(Math.abs(maxY) < 0.00001) {
					minY -= diff;
				} else {
					maxY += diff;
				}
			}
		}
		System.out.println("Exporting image of size " + pixelWidth + "x" + pixelHeight);
		
		int pwTask = pixelWidth;
		if(pixelWidth > 2048) {
			// try to cut into 1024 chunks
			int tasksWide = pixelWidth / 1024;
			int allTasksWidth = tasksWide * 1024;
			// if it can't be cut up perfectly into 1024 chunks
			// try to cut it up evenly with a slightly larger size
			if(allTasksWidth != pixelWidth) {
				pwTask = pixelWidth / tasksWide;
				// if we still didn't cut it up perfectly
				// increase the size so there isn't a tiny sliver missing
				if(pwTask * tasksWide != pixelWidth) {
					++pwTask;
				}
			} else {
				pwTask = 1024;
			}
		}
		
		int phTask = pixelHeight;
		if(pixelHeight > 2048) {
			// try to cut into 1024 chunks
			int tasksHigh = pixelHeight / 1024;
			int allTasksHeight = tasksHigh * 1024;
			// if it can't be cut up perfectly into 1024 chunks
			// try to cut it up evenly with a slightly larger size
			if(allTasksHeight != pixelHeight) {
				phTask = pixelHeight / tasksHigh;
				// if we still didn't cut it up perfectly
				// increase the size so there isn't a tiny sliver missing
				if(phTask * tasksHigh != pixelHeight) {
					++phTask;
				}
			} else {
				phTask = 1024;
			}
		}
		
		System.out.println("Rendering in chunks sized " + pwTask + "x" + phTask);
		
		List<ExportTask> tasks = new ArrayList<>();
		for(int y = 0; y < pixelHeight; y += phTask) {
			int tph = Math.min(pixelHeight - y, phTask);
			double yProgTop = y / phd;
			double yProgBtm = (y + tph) / phd;
			float miny = (float) (minY * (yProgBtm) + maxY * (1 - yProgBtm));
			float maxy = (float) (minY * (yProgTop) + maxY * (1 - yProgTop));
			
			for(int x = 0; x < pixelWidth; x += pwTask) {
				int tpw = Math.min(pixelWidth - x, pwTask);
				
				ExportTask task = new ExportTask();
				task.pixelX = x;
				task.pixelY = y;
				task.pixelWidth = tpw;
				task.pixelHeight = tph;
				task.pixelCount = tpw * tph;
				
				double xProgLeft = x / pwd;
				double xProgRight = (x + tpw) / pwd;
				task.minX = (float) (minX * (1 - xProgLeft) + maxX * xProgLeft);
				task.maxX = (float) (minX * (1 - xProgRight) + maxX * xProgRight);
				task.minY = miny;
				task.maxY = maxy;
				
				tasks.add(task);
			}
		}
		tasks.sort((a, b) -> b.pixelCount - a.pixelCount);
		ExportThread.taskList.addAll(tasks);
		System.out.println("Sorted " + tasks.size() + " tasks high to low");
		
		int threadCount = threadCountMdl.getNumber().intValue();
		// don't create more threads than tasks
		threadCount = Math.min(threadCount, tasks.size());
		
		try {
			for(int i = 0; i < threadCount; i++) {
				new ExportThread(Gamemap.canvas, lastWorld).start();
			}
		} catch(LWJGLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(),
					"Export Failed", JOptionPane.ERROR_MESSAGE);
			Gamemap.setWorldLocked(false);
			return;
		}
		
		BufferedImage img = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
		new Thread(() -> {
			while(tasks.size() > 0) {
				try {
					Thread.sleep(16);
				} catch(InterruptedException ie) {}
				
				Iterator<ExportTask> iter = tasks.iterator();
				while(iter.hasNext()) {
					ExportTask task = iter.next();
					if(task.pixels != null) {
						task.merge(img);
						iter.remove();
					}
				}
			}
			try {
				ImageIO.write(img, "PNG", new File(dstFolder, "out.png"));
			} catch(IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.getMessage(),
						"Image Write Failed", JOptionPane.ERROR_MESSAGE);
				Gamemap.setWorldLocked(false);
				return;
			}
			
			onExportFinished();
			// attempt to force finalize to be called on the Cameras created here to release their flags
			System.gc();
		}).start();
	}
	
	// try to automatically pngcrush the resulting image to reduce file size
	private void attemptPNGCrush() {
		
	}
	
	private void onExportFinished() {
		attemptPNGCrush();

		JOptionPane.showMessageDialog(getParent(), "Export Complete");
		Gamemap.setWorldLocked(false);
	}
}
