/**
 * 
 */
package ecologylab.sensor.location.gps;

import ecologylab.sensor.location.NMEAReader;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TooManyListenersException;

import javax.swing.Timer;

/**
 * A simulated GPS that uses a text file containing NMEA sentences to drive a GPS-based application.
 * 
 * Usage: Instantiate with a text file containing NMEA sentences. Like normal NMEA setences, each
 * line should end with \r\n. Register one or more GPSDataListeners to listen to it.
 * 
 * Call either start w/ the number of milliseconds between sends.
 * 
 * -OR-
 * 
 * Call sendSentence (to handle it interactively).
 * 
 * @author Zach
 * 
 */
public class SimGPS extends NMEAReader implements ActionListener
{
	public enum PlayMode
	{
		LOOP_FORWARD, FORWARD_BACKWARD, LOOP_BACKWARD
	}

	ArrayList<String>	nmeaStrings			= new ArrayList<String>();

	Timer							t;

	boolean						goingForward		= true;

	PlayMode					mode;

	int								currentSentence	= 0;

	/**
	 * 
	 * @param nmeaSentenceTextFile
	 * @param mode
	 * @throws IOException
	 */
	public SimGPS(File nmeaSentenceTextFile, PlayMode mode) throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(nmeaSentenceTextFile));

		String input;

		while ((input = in.readLine()) != null)
		{
			nmeaStrings.add(input);
		}

		this.mode = mode;

		if (mode == PlayMode.LOOP_BACKWARD)
		{
			goingForward = false;
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		sendSentence();
	}

	public void start(int millisecondsBetweenSends)
	{
		if (t == null)
		{
			t = new Timer(millisecondsBetweenSends, this);

			synchronized (t)
			{
				t.start();
			}
		}
	}

	public void stop()
	{
		if (t != null)
		{
			synchronized (t)
			{
				t.stop();
				t = null;
			}
		}
	}

	/**
	 * Sends the next sentence in the file.
	 */
	public void sendSentence()
	{
		/*
		this.incomingDataBuffer.append(this.nmeaStrings.get(currentSentence) + "\r\n");
		this.handleIncomingChars();
		 */
		
		this.fireGPSDataString(this.nmeaStrings.get(currentSentence).substring(1));
		
		if (goingForward)
		{
			this.currentSentence++;

			if (currentSentence == nmeaStrings.size())
			{
				switch (this.mode)
				{
				case LOOP_FORWARD:
					currentSentence = 0;
					break;
				case FORWARD_BACKWARD:
					currentSentence--;
					goingForward = false;
					break;
				}
			}
		}
		else
		{
			this.currentSentence--;

			if (currentSentence < 0)
			{
				switch (this.mode)
				{
				case LOOP_BACKWARD:
					currentSentence = nmeaStrings.size() - 1;
					break;
				case FORWARD_BACKWARD:
					currentSentence++;
					goingForward = true;
					break;
				}
			}
		}
	}

	/**
	 * Calls start with a 250ms delay.
	 */
	@Override
	public boolean connect() throws PortInUseException, UnsupportedCommOperationException,
			IOException, TooManyListenersException
	{
		this.start(50);

		return this.connected();
	}

	@Override
	public boolean connected()
	{
		return true;
	}

	/**
	 * Calls stop();
	 */
	@Override
	public void disconnect()
	{
		this.stop();
	}
}
