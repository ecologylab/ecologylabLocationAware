/**
 * 
 */
package ecologylab.sensor.location.gps.listener;

import ecologylab.sensor.location.NMEAStringListener;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 *
 */
public class GPSDataPrinter implements NMEAStringListener
{
    public GPSDataPrinter()
    {
        
    }

    /* (non-Javadoc)
     * @see ecologylab.sensor.gps.data.GPSDataListener#readGPSData(java.lang.String)
     */
    public void processIncomingNMEAString(String gpsDataString)
    {
        System.out.println(gpsDataString);
    }

}
