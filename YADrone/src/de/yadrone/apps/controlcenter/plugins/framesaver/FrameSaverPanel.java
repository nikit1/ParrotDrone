package de.yadrone.apps.controlcenter.plugins.framesaver;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.IARDrone;
import de.yadrone.base.video.ImageListener;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.ButtonGroup;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FrameSaverPanel extends JPanel implements ICCPlugin
{
	private IARDrone drone;
	String filepath = "Saved_Stream_Pictures\\";
	
	
	private BufferedImage image = null;
	private Result detectionResult;
	private int everyXthFrame = 35;
	private int framesSeen = 0;
	private int framesSaved = 0;
	private boolean doSave = false;
	
	
	private JTextField editFilepath;
	private JLabel lblFilepath;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JLabel lblFrameSaveInterval;
	private JSpinner spnrFrameSaveInterval;
	private JRadioButton rdbtnDoSaveTrue;
	private JRadioButton rdbtnDoSaveFalse;
	private JLabel lblDoSaveValue;
	private JLabel lblImageFileType_1;
	private JLabel lblVlunumsaved;

	
	private void refreshFilePath(){
		this.lblFilepath.setText(filepath);
		this.editFilepath.setText(filepath);
	}
	
	private void refreshFrameSaveInterval(){
		this.lblFrameSaveInterval.setText("" + everyXthFrame);
		this.spnrFrameSaveInterval.setValue(everyXthFrame);
	}
	
	private void refreshDoSave(){
		this.lblDoSaveValue.setText("" + doSave);
			rdbtnDoSaveTrue.setSelected(doSave);
			rdbtnDoSaveFalse.setSelected(!doSave);
	}
	
	
	public FrameSaverPanel()
	{

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel label = new JLabel("FilePath");
		label.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.gridwidth = 3;
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		add(label, gbc_label);
		
		lblFilepath = new JLabel("FilePath");
		lblFilepath.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblFilepath = new GridBagConstraints();
		gbc_lblFilepath.insets = new Insets(0, 0, 5, 5);
		gbc_lblFilepath.gridx = 0;
		gbc_lblFilepath.gridy = 1;
		add(lblFilepath, gbc_lblFilepath);
		
		editFilepath = new JTextField();
		editFilepath.setText("Filepath");
		GridBagConstraints gbc_editFilepath = new GridBagConstraints();
		gbc_editFilepath.insets = new Insets(0, 0, 5, 5);
		gbc_editFilepath.fill = GridBagConstraints.HORIZONTAL;
		gbc_editFilepath.gridx = 1;
		gbc_editFilepath.gridy = 1;
		add(editFilepath, gbc_editFilepath);
		editFilepath.setColumns(10);
		
		JButton btnFilepathUpdate = new JButton("Update");
		btnFilepathUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				filepath = editFilepath.getText();
				refreshFilePath();
			}
		});
		GridBagConstraints gbc_btnFilepathUpdate = new GridBagConstraints();
		gbc_btnFilepathUpdate.insets = new Insets(0, 0, 5, 0);
		gbc_btnFilepathUpdate.gridx = 2;
		gbc_btnFilepathUpdate.gridy = 1;
		add(btnFilepathUpdate, gbc_btnFilepathUpdate);
		
		JLabel lblSaveXthFrame = new JLabel("Save Xth Frame");
		lblSaveXthFrame.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblSaveXthFrame = new GridBagConstraints();
		gbc_lblSaveXthFrame.gridwidth = 3;
		gbc_lblSaveXthFrame.insets = new Insets(0, 0, 5, 0);
		gbc_lblSaveXthFrame.gridx = 0;
		gbc_lblSaveXthFrame.gridy = 2;
		add(lblSaveXthFrame, gbc_lblSaveXthFrame);
		
		lblFrameSaveInterval = new JLabel("XthFrame");
		GridBagConstraints gbc_lblFrameSaveInterval = new GridBagConstraints();
		gbc_lblFrameSaveInterval.insets = new Insets(0, 0, 5, 5);
		gbc_lblFrameSaveInterval.gridx = 0;
		gbc_lblFrameSaveInterval.gridy = 3;
		add(lblFrameSaveInterval, gbc_lblFrameSaveInterval);
		
		spnrFrameSaveInterval = new JSpinner();
		spnrFrameSaveInterval.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_spnrFrameSaveInterval = new GridBagConstraints();
		gbc_spnrFrameSaveInterval.insets = new Insets(0, 0, 5, 5);
		gbc_spnrFrameSaveInterval.gridx = 1;
		gbc_spnrFrameSaveInterval.gridy = 3;
		add(spnrFrameSaveInterval, gbc_spnrFrameSaveInterval);
		
		JButton btnFrameSaveInterval = new JButton("Update");
		btnFrameSaveInterval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				everyXthFrame = (Integer) spnrFrameSaveInterval.getValue();
				refreshFrameSaveInterval();
			}
		});
		
		GridBagConstraints gbc_btnFrameSaveInterval = new GridBagConstraints();
		gbc_btnFrameSaveInterval.insets = new Insets(0, 0, 5, 0);
		gbc_btnFrameSaveInterval.gridx = 2;
		gbc_btnFrameSaveInterval.gridy = 3;
		add(btnFrameSaveInterval, gbc_btnFrameSaveInterval);
		
		JLabel lblCurrentlySaving = new JLabel("Currently Saving?");
		lblCurrentlySaving.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblCurrentlySaving = new GridBagConstraints();
		gbc_lblCurrentlySaving.gridwidth = 3;
		gbc_lblCurrentlySaving.insets = new Insets(0, 0, 5, 0);
		gbc_lblCurrentlySaving.gridx = 0;
		gbc_lblCurrentlySaving.gridy = 4;
		add(lblCurrentlySaving, gbc_lblCurrentlySaving);
		
		lblDoSaveValue = new JLabel("doSaveVlaue");
		GridBagConstraints gbc_lblDoSaveValue = new GridBagConstraints();
		gbc_lblDoSaveValue.insets = new Insets(0, 0, 5, 5);
		gbc_lblDoSaveValue.gridx = 0;
		gbc_lblDoSaveValue.gridy = 5;
		add(lblDoSaveValue, gbc_lblDoSaveValue);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.VERTICAL;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 5;
		add(panel, gbc_panel);
		
		rdbtnDoSaveTrue = new JRadioButton("True");
		buttonGroup.add(rdbtnDoSaveTrue);
		panel.add(rdbtnDoSaveTrue);
		
		rdbtnDoSaveFalse = new JRadioButton("False");
		buttonGroup.add(rdbtnDoSaveFalse);
		panel.add(rdbtnDoSaveFalse);
		
		JButton btnDoSaveUpdate = new JButton("Update");
		btnDoSaveUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doSave = rdbtnDoSaveTrue.isSelected();
				refreshDoSave();
			}
		});
		GridBagConstraints gbc_btnDoSaveUpdate = new GridBagConstraints();
		gbc_btnDoSaveUpdate.insets = new Insets(0, 0, 5, 0);
		gbc_btnDoSaveUpdate.gridx = 2;
		gbc_btnDoSaveUpdate.gridy = 5;
		add(btnDoSaveUpdate, gbc_btnDoSaveUpdate);
		
		JLabel lblImageFileType = new JLabel("Image File Type");
		lblImageFileType.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblImageFileType = new GridBagConstraints();
		gbc_lblImageFileType.gridwidth = 3;
		gbc_lblImageFileType.insets = new Insets(0, 0, 5, 0);
		gbc_lblImageFileType.gridx = 0;
		gbc_lblImageFileType.gridy = 6;
		add(lblImageFileType, gbc_lblImageFileType);
		
		lblImageFileType_1 = new JLabel(".Jpg");
		GridBagConstraints gbc_lblImageFileType_1 = new GridBagConstraints();
		gbc_lblImageFileType_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblImageFileType_1.gridx = 0;
		gbc_lblImageFileType_1.gridy = 7;
		add(lblImageFileType_1, gbc_lblImageFileType_1);
		
		JLabel lblNewLabel = new JLabel("Status");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 3;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 8;
		add(lblNewLabel, gbc_lblNewLabel);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 9;
		add(panel_1, gbc_panel_1);
		
		JLabel lblNumSaved = new JLabel("Num Saved: ");
		panel_1.add(lblNumSaved);
		
		lblVlunumsaved = new JLabel("vluNumSaved");
		panel_1.add(lblVlunumsaved);
		
		this.refreshDoSave();
		this.refreshFilePath();
		this.refreshFrameSaveInterval();
		this.refreshFramesSaved();
		
	}
	

	
	private void saveImage(final BufferedImage image)
	{
		framesSeen++;
		if(framesSeen%everyXthFrame == 0 && doSave){

			try {
			String filePath = filepath + "image"+framesSaved+".jpg";
			File toSaveTo = new File(filePath);
			ImageIO.write(image, "jpg", toSaveTo);
			framesSaved++;
			this.refreshFramesSaved();
			} catch (IOException e) {
				System.err.println("Failed to save a frame to image");
				e.printStackTrace();
			}
		}
	}
	
	private void refreshFramesSaved() {
		this.lblVlunumsaved.setText(this.framesSaved + "");
	}

	public void activate(IARDrone drone)
	{
		this.drone = drone;
		drone.getVideoManager().addImageListener(new ImageListener() {
			public void imageUpdated(BufferedImage image)
			{
				saveImage(image);
			}
		});
		doSave = false;
		this.refreshDoSave();
	}

	public void deactivate()
	{
		drone.getVideoManager().removeImageListener(null);
		doSave = false;
	}

	public String getTitle()
	{
		return "Frame Saver";
	}
	
	public String getDescription()
	{
		return "Saves frames from the video stream as jpgs";
	}

	public boolean isVisual()
	{
		return true;
	}

	public Dimension getScreenSize()
	{
		return new Dimension(450, 280);
	}
	
	public Point getScreenLocation()
	{
		return new Point(100, 100);
	}

	public JPanel getPanel()
	{
		return this;
	}
}
