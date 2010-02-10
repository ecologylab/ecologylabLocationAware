/**
 * 
 */
package ecologylab.sensor.location.gps.gui;

import ecologylab.sensor.location.NMEAReader;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.TooManyListenersException;

/**
 * @author Zachary O. Toups (zach@ecologylab.net)
 *
 */
public interface GPSController
{

	public boolean connectGPS(CommPortIdentifier portId, int baud) throws PortInUseException, UnsupportedCommOperationException, IOException,
			TooManyListenersException, NoSuchPortException;

	public void disconnectGPS();

	/**
	 * @return the gps
	 */
	public NMEAReader getGps();

}