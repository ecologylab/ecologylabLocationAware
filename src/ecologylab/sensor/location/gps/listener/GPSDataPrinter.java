/**
 * 
 */
package ecologylab.sensor.location.gps.listener;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 *
 */
public class GPSDataPrinter implements GPSDataListener
{
    public GPSDataPrinter()
    {
        
    }

    /* (non-Javadoc)
     * @see ecologylab.sensor.gps.data.GPSDataListener#readGPSData(java.lang.String)
     */
    public void readGPSData(String gpsDataString)
    {
        System.out.println(gpsDataString);
    }

}
