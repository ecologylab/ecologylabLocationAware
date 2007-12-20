/**
 * 
 */
package ecologylab.standalone.visualizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.TooManyListenersException;

import javax.swing.JFrame;
import javax.swing.Timer;

import ecologylab.appframework.ApplicationEnvironment;
import ecologylab.projection.PlateCarreeProjection;
import ecologylab.projection.Projection;
import ecologylab.projection.SameCoordinatesException;
import ecologylab.sensor.gps.GPS;
import ecologylab.sensor.gps.data.GPSDatum;
import ecologylab.sensor.gps.listener.GPSDataUpdatedListener;
import ecologylab.sensor.gps.listener.GPSDataUpdater;
import ecologylab.xml.TranslationSpace;
import ecologylab.xml.XMLTranslationException;
import ecologylab.xml.library.geom.Rectangle2DDoubleState;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class ProjectionVisualizer extends ApplicationEnvironment implements GPSDataUpdatedListener, ActionListener,
		WindowListener
{
	JFrame						mainFrame;

	GPSDatum						currentGPSReportedPos;

	Rectangle2DDoubleState	virtualField;

	Timer							t;

	GPS							gps;

	GPSDataUpdater				updater	= new GPSDataUpdater();

	int							w			= 400;

	int							h			= 200;

	/**
	 * @param applicationName
	 * @throws XMLTranslationException
	 * @throws IOException
	 * @throws NoSuchPortException
	 */
	public ProjectionVisualizer(String applicationName) throws XMLTranslationException, NoSuchPortException, IOException
	{
		super(applicationName);

		configure();
	}

	/**
	 * @param applicationName
	 * @param translationSpace
	 * @param args
	 * @param prefsAssetVersion
	 * @throws XMLTranslationException
	 * @throws IOException
	 * @throws NoSuchPortException
	 */
	public ProjectionVisualizer(String applicationName, TranslationSpace translationSpace, String[] args,
			float prefsAssetVersion) throws XMLTranslationException, NoSuchPortException, IOException
	{
		super(applicationName, translationSpace, args, prefsAssetVersion);

		configure();
	}

	/**
	 * @param applicationName
	 * @param args
	 * @throws XMLTranslationException
	 * @throws IOException
	 * @throws NoSuchPortException
	 */
	public ProjectionVisualizer(String applicationName, String[] args) throws XMLTranslationException,
			NoSuchPortException, IOException
	{
		super(applicationName, args);

		configure();
	}

	/**
	 * @param baseClass
	 * @param applicationName
	 * @param args
	 * @throws XMLTranslationException
	 * @throws IOException
	 * @throws NoSuchPortException
	 */
	public ProjectionVisualizer(Class baseClass, String applicationName, String[] args) throws XMLTranslationException,
			NoSuchPortException, IOException
	{
		super(baseClass, applicationName, args);

		configure();
	}

	/**
	 * @param baseClass
	 * @param applicationName
	 * @param translationSpace
	 * @param args
	 * @param prefsAssetVersion
	 * @throws XMLTranslationException
	 * @throws IOException
	 * @throws NoSuchPortException
	 */
	public ProjectionVisualizer(Class baseClass, String applicationName, TranslationSpace translationSpace,
			String[] args, float prefsAssetVersion) throws XMLTranslationException, NoSuchPortException, IOException
	{
		super(baseClass, applicationName, translationSpace, args, prefsAssetVersion);

		configure();
	}

	private void configure() throws NoSuchPortException, IOException
	{
		debug("configuring visualizer");
		configureFromPrefs();
		setupVisualization();

		debug("starting repainter");
		t = new Timer(200, this);
		t.start();
	}

	/**
	 * 
	 */
	private void setupVisualization()
	{
		this.mainFrame = new JFrame();
		
		this.mainFrame.addWindowListener(this);

		PlateCarreeProjection p;
		try
		{
			p = new PlateCarreeProjection(new GPSDatum(29.9611133702 - .05, -95.6697746507 + .05), new GPSDatum(
					29.9611133702 + .07, -95.6697746507 - .17), 200.0, 100.0,
					Projection.RotationConstraintMode.CARDINAL_DIRECTIONS);

			ProjectionVisualizerPanel panel = new ProjectionVisualizerPanel(new GPSDatum(29.9611133702, -95.6697746507),
					p, w, h);
			this.mainFrame.getContentPane().add(panel);

			this.updater.addDataUpdatedListener(this);
			this.updater.addDataUpdatedListener(panel);

			this.mainFrame.setVisible(true);
			this.mainFrame.setSize(w, h);
			this.mainFrame.pack();
			this.mainFrame.invalidate();
		}
		catch (SameCoordinatesException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @throws IOException
	 * @throws NoSuchPortException
	 * 
	 */
	private void configureFromPrefs() throws NoSuchPortException, IOException
	{
		/*
		 * this.gps = new GPS("COM41", 115200); try { this.gps.connect(); } catch (PortInUseException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch (UnsupportedCommOperationException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch (TooManyListenersException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 * 
		 * this.gps.addGPSDataListener(updater); // this.gps.addGPSDataListener(new GPSDataPrinter());
		 */
	}

	/**
	 * @param args
	 * @throws XMLTranslationException
	 * @throws IOException
	 * @throws NoSuchPortException
	 */
	public static void main(String[] args) throws XMLTranslationException, NoSuchPortException, IOException
	{
		new ProjectionVisualizer("Projection Visualizer");
	}

	/**
	 * @see ecologylab.sensor.gps.listener.GPSDataUpdatedListener#gpsDatumUpdated(ecologylab.sensor.gps.data.GPSDatum)
	 */
	public void gpsDatumUpdated(GPSDatum datum)
	{
		this.currentGPSReportedPos = datum;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		mainFrame.repaint();
	}

	/**
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated(WindowEvent e)
	{
	}

	/**
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	public void windowClosed(WindowEvent e)
	{}

	/**
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent e)
	{
		debug("Window closed.");
		
		t.stop();
		
		System.exit(1);
	}

	/**
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	public void windowDeactivated(WindowEvent e)
	{
	}

	/**
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	public void windowDeiconified(WindowEvent e)
	{
	}

	/**
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	public void windowIconified(WindowEvent e)
	{
	}

	/**
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	public void windowOpened(WindowEvent e)
	{
	}
}