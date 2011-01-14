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

			cData.setHeading(Float.parseFloat(gpsDataString.substring(cIndex + 1, pIndex)));
			cData.setPitch(Float.parseFloat(gpsDataString.substring(pIndex + 1, rIndex)));
			cData.setRoll(Float.parseFloat(gpsDataString.substring(rIndex + 1, totAIndex)));
			cData.setTotAcc(Float.parseFloat(gpsDataString.substring(totAIndex + 1, aXIndex)));
			cData.setAccX(Float.parseFloat(gpsDataString.substring(aXIndex + 2, aYIndex)));
			cData.setAccY(Float.parseFloat(gpsDataString.substring(aYIndex + 2, aZIndex)));
			cData.setAccZ(Float.parseFloat(gpsDataString.substring(aZIndex + 2, asteriskIndex)));

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
