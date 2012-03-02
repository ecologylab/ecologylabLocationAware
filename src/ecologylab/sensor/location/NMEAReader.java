/**
 * 
 */
package ecologylab.sensor.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.TooManyListenersException;

import ecologylab.generic.Debug;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

/**
 * Buffers data from a GPS device for use in a Java application. Can produce instances of GPSData,
 * which encapsulate the sensor data at some time. Uses GPSDeviceProfiles to handle multiple
 * different types of GPS equipment, based on the type of data it produces.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public class NMEAReader extends Debug implements Runnable
{
	private CommPortIdentifier							portId;

	private SerialPort											port			= null;

	private int															baud;

	private BufferedReader									portIn		= null;

	private LinkedList<NMEAStringListener>	listeners	= new LinkedList<NMEAStringListener>();

	private Thread													t;

	/**
	 * Instantiate a GPS device on a given port and baud rate.
	 * 
	 * @param devProfile
	 * @param portName
	 * @param baud
	 * @throws NoSuchPortException
	 *           the port does not exist on this system.
	 * @throws IOException
	 *           the specified port is a parallel port.
	 */
	public NMEAReader(String portName, int baud) throws NoSuchPortException, IOException
	{
		this(CommPortIdentifier.getPortIdentifier(portName), baud);
	}

	public NMEAReader(CommPortIdentifier portId, int baud) throws IOException
	{
		setup(portId, baud);
	}

	public void setup(String portName, int baud) throws IOException, NoSuchPortException
	{
		this.setup(CommPortIdentifier.getPortIdentifier(portName), baud);
	}

	/**
	 * @param portId
	 * @param baud
	 * @throws IOException
	 */
	public void setup(CommPortIdentifier portId, int baud) throws IOException
	{
		this.baud = baud;

		this.portId = portId;

		if (portId.getPortType() == CommPortIdentifier.PORT_PARALLEL)
		{
			throw new IOException("GPS is not available for parallel ports.");
		}

		debug("port acquired: " + portId.toString()+":"+portId.getName());
	}

	/**
	 * No-argument, do-nothing constructor for simulator subclass that will not use a port.
	 * 
	 * Can also be used to have a GPS instance that is not yet configured, but will be configured
	 * later. This is useful if listeners need to be the same.
	 */
	public NMEAReader()
	{

	}

	/**
	 * Connects to the GPS device based on the portName, baud, and device profile and activates the
	 * connection.
	 * 
	 * @return true if connection was successful, false otherwise.
	 * @throws PortInUseException
	 * @throws IOException
	 * @throws UnsupportedCommOperationException
	 * @throws TooManyListenersException
	 */
	public boolean connect() throws PortInUseException, UnsupportedCommOperationException,
			IOException, TooManyListenersException
	{
		debug("Connecting to GPS...");

		if (port == null)
		{
			port = (SerialPort) portId.open("NMEA Reader Port", 10000);
		}

		port.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE);
		port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		// port.setDTR(true);
		port.setRTS(true);

		if (port.isReceiveFramingEnabled())
		{
			debug("Using framing!");
			port.enableReceiveFraming('\n');
		}
		else
		{
			debug("Not using framing!");
			port.enableReceiveTimeout(5000);
			port.enableReceiveThreshold(1);
		}

		// port.addEventListener(this);
		// port.notifyOnDataAvailable(true);

		portIn = new BufferedReader(new InputStreamReader(port.getInputStream(), "US-ASCII"));

		if (connected())
		{
			debug("...successful.");
			t = new Thread(this, "NMEA Reader Thread on " + portId.toString() + " @ "
					+ port.getBaudRate() + "bps");
			t.start();
		}
		else
		{
			debug("...FAILED!");
		}
		return connected();
	}

	public boolean connected()
	{
		return port != null;
	}

	/**
	 * Deactivates the current GPS device and closes out the port.
	 */
	public void disconnect()
	{
		if (port != null)
		{
			synchronized (port)
			{
				port.setDTR(false);
				port.close();

				port = null;
				try
				{
					portIn.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				portIn = null;
			}
		}
	}

	public void run()
	{
		String nmeaLine = null;

		try
		{
			portIn.readLine();
			portIn.skip(1);
			nmeaLine = portIn.readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		while (nmeaLine != null)
		{
			this.fireGPSDataString(nmeaLine);

			try
			{
				portIn.skip(1);
				nmeaLine = portIn.readLine();
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}

	public void addGPSDataListener(NMEAStringListener l)
	{
		this.listeners.add(l);
	}

	public void removeGPSDataListener(NMEAStringListener l)
	{
		this.listeners.remove(l);
	}

	public String getPortName()
	{
		if (this.port != null)
		{
			return this.port.getName();
		}

		return "";
	}

	/**
	 * Gets the baud rate of the port. If the port is not specified (not connected), then returns -1.
	 * 
	 * @return an integer representing the baud rate of the current port or -1 if not connected.
	 */
	public int getBaudRate()
	{
		if (this.port != null)
		{
			return this.port.getBaudRate();
		}

		return -1;
	}

	protected void fireGPSDataString(String gpsDataString)
	{
		// debug("firing for: "+gpsDataString);

		for (NMEAStringListener l : listeners)
		{
			l.processIncomingNMEAString(gpsDataString);
		}
	}
}
