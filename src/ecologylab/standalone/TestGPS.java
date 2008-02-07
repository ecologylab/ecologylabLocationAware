package ecologylab.standalone;

import ecologylab.sensor.gps.GPS;
import ecologylab.sensor.gps.listener.GPSDataLogger;
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
        GPS g = new GPS("COM4", 9600);
        
        File fileToWriteTo = new File("C:\\gpsLog"+new Date().toString());
        
        fileToWriteTo.createNewFile();
        
        g.addGPSDataListener(new GPSDataLogger(fileToWriteTo));
        
        g.connect();
    }
}
