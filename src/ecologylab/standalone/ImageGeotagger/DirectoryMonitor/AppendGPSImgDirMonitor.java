package ecologylab.standalone.ImageGeotagger.DirectoryMonitor;

import java.io.File;
import java.util.ArrayList;

import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.standalone.GeoClient;
import ecologylab.standalone.ImageGeotagger.ImageProcessor;

public class AppendGPSImgDirMonitor extends ImgDirMonitor
{
	protected final GeoClient	client;

	private final ArrayList<CompassDatum> compassData = new ArrayList<CompassDatum>();
	private final ArrayList<GPSDatum> gpsData = new ArrayList<GPSDatum>();
	
	public AppendGPSImgDirMonitor(File directory, GeoClient client) throws Exception
	{
		super(directory);
		
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
	protected void processFile(File file)
	{
		// XXX THIS IS WRONG; JUST ADDED TO MAKE IT COMPILE FOR TESTING
		ImageProcessor processor = new ImageProcessor(file, client, 100, this);
		
		processor.processImage();
	}
}
