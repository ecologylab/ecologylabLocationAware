package ecologylab.standalone;

import java.io.IOException;
import java.util.TooManyListenersException;

import ecologylab.sensor.gps.GPS;
import ecologylab.sensor.gps.data.GPSDataPrinter;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

public class TestUBloxGPS
{
    public static void main(String[] args) throws PortInUseException, UnsupportedCommOperationException, IOException, TooManyListenersException, NoSuchPortException
    {
        GPS g = new GPS(null, "COM1", 115200);
        
        g.addGPSDataListener(new GPSDataPrinter());
        
        g.connect();
    }
}
