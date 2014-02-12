package ecologylab.sensor.location.compass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TooManyListenersException;

import ecologylab.generic.Debug;
import ecologylab.sensor.location.NMEAReader;
import ecologylab.sensor.location.NMEAStringListener;
import ecologylab.sensor.location.gps.listener.GPSDataUpdater;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

public class CompassDataUpdater implements NMEAStringListener
{
	private final CompassDatum										cData;

	// private char[] tempDataStore;

	private final ArrayList<CompassDataListener>	listeners	= new ArrayList<CompassDataListener>();

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

	private static int nextNonNumber(String src, int start)
	{
		for (int i = start; i < src.length(); i++)
			if (!Character.isDigit(src.charAt(i)) && src.charAt(i) != '.' && src.charAt(i) != '-')
				return i;

		return -1;
	}

	/**
	 * This object's singleton CompassDatum can receive updates from a GPS in order to provide time
	 * data. Executing this method with a properly configured GPSDataUpdater will connect up the
	 * singleton as a listener.
	 * 
	 * @param gpsDataUpdater
	 */
	public void connectGPS(GPSDataUpdater gpsDataUpdater)
	{
		gpsDataUpdater.addDataUpdatedListener(this.cData);
	}

	@Override
	public void processIncomingNMEAString(String gpsDataString)
	{
		if (checkCheckSum(gpsDataString) && gpsDataString.charAt(0) == 'C')
		{
			int end;

			for (int i = 0; i < gpsDataString.length(); i++)
			{
				char c = gpsDataString.charAt(i);

				switch (c)
				{
					case 'C':
						end = nextNonNumber(gpsDataString, i + 1);
						if (end > -1)
						{
							cData.setHeading(Float.parseFloat(gpsDataString.substring(i + 1, end)));
							i = end - 1;
						} // should throw an exception, I guess the hardware is broken on the else
						break;
					case 'P':
						end = nextNonNumber(gpsDataString, i + 1);
						if (end > -1)
						{
							try
							{
								cData.setPitch(Float.parseFloat(gpsDataString.substring(i + 1, end)));
							}
							catch (NumberFormatException e)
							{
								e.printStackTrace();
								Debug.println(gpsDataString.substring(i + 1, end));
							}
							i = end - 1;
						} // should throw an exception, I guess the hardware is broken on the else
						break;
					case 'R':
						end = nextNonNumber(gpsDataString, i + 1);
						if (end > -1)
						{
							cData.setRoll(Float.parseFloat(gpsDataString.substring(i + 1, end)));
							i = end - 1;
						} // should throw an exception, I guess the hardware is broken on the else
						break;
					case 'T': // ignore
					case 'D': // ignore
						end = nextNonNumber(gpsDataString, i + 1);
						if (end > -1)
						{
							i = end - 1;
						} // should throw an exception, I guess the hardware is broken on the else
						break;
					case 'M':
						switch (gpsDataString.charAt(i + 1))
						{
							case 'x': // ignore
							case 'y': // ignore
							case 'z': // ignore
								end = nextNonNumber(gpsDataString, i + 2);

								if (end > -1)
								{
									i = end - 1;
								} // should throw an exception, I guess the hardware is broken on the else
								break;
							default: // ignore
								end = nextNonNumber(gpsDataString, i + 1);

								if (end > -1)
								{
									i = end - 1;
								} // should throw an exception, I guess the hardware is broken on the else
						}
						break;
					case 'A':
						switch (gpsDataString.charAt(i + 1))
						{
							case 'x':
								end = nextNonNumber(gpsDataString, i + 2);

								if (end > -1)
								{
									cData.setAccX(Float.parseFloat(gpsDataString.substring(i + 2, end)));
									i = end - 1;
								} // should throw an exception, I guess the hardware is broken on the else
								break;
							case 'y':
								end = nextNonNumber(gpsDataString, i + 2);

								if (end > -1)
								{
									cData.setAccX(Float.parseFloat(gpsDataString.substring(i + 2, end)));
									i = end - 1;
								} // should throw an exception, I guess the hardware is broken on the else
								break;
							case 'z':
								end = nextNonNumber(gpsDataString, i + 2);

								if (end > -1)
								{
									cData.setAccX(Float.parseFloat(gpsDataString.substring(i + 2, end)));
									i = end - 1;
								} // should throw an exception, I guess the hardware is broken on the else
								break;
							default:
								end = nextNonNumber(gpsDataString, i + 1);

								if (end > -1)
								{
									cData.setTotAcc(Float.parseFloat(gpsDataString.substring(i + 1, end)));
									i = end - 1;
								} // should throw an exception, I guess the hardware is broken on the else
						}
						break;
					case 'G':
						switch (gpsDataString.charAt(i + 1))
						{
							case 'x': // ignore
							case 'y': // ignore
								end = nextNonNumber(gpsDataString, i + 2);

								if (end > -1)
								{
									i = end - 1;
								} // should throw an exception, I guess the hardware is broken on the else
								break;
						}
						break;
					case '*':
						for (CompassDataListener listener : listeners)
						{
							listener.compassDataUpdated(cData);
						}

						return;
				}
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
	 * @throws IOException
	 * @throws NoSuchPortException
	 * @throws TooManyListenersException
	 * @throws UnsupportedCommOperationException
	 * @throws PortInUseException
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

	public static void main(String[] args) throws NoSuchPortException, IOException,
			PortInUseException, UnsupportedCommOperationException, TooManyListenersException
	{
		NMEAReader compass = new NMEAReader("/dev/tty.SLAB_USBtoUART", 19200);
		CompassDatum cd = new CompassDatum();

		CompassDataUpdater comUpdater = new CompassDataUpdater(cd);
		compass.addGPSDataListener(comUpdater);
		compass.connect();

		cd.addCompassDataListener(new CompassDataListener()
		{

			@Override
			public void compassDataUpdated(CompassDatum data)
			{
				System.out.println("facing: " + data.getHeading());
				System.out.println("roll:   " + data.getRoll());
				System.out.println("pitch:  " + data.getPitch());
			}
		});

		for (int i = 0; i < 10; i++)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
