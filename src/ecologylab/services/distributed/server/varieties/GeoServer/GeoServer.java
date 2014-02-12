package ecologylab.services.distributed.server.varieties.GeoServer;

import java.io.IOException;
import java.net.BindException;
import java.util.EnumSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import ecologylab.collections.Scope;
import ecologylab.oodss.distributed.server.DoubleThreadedNIOServer;
import ecologylab.sensor.location.LocationUpdatedListener;
import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.services.messages.LocationDataRequest;
import ecologylab.services.messages.LocationDataResponse;

public class GeoServer extends DoubleThreadedNIOServer implements LocationUpdatedListener
{

	/** Maps a capacity-bounded deque of CompassDatum objects. */
	public static final String								COMPASS_DATA							= "COMPASS_DATA";

	private final BlockingDeque<CompassDatum>	compassData;

	/** Maps a capacity-bounded deque of GPSDatum objects. */
	public static final String								GPS_DATA									= "GPS_DATA";

	private final BlockingDeque<GPSDatum>			gpsData;

	private static EnumSet<GPSUpdateInterest>	interestSet;

	public static Class[]											LOCATION_MESSAGE_CLASSES	=
																																			{ LocationDataRequest.class,
																																			LocationDataResponse.class };

	/**
	 * @param portNumber
	 * @param objectRegistry
	 * @param useCompression
	 * @param historyLength
	 *          indicates the number of historical copies of the GPS and compass data to store.
	 *          Default is 1.
	 * @throws BindException
	 * @throws IOException
	 */
	public GeoServer(int portNumber, Scope objectRegistry, boolean useCompression, int historyLength)
			throws BindException, IOException
	{
		super(portNumber, LocationTranslations.get(),
				objectRegistry);

		this.gpsData = new LinkedBlockingDeque<>(historyLength);
		// this.gpsData = new GPSDatum();
		// this.compassData = new CompassDatum(0, 0, 0, 0);
		this.compassData = new LinkedBlockingDeque<>(historyLength);

		objectRegistry.put(GPS_DATA, this.gpsData);
		objectRegistry.put(COMPASS_DATA, this.compassData);
	}

	@Override
	public EnumSet<GPSUpdateInterest> getInterestSet()
	{
		if (interestSet == null)
			interestSet = EnumSet.of(GPSUpdateInterest.ALT, GPSUpdateInterest.SPEED,
					GPSUpdateInterest.LAT_LON, GPSUpdateInterest.OTHERS);

		return interestSet;
	}

	@Override
	public void gpsDatumUpdated(GPSDatum datum)
	{
		synchronized (gpsData)
		{
			if (this.gpsData.size() == 0)
				this.gpsData.offerLast(datum.clone());
			else if (!this.gpsData.peekLast().getTime().equals(datum.getTime()))
			{
				if (this.gpsData.remainingCapacity() == 0)
					this.gpsData.removeFirst();
				this.gpsData.offerLast(datum.clone());
			}
		}
	}

	@Override
	public void compassDataUpdated(CompassDatum datum)
	{
		synchronized (compassData)
		{
			if (this.compassData.size() == 0)
				this.compassData.offerLast(datum.clone());
			else if (this.compassData.peekLast().getTime() == null
					|| !this.compassData.peekLast().getTime().equals(datum.getTime()))
			{
				if (this.compassData.remainingCapacity() == 0)
					this.compassData.removeFirst();
				this.compassData.offerLast(datum.clone());
			}
		}
	}
}
