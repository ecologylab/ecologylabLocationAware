/**
 * 
 */
package ecologylab.sensor.gps.data;

import ecologylab.sensor.gps.listener.GPSDataListener;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 *
 */
public class CoordStore implements GPSDataListener
{
    public CoordStore()
    {
        
    }

    /* (non-Javadoc)
     * @see ecologylab.sensor.gps.data.GPSDataListener#readGPSData(java.lang.String)
     */
    public void readGPSData(String gpsDataString)
    {
        System.out.println("got data");
        System.out.println(gpsDataString);
    }

}
