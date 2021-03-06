/**
 * 
 */
package ecologylab.standalone.visualizer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.EnumSet;
import java.util.TooManyListenersException;

import javax.swing.JFrame;
import javax.swing.Timer;

import ecologylab.appframework.PropertiesAndDirectories;
import ecologylab.appframework.SingletonApplicationEnvironment;
import ecologylab.projection.PlateCarreeProjection;
import ecologylab.projection.Projection;
import ecologylab.projection.SameCoordinatesException;
import ecologylab.sensor.location.NMEAReader;
import ecologylab.sensor.location.gps.data.GPSConstants;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.sensor.location.gps.gui.GPSConnectionControls;
import ecologylab.sensor.location.gps.gui.GPSController;
import ecologylab.sensor.location.gps.listener.GPSDataUpdatedListener;
import ecologylab.sensor.location.gps.listener.GPSDataUpdater;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.SimplTypesScope;
import ecologylab.serialization.library.geom.Rectangle2DDoubleState;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

/**
 * @author Z O. Toups (zach@ecologylab.net)
 * 
 */
public class ProjectionVisualizer extends SingletonApplicationEnvironment implements GPSDataUpdatedListener,
		ActionListener, WindowListener, GPSController
{
	JFrame									mainFrame;

	GPSConstants						currentGPSReportedPos;

	Rectangle2DDoubleState	virtualField;

	Timer										t;

	NMEAReader							gps			= new NMEAReader();

	GPSDataUpdater					updater	= new GPSDataUpdater();

	int											w				= 400;

	int											h				= 200;

	/**
	 * @param applicationName
	 * @throws SIMPLTranslationException
	 * @throws IOException
	 * @throws NoSuchPortException
	 */
	public ProjectionVisualizer(String applicationName) throws SIMPLTranslationException,
			NoSuchPortException, IOException
	{
		super(applicationName);

		configure();
	}

	/**
	 * @param applicationName
	 * @param translationScope
	 * @param args
	 * @param prefsAssetVersion
	 * @throws SIMPLTranslationException
	 * @throws IOException
	 * @throws NoSuchPortException
	 */
	public ProjectionVisualizer(String applicationName, SimplTypesScope translationScope,
			String[] args, float prefsAssetVersion) throws SIMPLTranslationException, NoSuchPortException,
			IOException
	{
		super(applicationName, translationScope, (SimplTypesScope) null, args, prefsAssetVersion);

		configure();
	}

	/**
	 * @param applicationName
	 * @param args
	 * @throws SIMPLTranslationException
	 * @throws IOException
	 * @throws NoSuchPortException
	 */
	public ProjectionVisualizer(String applicationName, String[] args)
			throws SIMPLTranslationException, NoSuchPortException, IOException
	{
		super(applicationName, args);

		configure();
	}

	/**
	 * @param baseClass
	 * @param applicationName
	 * @param args
	 * @throws SIMPLTranslationException
	 * @throws IOException
	 * @throws NoSuchPortException
	 */
	public ProjectionVisualizer(Class baseClass, String applicationName, String[] args)
			throws SIMPLTranslationException, NoSuchPortException, IOException
	{
		super(baseClass, applicationName, args);

		configure();
	}

	/**
	 * @param baseClass
	 * @param applicationName
	 * @param translationScope
	 * @param args
	 * @param prefsAssetVersion
	 * @throws SIMPLTranslationException
	 * @throws IOException
	 * @throws NoSuchPortException
	 */
	public ProjectionVisualizer(Class baseClass, String applicationName,
			SimplTypesScope translationScope, String[] args, float prefsAssetVersion)
			throws SIMPLTranslationException, NoSuchPortException, IOException
	{
		super(baseClass, applicationName, translationScope, args, prefsAssetVersion);

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
		this.mainFrame = new JFrame(PropertiesAndDirectories.applicationName());

		this.mainFrame.addWindowListener(this);

		PlateCarreeProjection p;
		try
		{
			p = new PlateCarreeProjection(new GPSDatum(29.9611133702 - .05, -95.6697746507 + .05),
					new GPSDatum(29.9611133702 + .07, -95.6697746507 - .17), 200.0, 100.0,
					Projection.RotationConstraintMode.ANCHOR_POINTS);

			ProjectionVisualizerPanel panel = new ProjectionVisualizerPanel(new GPSDatum(29.9611133702,
					-95.6697746507), p, w, h);
			this.mainFrame.getContentPane().add(panel);

			this.updater.addDataUpdatedListener(this);
			this.updater.addDataUpdatedListener(panel);

			GPSConnectionControls v = new GPSConnectionControls(this);
			v.setPreferredSize(new Dimension(200, 200));

			this.mainFrame.getContentPane().add(v);

			this.mainFrame.setLayout(new FlowLayout());

			this.mainFrame.setVisible(true);
			this.mainFrame.setSize(w, h + 200);
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
	}

	/**
	 * @see ecologylab.sensor.location.gps.gui.GPSController#disconnectGPS()
	 */
	public void disconnectGPS()
	{
		this.gps.disconnect();

		this.gps.removeGPSDataListener(updater);
	}

	/**
	 * @param args
	 * @throws SIMPLTranslationException
	 * @throws IOException
	 * @throws NoSuchPortException
	 */
	public static void main(String[] args) throws SIMPLTranslationException, NoSuchPortException,
			IOException
	{
		new ProjectionVisualizer("Projection Visualizer");
	}

	/**
	 * @see ecologylab.sensor.location.gps.listener.GPSDataUpdatedListener#gpsDatumUpdated(ecologylab.sensor.location.gps.data.GPSDatum)
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
	{
	}

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

	/**
	 * @see ecologylab.sensor.location.gps.gui.GPSController#getGps()
	 */
	public NMEAReader getGps()
	{
		return gps;
	}

	/**
	 * Indicates which GPS update operations this is interested in; in this case, all of them.
	 */
	private static EnumSet<GPSUpdateInterest>	interestSet	= EnumSet.of(GPSUpdateInterest.LAT_LON,
																														GPSUpdateInterest.ALT,
																														GPSUpdateInterest.OTHERS);

	public EnumSet<GPSUpdateInterest> getInterestSet()
	{
		return interestSet;
	}

	public boolean connectGPS(CommPortIdentifier portId, int baud) throws PortInUseException,
			UnsupportedCommOperationException, IOException, TooManyListenersException,
			NoSuchPortException
	{
		this.gps.disconnect();

		this.gps.setup(portId, baud);

		this.gps.connect();

		this.gps.addGPSDataListener(updater);
		// this.gps.addGPSDataListener(new GPSDataPrinter());

		return this.gps.connected();
	}
}
