package de.yadrone.apps.controlcenter.plugins.framesaver;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

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

public class FrameSaverPanel extends JPanel implements ICCPlugin
{
	private IARDrone drone;
	String filepath = "Saved_Stream_Pictures\\";
	boolean doSave;
	
	private BufferedImage image = null;
	private Result detectionResult;
	

	
	public FrameSaverPanel()
	{
		setBackground(Color.BLACK);
		

	}
	
	private long imageCount = 0;
	
	private void saveImage(final BufferedImage image)
	{
		System.out.println("SAVING IMAGE");
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
