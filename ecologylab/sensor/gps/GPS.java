/**
 * 
 */
package ecologylab.sensor.gps;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.TooManyListenersException;

import ecologylab.generic.Debug;
import ecologylab.sensor.gps.data.GPSDataListener;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * Buffers data from a GPS device for use in a Java application. Can produce instances of GPSData, which encapsulate the
 * sensor data at some time. Uses GPSDeviceProfiles to handle multiple different types of GPS equipment, based on the
 * type of data it produces.
 * 
 * @author toupsz
 * 
 */
public class GPS extends Debug implements SerialPortEventListener
{
    private GPSDeviceProfile            devProfile;

    private CommPortIdentifier          portId;

    private SerialPort                  port               = null;

    private int                         baud;

    private InputStream                 portIn             = null;

    private OutputStream                portOut            = null;

    private LinkedList<GPSDataListener> listeners          = new LinkedList<GPSDataListener>();

    private static final CharsetDecoder ASCII_DECODER      = Charset.forName("ASCII").newDecoder();

    private StringBuilder               incomingDataBuffer = new StringBuilder();

    /**
     * Instantiate a GPS device on a given port and baud rate.
     * 
     * @param devProfile
     * @param portName
     * @param baud
     * @throws NoSuchPortException
     *             the port does not exist on this system.
     * @throws IOException
     *             the specified port is a parallel port.
     */
    public GPS(GPSDeviceProfile devProfile, String portName, int baud) throws NoSuchPortException, IOException
    {
        this.devProfile = devProfile;
        this.baud = baud;
        
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();
        
        System.out.println("serial port: "+CommPortIdentifier.PORT_SERIAL);
        System.out.println("i2c port: "+CommPortIdentifier.PORT_I2C);
        System.out.println("parallel port: "+CommPortIdentifier.PORT_PARALLEL);
        System.out.println("raw port: "+CommPortIdentifier.PORT_RAW);
        System.out.println("rs485 port: "+CommPortIdentifier.PORT_RS485);
        
        while (ports.hasMoreElements())
        {
            CommPortIdentifier p = (CommPortIdentifier)ports.nextElement();
            System.out.println(p.getName()+": "+p.getPortType());
        }

        portId = CommPortIdentifier.getPortIdentifier(portName);

        if (portId.getPortType() == CommPortIdentifier.PORT_PARALLEL)
        {
            throw new IOException("GPS is not available for parallel ports.");
        }

        debug("port acquired: " + portId.toString());
    }

    /**
     * Connects to the GPS device based on the portName, baud, and device profile and activates the connection.
     * 
     * @return true if connection was successful, false otherwise.
     * @throws PortInUseException
     * @throws IOException
     * @throws UnsupportedCommOperationException
     * @throws TooManyListenersException
     */
    public boolean connect() throws PortInUseException, UnsupportedCommOperationException, IOException,
            TooManyListenersException
    {
        debug("Connecting to GPS...");

        if (port == null)
        {
            port = (SerialPort) portId.open("GPS Port", 5000);
        }

        port.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        port.setDTR(true);

        port.addEventListener(this);
        port.notifyOnDataAvailable(true);

        portIn = port.getInputStream();
        portOut = port.getOutputStream();

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
            port.setDTR(false);
            port.close();

            port = null;
            portIn = null;
            portOut = null;
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
                int bytesRead = portIn.available();

                if (bytesRead > 0)
                {
                    byte[] bytes = new byte[bytesRead];

                    portIn.read(bytes);

                    incomingDataBuffer.append(ASCII_DECODER.decode(ByteBuffer.wrap(bytes)));
                    
                    int endOfMessage = incomingDataBuffer.indexOf("\r\n");
                    int startOfMessage = incomingDataBuffer.indexOf("$");

                    if (startOfMessage > -1 && endOfMessage > -1)
                    {
                        this.fireGPSDataString(incomingDataBuffer.substring(startOfMessage, endOfMessage));

                        incomingDataBuffer.delete(startOfMessage, endOfMessage+2);
                    }
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

    public void addGPSDataListener(GPSDataListener l)
    {
        this.listeners.add(l);
    }

    public void removeGPSDataListener(GPSDataListener l)
    {
        this.listeners.remove(l);
    }

    private void fireGPSDataString(String gpsDataString)
    {
        for (GPSDataListener l : listeners)
        {
            l.readGPSData(gpsDataString);
        }
    }

}
