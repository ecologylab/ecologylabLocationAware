/**
 * 
 */
package ecologylab.standalone;

import java.util.LinkedList;
import java.util.List;

import ecologylab.sensor.gps.listener.GPSDataListener;
import ecologylab.sensor.gps.listener.GPSDataPrinter;
import ecologylab.sensor.gps.listener.GPSDataUpdater;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class NMEASentenceSimulator
{
	String[]	nmeaSentences	=
									{ 
			"$GPRMC,130303.0,A,4717.115,N,00833.912,E,000.03,043.4,200601,01.3,W*7D\\r\\n",
			"$GPZDA,130304.2,20,06,2001,,*56\\r\\n",
			"$GPGGA,130304.0,4717.115,N,00833.912,E,1,08,0.94,00499,M,047,M,,*59\\r\\n",
			"$GPGLL,4717.115,N,00833.912,E,130304.0,A*33\\r\\n", "$GPVTG,205.5,T,206.8,M,000.04,N,000.08,K*4C\\r\\n",
			"$GPGSA,A,3,13,20,11,29,01,25,07,04,,,,,1.63,0.94,1.33*04\\r\\n",
			"$GPGSV,2,1,8,13,15,208,36,20,80,358,39,11,52,139,43,29,13,044,36*42\\r\\n",
			"$GPGSV,2,2,8,01,52,187,43,25,25,074,39,07,37,286,40,04,09,306,33*44\\r\\n",
			"$GPRMC,130304.0,A,4717.115,N,00833.912,E,000.04,205.5,200601,01.3,W*7C\\r\\n",
			"$GPZDA,130305.2,20,06,2001,,*57\\r\\n",
			"$GPGGA,130305.0,4717.115,N,00833.912,E,1,08,0.94,00499,M,047,M,,*58\\r\\n",
			"$GPGLL,4717.115,N,00833.912,E,130305.0,A*32\\r\\n", "$GPVTG,014.2,T,015.4,M,000.03,N,000.05,K*4F\\r\\n",
			"$GPGSA,A,3,13,20,11,29,01,25,07,04,,,,,1.63,0.94,1.33*04\\r\\n",
			"$GPGSV,2,1,8,13,15,208,36,20,80,358,39,11,52,139,43,29,13,044,36*42\\r\\n",
			"$GPGSV,2,2,8,01,52,187,43,25,25,074,39,07,37,286,40,04,09,306,33*44\\r\\n" 
			};
	
	List<GPSDataListener> listeners = new LinkedList<GPSDataListener>();
	
	public NMEASentenceSimulator()
	{
		
	}
	
	public void addListener(GPSDataListener l)
	{
		listeners.add(l);
	}
	
	private void fireGPSEvent(String s)
	{
		for (GPSDataListener l : listeners)
		{
			l.readGPSData(s);
		}
	}
	
	public void go()
	{
		for (String s : this.nmeaSentences)
		{
			this.fireGPSEvent(s.substring(1, s.length()-4));
		}
	}
	
	public static void main(String[] args)
	{
		NMEASentenceSimulator s = new NMEASentenceSimulator();
		
		s.addListener(new GPSDataPrinter());
		s.addListener(new GPSDataUpdater());
		
		s.go();
	}
}
