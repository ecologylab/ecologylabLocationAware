/**
 * 
 */
package ecologylab.sensor.gps.data;

/**
 * @author toupsz
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
