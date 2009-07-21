package ecologylab.sensor.location.compass;

import java.util.ArrayList;

import ecologylab.sensor.location.NMEAStringListener;

public class CompassDataUpdater implements NMEAStringListener
{
	private float heading 					= 0;
	
	private float pitch 						= 0;
	
	private float roll						= 0;
	
	private float temp 						= 0;

	private char[]	tempDataStore;
	
	private ArrayList<CompassDataListener> listeners = new ArrayList<CompassDataListener>();
	
	public void processIncomingNMEAString(String gpsDataString)
	{
		if(checkCheckSum(gpsDataString) && gpsDataString.charAt(0) == 'C')
		{
			int cIndex = 0;
			int pIndex = gpsDataString.indexOf('P');
			int rIndex = gpsDataString.indexOf('R');
			int tIndex = gpsDataString.indexOf('T');
			int asteriskIndex = gpsDataString.indexOf('*');
			
			heading = Float.parseFloat(gpsDataString.substring(cIndex +  1, pIndex));
			pitch = Float.parseFloat(gpsDataString.substring(pIndex +  1, rIndex));;
			roll = Float.parseFloat(gpsDataString.substring(rIndex +  1, tIndex));
			temp = Float.parseFloat(gpsDataString.substring(tIndex + 1, asteriskIndex));
			
			for(CompassDataListener listener:listeners)
			{
				listener.compassDataUpdated(heading, pitch, roll, temp);
			}
		}
	}
	
	public void addCompassDataListener(CompassDataListener listener)
	{
		listeners.add(listener);
	}
	
	private boolean checkCheckSum(String gpsData)
	{
		char[] tempData = tempDataStore();

		// check the checksum before doing any processing
		int checkSumSplit = gpsData.length() - 3;

		String messageStringToCheck = gpsData.substring(0, checkSumSplit);

		int checkSum = 0;

		for (int i = 0; i < messageStringToCheck.length(); i++)
		{
			checkSum = (checkSum ^ messageStringToCheck.charAt(i));
		}

		String computedCheckSum = Integer.toHexString((checkSum & 0xF0) >>> 4)
				+ Integer.toHexString(checkSum & 0x0F);
		
		return computedCheckSum.toUpperCase().equals(gpsData.substring(checkSumSplit + 1));
	}
	
	/**
	 * Ensure that tempDataStore is instantiated and clean, then return it. Used for lazy
	 * instantiation; no need to instantiate it if this is a serialized set of data from somewhere
	 * else (for example).
	 * 
	 * @return
	 */
	private synchronized char[] tempDataStore()
	{
		if (tempDataStore == null)
		{
			tempDataStore = new char[80];
		}

		return tempDataStore;
	}

}
