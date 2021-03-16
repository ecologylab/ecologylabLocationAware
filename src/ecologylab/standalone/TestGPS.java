package ecologylab.standalone;

import ecologylab.sensor.location.NMEAReader;
import ecologylab.sensor.location.gps.listener.GPSDataLogger;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.File;
import java.io.IOException;
import java.util.TooManyListenersException;

public class TestGPS
{
    public static void main(String[] args) throws PortInUseException, UnsupportedCommOperationException, IOException, TooManyListenersException, NoSuchPortException
    {
        NMEAReader g = new NMEAReader("COM5", 9600);
        
        File fileToWriteTo = new File("gpsLog.log");
        
        fileToWriteTo.createNewFile();
        
        g.addGPSDataListener(new GPSDataLogger(fileToWriteTo));
        
        g.connect();
        
        byte[] data = {(byte) 0xB5, 0x62, 0x06, 0x04, 0x04, 0x00,
              0x00, 0x00, 0x00, 0x00, 0x0E, 0x64}; 
        
       // g.getPort().getOutputStream().write(data);
    }
}
