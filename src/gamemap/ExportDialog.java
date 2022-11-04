package gamemap;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import gamemap.world.World;

public class ExportDialog extends Dialog {
	private static final int MARGIN_SIZE = 8;

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
	
	private SpinnerNumberModel	threadCount		= new SpinnerNumberModel(2, 1, 4, 1);
	private JTextField			friendlyName	= new JTextField(12);
	private JTextField			mapId			= new JTextField(12);
	private JFormattedTextField	unitsPerPixel	= new JFormattedTextField();
	private JFormattedTextField	dstFolderName	= new JFormattedTextField();
	private JFormattedTextField	minX			= new JFormattedTextField();
	private JFormattedTextField	minY			= new JFormattedTextField();
	private JFormattedTextField	maxX			= new JFormattedTextField();
	private JFormattedTextField	maxY			= new JFormattedTextField();

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
				new JLabel("Folder Name"), dstFolderName,
				new JLabel("Map Id"), mapId,
				new JLabel("Threads to use"), new JSpinner(threadCount));
		
		unitsPerPixel.setColumns(8);
		
		JPanel sizing = makeTable("Sizing",
				new JLabel("Units Per Pixel"), unitsPerPixel,
				new JLabel("Min X"), minX,
				new JLabel("Min Y"), minY,
				new JLabel("Max X"), maxX,
				new JLabel("Max Y"), maxY);

		JButton exportBtn = new JButton("Export");
		exportBtn.addActionListener(e -> {
			// TODO check if folder name is allowed first
			Gamemap.setWorldLocked(true);
			// TODO trigger multithreaded exporting and show pane on finish
			setVisible(false);
		});

		JPanel content = new JPanel();
		content.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(MARGIN_SIZE / 2, MARGIN_SIZE, MARGIN_SIZE / 2, MARGIN_SIZE / 2);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 4;
		content.add(general, gbc);

		gbc.insets = new Insets(MARGIN_SIZE / 2, 0, MARGIN_SIZE, MARGIN_SIZE);
		gbc.gridheight = 5;
		gbc.gridx = 1;
		content.add(sizing, gbc);

		gbc.insets = new Insets(0, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE / 2);
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 4;
		content.add(exportBtn, gbc);
		
		add(content);
		pack();
	}
	
	public void showDialogFor(World world) {
		int maxThreads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
		threadCount.setMaximum(maxThreads);
		int threads = ((Number) threadCount.getValue()).intValue();
		if(threads > maxThreads) {
			threadCount.setValue(maxThreads);
		}
		
		// TODO propagate text fields
		
		setLocationRelativeTo(getParent());
		setVisible(true);
	}
}
