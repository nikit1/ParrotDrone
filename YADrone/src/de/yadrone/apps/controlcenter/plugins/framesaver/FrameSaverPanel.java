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

public class FrameSaverPanel extends JPanel implements ICCPlugin
{
	private IARDrone drone;
	String filepath = "Saved_Stream_Pictures\\";
	boolean doSave;
	
	private BufferedImage image = null;
	private Result detectionResult;
	private int everyXthFrame = 35;
	private int framesSeen = 0;
	private int framesSaved = 0;

	
	public FrameSaverPanel()
	{

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel label = new JLabel("FilePath");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.gridwidth = 3;
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		add(label, gbc_label);
		
		JLabel lblFilepath = new JLabel("FilePath");
		lblFilepath.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblFilepath = new GridBagConstraints();
		gbc_lblFilepath.insets = new Insets(0, 0, 5, 5);
		gbc_lblFilepath.gridx = 0;
		gbc_lblFilepath.gridy = 1;
		add(lblFilepath, gbc_lblFilepath);
		
		txtFilepath = new JTextField();
		txtFilepath.setText("Filepath");
		GridBagConstraints gbc_txtFilepath = new GridBagConstraints();
		gbc_txtFilepath.insets = new Insets(0, 0, 5, 5);
		gbc_txtFilepath.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFilepath.gridx = 1;
		gbc_txtFilepath.gridy = 1;
		add(txtFilepath, gbc_txtFilepath);
		txtFilepath.setColumns(10);
		
		JButton btnUpdate = new JButton("Update");
		GridBagConstraints gbc_btnUpdate = new GridBagConstraints();
		gbc_btnUpdate.insets = new Insets(0, 0, 5, 0);
		gbc_btnUpdate.gridx = 2;
		gbc_btnUpdate.gridy = 1;
		add(btnUpdate, gbc_btnUpdate);
		
		JLabel lblSaveXthFrame = new JLabel("Save Xth Frame");
		GridBagConstraints gbc_lblSaveXthFrame = new GridBagConstraints();
		gbc_lblSaveXthFrame.gridwidth = 3;
		gbc_lblSaveXthFrame.insets = new Insets(0, 0, 5, 0);
		gbc_lblSaveXthFrame.gridx = 0;
		gbc_lblSaveXthFrame.gridy = 2;
		add(lblSaveXthFrame, gbc_lblSaveXthFrame);
		
		JLabel lblXthframe = new JLabel("XthFrame");
		GridBagConstraints gbc_lblXthframe = new GridBagConstraints();
		gbc_lblXthframe.insets = new Insets(0, 0, 5, 5);
		gbc_lblXthframe.gridx = 0;
		gbc_lblXthframe.gridy = 3;
		add(lblXthframe, gbc_lblXthframe);
		
		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.insets = new Insets(0, 0, 5, 5);
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 3;
		add(spinner, gbc_spinner);
		
		JButton button = new JButton("Update");
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 0);
		gbc_button.gridx = 2;
		gbc_button.gridy = 3;
		add(button, gbc_button);
		
		JLabel lblCurrentlySaving = new JLabel("Currently Saving?");
		GridBagConstraints gbc_lblCurrentlySaving = new GridBagConstraints();
		gbc_lblCurrentlySaving.gridwidth = 3;
		gbc_lblCurrentlySaving.insets = new Insets(0, 0, 5, 0);
		gbc_lblCurrentlySaving.gridx = 0;
		gbc_lblCurrentlySaving.gridy = 4;
		add(lblCurrentlySaving, gbc_lblCurrentlySaving);
		
		JLabel lblDosavevlaue = new JLabel("doSaveVlaue");
		GridBagConstraints gbc_lblDosavevlaue = new GridBagConstraints();
		gbc_lblDosavevlaue.insets = new Insets(0, 0, 5, 5);
		gbc_lblDosavevlaue.gridx = 0;
		gbc_lblDosavevlaue.gridy = 5;
		add(lblDosavevlaue, gbc_lblDosavevlaue);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 5;
		add(panel, gbc_panel);
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("True");
		panel.add(rdbtnNewRadioButton);
		
		JRadioButton rdbtnFalse = new JRadioButton("False");
		panel.add(rdbtnFalse);
		
		JButton button_1 = new JButton("Update");
		GridBagConstraints gbc_button_1 = new GridBagConstraints();
		gbc_button_1.insets = new Insets(0, 0, 5, 0);
		gbc_button_1.gridx = 2;
		gbc_button_1.gridy = 5;
		add(button_1, gbc_button_1);
		
		JLabel lblImageFileType = new JLabel("Image File Type");
		GridBagConstraints gbc_lblImageFileType = new GridBagConstraints();
		gbc_lblImageFileType.gridwidth = 3;
		gbc_lblImageFileType.insets = new Insets(0, 0, 5, 5);
		gbc_lblImageFileType.gridx = 0;
		gbc_lblImageFileType.gridy = 6;
		add(lblImageFileType, gbc_lblImageFileType);
		
		JLabel lbljpg = new JLabel(".Jpg");
		GridBagConstraints gbc_lbljpg = new GridBagConstraints();
		gbc_lbljpg.insets = new Insets(0, 0, 0, 5);
		gbc_lbljpg.gridx = 0;
		gbc_lbljpg.gridy = 7;
		add(lbljpg, gbc_lbljpg);
		

	}
	
	private long imageCount = 0;
	private JTextField txtFilepath;
	
	private void saveImage(final BufferedImage image)
	{
		framesSeen++;
		if(framesSeen%everyXthFrame == 0 && doSave){
			framesSaved++;
			try {
			String filePath = filepath + "image"+framesSaved+".jpg";
			File toSaveTo = new File(filePath);
			
				ImageIO.write(image, "jpg", toSaveTo);
			} catch (IOException e) {
				System.err.println("Failed to save a frame to image");
				e.printStackTrace();
			}
		}
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
		doSave = true;
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
		return new Dimension(650, 390);
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
