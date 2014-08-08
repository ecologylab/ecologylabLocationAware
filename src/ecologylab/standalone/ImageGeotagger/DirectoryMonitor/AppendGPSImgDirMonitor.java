package ecologylab.standalone.ImageGeotagger.DirectoryMonitor;

import java.io.File;
import java.util.ArrayList;

import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.standalone.GeoClient;
import ecologylab.standalone.ImageGeotagger.ImageProcessor;

public class AppendGPSImgDirMonitor extends ImgDirMonitor
{
	protected final GeoClient							client;

	private final ArrayList<CompassDatum>	compassData	= new ArrayList<CompassDatum>();

	private final ArrayList<GPSDatum>			gpsData			= new ArrayList<GPSDatum>();

	private final long										offsetInMillis;

	public AppendGPSImgDirMonitor(File directory, GeoClient client, long clockOffset)
			throws Exception
	{
		super(directory);

		this.offsetInMillis = clockOffset;
		this.client = client;
	}

	public void addGPSDatum(GPSDatum datum)
	{
		this.gpsData.add(datum);
	}

	public void addCompassDatum(CompassDatum datum)
	{
		this.compassData.add(datum);
	}

	public ArrayList<GPSDatum> getGPSData()
	{
		return this.gpsData;
	}

	public ArrayList<CompassDatum> getCompassData()
	{
		return this.compassData;
	}

	@Override
	protected synchronized void processFile(File file)
	{
		ImageProcessor processor = new ImageProcessor(file, client, offsetInMillis, this);

		processor.processImage();
	}
}
