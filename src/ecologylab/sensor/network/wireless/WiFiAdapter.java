/**
 * 
 */
package ecologylab.sensor.network.wireless;

import stec.jenie.BOOL;
import stec.jenie.Dll;
import stec.jenie.INT32;
import stec.jenie.NativeException;
import stec.jenie.Pointer;
import stec.jenie.UCHARArray;
import ecologylab.generic.Debug;

/**
 * Abstracts the sensor for a WiFi card; allows listeners to retrieve updates.
 * 
 * @author Z O. Toups (zach@ecologylab.net)
 * @author Alan Blevins (blevinsa@gmail.com)
 */
public class WiFiAdapter extends Debug
{
	public static final String	NOT_ASSOCIATED	= "NOT_ASSOCIATED";

	private Dll						jWifiDll;

	/**
	 * @throws NativeException
	 * 
	 */
	public WiFiAdapter()
	{
	}

	public void connect() throws NativeException
	{
		jWifiDll = new Dll("lib/Jwifi");
	}

	public void disconnect()
	{
		if (jWifiDll != null)
		{
			jWifiDll.release();
			jWifiDll = null;
		}
	}
	
	public boolean connected()
	{
		return jWifiDll != null;
	}

	public String getAssociatedMac() throws NativeException
	{
		UCHARArray returnArray = new UCHARArray(18);
		// AnsiString retValue = new AnsiString();
		Pointer toReturnArray = new Pointer(returnArray);

		BOOL associated = new BOOL();

		jWifiDll.getFunction("getAssociatedMAC").call(toReturnArray, associated);

		char[] resizer = new char[17];

		if (associated.getValue())
			for (int x = 0; x < 17; x++)
				resizer[x] = (char) returnArray.getValueAt(x);

		String toReturn = new String(resizer);
		if (!associated.getValue())
			toReturn = "NOT_ASSOCIATED";

		return toReturn;
	}

	public String getAPData() throws NativeException
	{
		UCHARArray returnArray = new UCHARArray(5000);
		Pointer toReturnArray = new Pointer(returnArray);

		INT32 ptvalue = new INT32();

		jWifiDll.getFunction("getAPList").call(toReturnArray, ptvalue);

		char[] resizer = new char[ptvalue.getValue()];

		for (int x = 0; x < ptvalue.getValue(); x++)
			resizer[x] = (char) returnArray.getValueAt(x);

		String toReturn = new String(resizer);

		return toReturn;
	}
	
	public static void main(String[] args) throws NativeException
	{
		WiFiAdapter w = new WiFiAdapter();
		w.connect();
		
		System.out.println(w.getAPData());
		
		w.disconnect();
		w.connect();
		
		System.out.println("associated AP MAC: "+w.getAssociatedMac());
	}
}
