package de.yadrone.apps.controlcenter.plugins.searchroutine;

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
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.video.ImageListener;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class searchroutine extends JPanel implements ICCPlugin{

	
	private IARDrone drone;
	private String code;
	private String orientation;
	
	private BufferedImage image = null;
	private Result detectionResult;
	public int hoverLength = 0;
	private boolean runRoutine = false;
	
	public searchroutine()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 0;
		add(panel_1, gbc_panel_1);
		
		JPanel panel = new JPanel(){
			public void paint(Graphics g){
				if (image != null)
				{
					g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
					if (detectionResult != null)
					{
						ResultPoint[] points = detectionResult.getResultPoints();
						ResultPoint a = points[1]; // top-left
						ResultPoint b = points[2]; // top-right
						ResultPoint c = points[0]; // bottom-left
						ResultPoint d = points[3]; // alignment point (bottom-right)s
						
						//when scanning?
						g.setColor(Color.GREEN);
						
						g.drawPolygon(new int[] {(int)a.getX(),(int)b.getX(),(int)d.getX(),(int)c.getX()}, 
								      new int[] {(int)a.getY(),(int)b.getY(),(int)d.getY(),(int)c.getY()}, 4);
						
						// font of the QR code displayed on screen.
						g.setColor(Color.RED);
						g.setFont(new Font("SansSerif", Font.BOLD, 14));
						g.drawString(code, (int)a.getX(), (int)a.getY());
						g.drawString(orientation, (int)a.getX(), (int)a.getY() + 20);
						
					}
				}
				else
				{
					g.setColor(Color.WHITE);
					g.drawString("Waiting for Video ...", 10, 20);
				}
			}
		};
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridheight = 6;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 3;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		
		JLabel lblRoutine = new JLabel("Routine 1");
		GridBagConstraints gbc_lblRoutine = new GridBagConstraints();
		gbc_lblRoutine.insets = new Insets(0, 0, 5, 5);
		gbc_lblRoutine.gridx = 1;
		gbc_lblRoutine.gridy = 1;
		add(lblRoutine, gbc_lblRoutine);
		
		JButton btnBasicRoutine = new JButton("Toggle Routine");
		btnBasicRoutine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				runRoutine = !runRoutine;
				lblRunning.setText(runRoutine + "");
				
			}
		});
		
		lblRunning = new JLabel("Running");
		GridBagConstraints gbc_lblRunning = new GridBagConstraints();
		gbc_lblRunning.insets = new Insets(0, 0, 5, 5);
		gbc_lblRunning.gridx = 0;
		gbc_lblRunning.gridy = 2;
		add(lblRunning, gbc_lblRunning);
		GridBagConstraints gbc_btnBasicRoutine = new GridBagConstraints();
		gbc_btnBasicRoutine.insets = new Insets(0, 0, 5, 5);
		gbc_btnBasicRoutine.gridx = 1;
		gbc_btnBasicRoutine.gridy = 2;
		add(btnBasicRoutine, gbc_btnBasicRoutine);
		
		JLabel lblRoutine_1 = new JLabel("Routine 2 - Proceed with Caution");
		GridBagConstraints gbc_lblRoutine_1 = new GridBagConstraints();
		gbc_lblRoutine_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblRoutine_1.gridx = 1;
		gbc_lblRoutine_1.gridy = 4;
		add(lblRoutine_1, gbc_lblRoutine_1);
		
		JButton btnRoutine = new JButton("Do you dare?");
		btnRoutine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doYouDareRoutine();
			}
		});
		GridBagConstraints gbc_btnRoutine = new GridBagConstraints();
		gbc_btnRoutine.anchor = GridBagConstraints.SOUTH;
		gbc_btnRoutine.insets = new Insets(0, 0, 0, 5);
		gbc_btnRoutine.gridx = 1;
		gbc_btnRoutine.gridy = 5;
		add(btnRoutine, gbc_btnRoutine);
		
		panel.setBackground(Color.BLACK);

	}

	
	protected void doYouDareRoutine() {
		CommandManager commandManager = drone.getCommandManager();
		commandManager.takeOff();
		commandManager.forward(10).doFor(500);
		commandManager.backward(10).doFor(500);
		commandManager.up(10).doFor(500);
		commandManager.backward(10).doFor(500);
		commandManager.goRight(10).doFor(500);
		commandManager.landing();
	}
		

	private void setImage(final BufferedImage image)
	{
		this.image = image;
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				repaint();
			}
		});
		
		// try to detect QR code
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		// decode the barcode (if only QR codes are used, the QRCodeReader might be a better choice)
		MultiFormatReader reader = new MultiFormatReader();

		try
		{
			detectionResult = reader.decode(bitmap);
			
			code = detectionResult.getText();
			
			ResultPoint[] points = detectionResult.getResultPoints();
			ResultPoint a = points[1]; // top-left
			ResultPoint b = points[2]; // top-right
			ResultPoint c = points[0]; // bottom-left
			ResultPoint d = points[3]; // alignment point (bottom-right)
			
			// Find the degree of the rotation that is needed

			double z = Math.abs(a.getX() - b.getX());
			double x = Math.abs(a.getY() - b.getY());
			double theta = Math.atan(x / z); // degree in rad (+- PI/2)

			theta = theta * (180 / Math.PI); // convert to degree

			if ((b.getX() < a.getX()) && (b.getY() > a.getY()))
			{ // code turned more than 90° clockwise
				theta = 180 - theta;
			}
			else if ((b.getX() < a.getX()) && (b.getY() < a.getY()))
			{ // code turned more than 180° clockwise
				theta = 180 + theta;
			}
			else if ((b.getX() > a.getX()) && (b.getY() < a.getY()))
			{ // code turned more than 270 clockwise
				theta = 360 - theta;
			}
			
			orientation = (int)theta + " °";
			
			if(runRoutine){
				// hover up to 10 times for .1ms and then land.
				if (hoverLength < 1){
				//	drone.getCommandManager().hover();
					hoverLength++;
				} else {
					drone.landing();
				}
			}
		} 
		catch (ReaderException e) 
		{
			// no code found.
			detectionResult = null;
			orientation = "n/a °";
			code = "n/a";
		}
	}

	private ImageListener imageListener = new ImageListener() {
		public void imageUpdated(BufferedImage image)
		{
			setImage(image);
		}
	};
	private JLabel lblRunning;
	
	public void activate(IARDrone drone)
	{
		this.drone = drone;
		drone.getVideoManager().addImageListener(imageListener);
		this.runRoutine = false;
		this.lblRunning.setText(runRoutine+ "");
	}

	public void deactivate()
	{
		drone.getVideoManager().removeImageListener(null);
	}

	public String getTitle()
	{
		return "Search Routine";
	}
	
	public String getDescription()
	{
		return "Runs a basic routine and scans for QR code.";
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
