package ecologylab.sensor.location.compass;

import ecologylab.sensor.location.NMEAStringListener;

import java.util.ArrayList;

public class CompassDataUpdater implements NMEAStringListener
{
	private CompassDatum										cData;

	// private char[] tempDataStore;

	private ArrayList<CompassDataListener>	listeners	= new ArrayList<CompassDataListener>();

	// public CompassDataUpdater(CompassDatum datum){
	//
	// this.cData = datum;
	// }

	public CompassDataUpdater(CompassDatum cDatum)
	{
		cData = cDatum;
	}

	/**
	 * Instantiates a CompassDatum object and initializes it to 0, 0, 0, 0
	 */
	public CompassDataUpdater()
	{
		cData = new CompassDatum(0, 0, 0, 0);
	}

	public void processIncomingNMEAString(String gpsDataString)
	{
		if (checkCheckSum(gpsDataString) && gpsDataString.charAt(0) == 'C')
		{
			int cIndex = 0;
			
			int pIndex = gpsDataString.indexOf('P');
			int rIndex = gpsDataString.indexOf('R');
			// int tIndex = gpsDataString.indexOf('T');

			int totAIndex = gpsDataString.indexOf('A');
			
			int aXIndex = gpsDataString.indexOf("Ax");
			int aYIndex = gpsDataString.indexOf("Ay");
			int aZIndex = gpsDataString.indexOf("Az");

			int asteriskIndex = gpsDataString.indexOf('*');

			int startNext = cIndex+1;
			if (pIndex != -1)
			{
				cData.setHeading(Float.parseFloat(gpsDataString.substring(startNext, pIndex)));
				startNext = pIndex + 1;
			}

			if (rIndex != -1)
			{
				cData.setPitch(Float.parseFloat(gpsDataString.substring(startNext, rIndex)));
				startNext = rIndex + 1;
			}
			
			if (totAIndex != -1)
			{
				cData.setRoll(Float.parseFloat(gpsDataString.substring(startNext, totAIndex)));
				startNext = totAIndex + 1;
			}
			
			if (aXIndex != -1)
			{
				cData.setTotAcc(Float.parseFloat(gpsDataString.substring(startNext, aXIndex)));
				startNext = aXIndex + 2;
			}
			
			if (aYIndex != -1)
			{
				cData.setAccX(Float.parseFloat(gpsDataString.substring(startNext, aYIndex)));
				startNext = aYIndex + 2;
			}
			
			if (aZIndex != -1)
			{
				cData.setAccY(Float.parseFloat(gpsDataString.substring(startNext, aZIndex)));
				startNext = aZIndex + 2;
			}
			
			if (asteriskIndex != -1)
			{
				cData.setAccZ(Float.parseFloat(gpsDataString.substring(startNext, asteriskIndex)));
			}
			
			// cData.setTemp(Float.parseFloat(gpsDataString.substring(tIndex + 1, asteriskIndex)));

			for (CompassDataListener listener : listeners)
			{
				listener.compassDataUpdated(cData);
			}
		}
	}

	public void addCompassDataListener(CompassDataListener listener)
	{
		listeners.add(listener);
	}

	private boolean checkCheckSum(String gpsData)
	{
		// char[] tempData = tempDataStore();

		if (gpsData.length() < 3)
			return false;

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
	// private synchronized char[] tempDataStore()
	// {
	// if (tempDataStore == null)
	// {
	// tempDataStore = new char[80];
	// }
	//
	// return tempDataStore;
	// }

}
