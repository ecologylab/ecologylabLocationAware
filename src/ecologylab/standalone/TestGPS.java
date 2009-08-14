package ecologylab.standalone;

import ecologylab.sensor.location.NMEAReader;
import ecologylab.sensor.location.gps.listener.GPSDataLogger;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TooManyListenersException;

public class TestGPS
{
    public static void main(String[] args) throws PortInUseException, UnsupportedCommOperationException, IOException, TooManyListenersException, NoSuchPortException
    {
        NMEAReader g = new NMEAReader("COM40", 115200);
        
        File fileToWriteTo = new File("gpsLog.log");
        
        fileToWriteTo.createNewFile();
        
        g.addGPSDataListener(new GPSDataLogger(fileToWriteTo));
        
        g.connect();
    }
}
