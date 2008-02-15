/**
 * 
 */
package ecologylab.sensor.location.gps.gui;

import java.io.IOException;
import java.util.TooManyListenersException;

import ecologylab.sensor.location.gps.GPS;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 *
 */
public interface GPSController
{

	public boolean connectGPS(GPS newGPS) throws PortInUseException, UnsupportedCommOperationException, IOException,
			TooManyListenersException;

	public void disconnectGPS();

	/**
	 * @return the gps
	 */
	public GPS getGps();

}