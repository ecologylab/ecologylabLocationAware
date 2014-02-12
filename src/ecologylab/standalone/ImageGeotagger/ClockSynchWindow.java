/**
 * 
 */
package ecologylab.standalone.ImageGeotagger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.constants.TiffTagConstants;

import ecologylab.rendering.panel.ImagePanel;
import ecologylab.standalone.GeoClient;
import ecologylab.standalone.ImageGeotagger.DirectoryMonitor.ImgArrivesMonitor;

/**
 * Provides a way to make a clock synch window appear. The clock synch window shows the current time
 * according to the GPS service. It prompts the user to take a photo with the camera, which will
 * then upload the image to the image monitoring directory. This method then looks at the time stamp
 * for that image, and asks the user to report the GPS time shown in the image. The combination of
 * GPS time and the photo's timestamp can be used to determine the offset between GPS time and the
 * camera clock.
 * 
 * @author Zachary O. Toups (ztoups@nmsu.edu)
 */
public class ClockSynchWindow implements ActionListener
{
	ImgArrivesMonitor	iam;

	GeoClient					gc;

	JFrame						jf;

	JLabel						timeLabel;

	ImagePanel				imgPanel;

	JTextField				timeInputField;

	Timer							t;

	JButton						submitButton;

	private File			srcImg;

	private long			offset;

	public static long getOffsetFromSynchWindow(File dirToMonitor, GeoClient gc) throws Exception
	{
		System.out.println("showing offset synch window");
		final ClockSynchWindow csw = new ClockSynchWindow(dirToMonitor, gc);

		final CountDownLatch latch = new CountDownLatch(1);

		csw.jf.addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{
			}

			@Override
			public void windowIconified(WindowEvent e)
			{
			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{
			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
			}

			@Override
			public void windowClosed(WindowEvent e)
			{
				latch.countDown();
			}

			@Override
			public void windowActivated(WindowEvent e)
			{
			}
		});

		System.out.println("Starting up window...");
		csw.start();

		System.out.println("...waiting for user...");
		latch.await();

		System.out.println("...done!");
		System.out.println(csw.getOffset());

		return csw.getOffset();
	}
	
	public static void main(String[] args)
	{

	}

	public ClockSynchWindow(File dirToMonitor, GeoClient gc) throws Exception
	{
		this.iam = new ImgArrivesMonitor(dirToMonitor, this);
		this.gc = gc;

		timeLabel = new JLabel();
		timeLabel.setMinimumSize(new Dimension(600, 100));
		timeLabel.setPreferredSize(new Dimension(600, 100));
		timeLabel.setFont(new Font("Helvetica", Font.PLAIN, 44));
		imgPanel = new ImagePanel();
		imgPanel.setMinimumSize(new Dimension(600, 300));
		imgPanel.setPreferredSize(new Dimension(600, 300));
		timeInputField = new JTextField();
		timeInputField.setMinimumSize(new Dimension(300, 100));
		timeInputField.setPreferredSize(new Dimension(300, 100));
		submitButton = new JButton("submit");
		submitButton.addActionListener(this);
		submitButton.setEnabled(false);
		submitButton.setMinimumSize(new Dimension(200, 100));
		submitButton.setPreferredSize(new Dimension(200, 100));

		jf = new JFrame();
		jf.getContentPane().setLayout(new BorderLayout());

		jf.getContentPane().add(timeLabel, BorderLayout.PAGE_START);

		jf.getContentPane().add(imgPanel, BorderLayout.CENTER);

		JPanel tempPanel = new JPanel();
		tempPanel.setMinimumSize(new Dimension(600, 100));
		tempPanel.setPreferredSize(new Dimension(600, 100));
		tempPanel.setLayout(new FlowLayout());
		tempPanel.add(timeInputField);
		tempPanel.add(submitButton);

		jf.getContentPane().add(tempPanel, BorderLayout.PAGE_END);

		jf.setSize(600, 400);
		jf.pack();
	}

	public void start()
	{
		jf.setVisible(true);
		iam.start();

		t = new Timer(100, this);
		t.setRepeats(true);
		t.start();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		synchronized (this)
		{
			long timeInMillis = -1;
			if (gc.updateLocation().gpsData != null)
				timeInMillis = gc.updateLocation().gpsData.getTimeInMillis();
			if (ImgArrivesMonitor.IMAGE_ARRIVED == e.getActionCommand())
			{
				timeInputField.setText(Long.toHexString(timeInMillis));

				BufferedImage rawImage = null;
				try
				{
					srcImg = (File) e.getSource();
					rawImage = ImageIO.read(srcImg);
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}

				if (rawImage != null)
				{
					imgPanel.setSrcImg(rawImage);
					imgPanel.repaint();
				}

				submitButton.setEnabled(true);
			}
			if (e.getSource() == submitButton && srcImg != null)
			{ // calculate offset
				IImageMetadata metadata = null;
				JpegImageMetadata jpegMetadata = null;

				try
				{
					metadata = Sanselan.getMetadata(srcImg);

					if (metadata == null || !(metadata instanceof JpegImageMetadata))
						return;

					jpegMetadata = (JpegImageMetadata) metadata;

					String dateTimeUTC;

					dateTimeUTC = jpegMetadata.findEXIFValue(TiffTagConstants.TIFF_TAG_DATE_TIME)
							.getStringValue();

					DateFormat df = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss");
					java.util.Date d = df.parse(dateTimeUTC);

					offset = d.getTime() - timeInMillis;
					t.stop();
					jf.setVisible(false);
					jf.dispose();
				}
				catch (ImageReadException | IOException | ParseException e1)
				{
					e1.printStackTrace();
				}
			}
			else
			{ // update other things
				timeLabel.setText(Long.toHexString(timeInMillis));
			}
		}
	}

	/**
	 * @return the offset
	 */
	public long getOffset()
	{
		return offset;
	}
}