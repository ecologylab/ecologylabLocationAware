/**
 * 
 */
package ecologylab.services.logging;

import java.io.IOException;
import java.util.Calendar;
import java.util.TooManyListenersException;

import stec.jenie.NativeException;
import ecologylab.generic.Generic;
import ecologylab.oodss.logging.Logging;
import ecologylab.oodss.logging.MixedInitiativeOp;
import ecologylab.sensor.location.NMEAReader;
import ecologylab.sensor.location.NMEAStringListener;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.sensor.location.gps.listener.GPSDataUpdater;
import ecologylab.sensor.network.wireless.RunnableWiFiAdapter;
import ecologylab.sensor.network.wireless.data.WiFiAdapterStatus;
import ecologylab.sensor.network.wireless.listener.WiFiStringDataListener;
import ecologylab.serialization.annotations.simpl_composite;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

/**
 * Represents a moment of data from both a location sensor and a network sensor
 * with a time stamp.
 * 
 * These may be logged to create a set of data about locations, times, and
 * network status.
 * 
 * @author Z O. Toups (zach@ecologylab.net)
 */
@simpl_inherit public class WiFiGPSStatusOp extends MixedInitiativeOp implements
		WiFiStringDataListener, NMEAStringListener
{
	/**
	 * The status from the location system at the moment this was recorded.
	 * Becomes frozen when the event is recorded.
	 */
	@simpl_composite protected GPSDatum				locationStatus;

	private GPSDataUpdater							locationStatusUpdater								= null;

	/**
	 * Controls access to the instantiation of locationStatusUpdater, which is
	 * constructed lazilly.
	 */
	private final Object								locationStatusUpdaterInstantiationSemaphore	= new Object();

	/**
	 * The status from the network system at the moment this was recorded.
	 * Becomes frozen when the event is recorded.
	 */
	@simpl_composite protected WiFiAdapterStatus	netStatus;

	@simpl_scalar
	protected Calendar					utcTime;

	/**
	 * 
	 */
	public WiFiGPSStatusOp()
	{
		super();

		this.locationStatus = new GPSDatum();
		this.netStatus = new WiFiAdapterStatus();
	}

	private GPSDataUpdater locationStatusUpdater()
	{
		if (locationStatusUpdater == null)
		{
			synchronized (locationStatusUpdaterInstantiationSemaphore)
			{
				if (locationStatusUpdater == null)
				{
					this.locationStatusUpdater = new GPSDataUpdater(
							this.locationStatus);
				}
			}
		}

		return this.locationStatusUpdater;
	}

	/**
	 * @see ecologylab.oodss.logging.MixedInitiativeOp#performAction(boolean)
	 */
	@Override public void performAction(boolean invert)
	{
	}

	@Override
	public void apListUpdate(String newData)
	{
		this.netStatus.apListUpdate(newData);
	}

	@Override
	public void macAddressUpdate(String newData)
	{
		this.netStatus.macAddressUpdate(newData);
	}

	@Override
	public void processIncomingNMEAString(String gpsDataString)
	{
		this.locationStatusUpdater().processIncomingNMEAString(gpsDataString);

		this.utcTime = this.locationStatus.getUtcTime();
	}
	
	public static void main(String[] args) throws NativeException, NoSuchPortException, IOException, PortInUseException, UnsupportedCommOperationException, TooManyListenersException
	{
		WiFiGPSStatusOp currentOp = new WiFiGPSStatusOp();
		
		RunnableWiFiAdapter wiFi = new RunnableWiFiAdapter(500);
		
		NMEAReader gps = new NMEAReader("COM6", 9600);
		
		wiFi.addListener(currentOp);
		gps.addGPSDataListener(currentOp);
		
		gps.connect();
		wiFi.connect();
		
		Logging l = new Logging("c:\\wGLog.xml", true, 10,
				Logging.LOG_TO_MEMORY_MAPPED_FILE, null, 0, null);
		
		l.start();
		
		for (int i = 0; i < 100; i++)
		{
			l.logAction(currentOp);
			
			Generic.sleep(100);
		}
		
		l.stop();
	}

	/**
	 * @return the locationStatus
	 */
	public GeoCoordinate getLocationStatus()
	{
		return locationStatus;
	}

	/**
	 * @return the netStatus
	 */
	public WiFiAdapterStatus getNetStatus()
	{
		return netStatus;
	}

	/**
	 * @return the utcTime
	 */
	public Calendar getUtcTime()
	{
		return utcTime;
	}
}
