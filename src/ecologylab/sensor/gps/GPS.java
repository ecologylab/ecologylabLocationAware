/**
 * 
 */
package ecologylab.sensor.gps;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.LinkedList;
import java.util.TooManyListenersException;

import ecologylab.generic.Debug;
import ecologylab.sensor.gps.listener.GPSDataListener;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * Buffers data from a GPS device for use in a Java application. Can produce
 * instances of GPSData, which encapsulate the sensor data at some time. Uses
 * GPSDeviceProfiles to handle multiple different types of GPS equipment, based
 * on the type of data it produces.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class GPS extends Debug implements SerialPortEventListener
{
	private CommPortIdentifier				portId;

	private SerialPort						port						= null;

	private int									baud;

	private InputStream						portIn					= null;

	private LinkedList<GPSDataListener>	listeners				= new LinkedList<GPSDataListener>();

	private static final CharsetDecoder	ASCII_DECODER			= Charset.forName(
																						"US-ASCII")
																						.newDecoder();

	protected StringBuilder					incomingDataBuffer	= new StringBuilder();

	private ByteBuffer						incomingBytes			= ByteBuffer
																						.allocate(10000);

	/**
	 * Instantiate a GPS device on a given port and baud rate.
	 * 
	 * @param devProfile
	 * @param portName
	 * @param baud
	 * @throws NoSuchPortException
	 *            the port does not exist on this system.
	 * @throws IOException
	 *            the specified port is a parallel port.
	 */
	public GPS(String portName, int baud) throws NoSuchPortException,
			IOException
	{
		this(CommPortIdentifier.getPortIdentifier(portName), baud);
	}

	public GPS(CommPortIdentifier portId, int baud) throws NoSuchPortException,
			IOException
	{
		this.baud = baud;

		this.portId = portId;

		if (portId.getPortType() == CommPortIdentifier.PORT_PARALLEL)
		{
			throw new IOException("GPS is not available for parallel ports.");
		}

		debug("port acquired: " + portId.toString());
	}

	/**
	 * No-argument, do-nothing constructor for simulator subclass that will not
	 * use a port.
	 */
	protected GPS()
	{

	}

	/**
	 * Connects to the GPS device based on the portName, baud, and device profile
	 * and activates the connection.
	 * 
	 * @return true if connection was successful, false otherwise.
	 * @throws PortInUseException
	 * @throws IOException
	 * @throws UnsupportedCommOperationException
	 * @throws TooManyListenersException
	 */
	public boolean connect() throws PortInUseException,
			UnsupportedCommOperationException, IOException,
			TooManyListenersException
	{
		debug("Connecting to GPS...");

		if (port == null)
		{
			port = (SerialPort) portId.open("GPS Port", 5000);
		}

		port.setSerialPortParams(baud, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		// port.setDTR(true);
		port.setRTS(true);

		port.addEventListener(this);
		port.notifyOnDataAvailable(true);

		portIn = port.getInputStream();

		if (connected())
		{
			debug("...successful.");
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
				portIn = null;
			}
		}
	}

	public void serialEvent(SerialPortEvent event)
	{
		switch (event.getEventType())
		{
		case (SerialPortEvent.DATA_AVAILABLE):
			// when data is available, we read it, parse it as ASCII
			// strings, and pass it to a data listener

			try
			{
				incomingBytes.clear();

				int bytesRead = portIn.read(this.incomingBytes.array());

				if (bytesRead > 0)
				{
					// have to set the limit on the bytebuffer, because we just
					// changed the
					// backing array
					incomingBytes.limit(bytesRead);

					incomingDataBuffer.append(ASCII_DECODER.decode(incomingBytes));

					handleIncomingChars();
				}
			}
			catch (IOException ioe)
			{
				System.out.println("I/O Exception!");
				ioe.printStackTrace();
			}

			break;
		}
	}

	/**
	 * @param bytesRead
	 * @throws CharacterCodingException
	 */
	protected void handleIncomingChars()
	{
		int endOfMessage = incomingDataBuffer.indexOf("\r\n");
		int startOfMessage = incomingDataBuffer.indexOf("$");

		if (startOfMessage > -1 && endOfMessage > -1)
		{
			this.fireGPSDataString(incomingDataBuffer.substring(
					startOfMessage + 1, endOfMessage));

			incomingDataBuffer.delete(startOfMessage, endOfMessage + 2);
		}
	}

	public void addGPSDataListener(GPSDataListener l)
	{
		this.listeners.add(l);
	}

	public void removeGPSDataListener(GPSDataListener l)
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
	 * Gets the baud rate of the port. If the port is not specified (not
	 * connected), then returns -1.
	 * 
	 * @return an integer representing the baud rate of the current port or -1 if
	 *         not connected.
	 */
	public int getBaudRate()
	{
		if (this.port != null)
		{
			return this.port.getBaudRate();
		}

		return -1;
	}

	private void fireGPSDataString(String gpsDataString)
	{
		for (GPSDataListener l : listeners)
		{
			l.readGPSData(gpsDataString);
		}
	}
}
