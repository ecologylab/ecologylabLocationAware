/**
 * 
 */
package ecologylab.composite.kml.sensor;

import java.util.LinkedList;

import ecologylab.sensor.network.wireless.WiFiAdapter;
import ecologylab.sensor.network.wireless.data.WiFiAdapterStatus;
import ecologylab.sensor.network.wireless.data.WiFiSource;
import ecologylab.sensor.network.wireless.listener.WiFiStringDataListener;
import ecologylab.xml.library.kml.feature.KmlFeature;

/**
 * Can modify the Description component of a KmlFeature to keep it updated with
 * an HTML-formatted string indicating the current WiFi status.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
public class WiFiDataComment implements WiFiStringDataListener
{
	protected WiFiAdapterStatus		netStatus				= new WiFiAdapterStatus();

	protected LinkedList<KmlFeature>	registeredFeatures	= new LinkedList<KmlFeature>();

	/**
	 * 
	 */
	public WiFiDataComment()
	{
	}

	public void registerKmlFeature(KmlFeature featureToRegister)
	{
		this.registeredFeatures.add(featureToRegister);
	}

	public void updateFeatures()
	{
		StringBuilder htmlString = new StringBuilder();

		// build a formatted string of wifi status data

		String currentAP = netStatus.getCurrentMacAddr();

		if (currentAP == null)
		{ // just to be safe
			currentAP = "";
		}

		if (!WiFiAdapter.NOT_ASSOCIATED.equals(currentAP))
		{ // put the currently-associated network first, and in bold
			WiFiSource stat = netStatus.getAvailableConnections().get(currentAP);

			if (stat != null)
			{
				htmlString.append("<h3>");
				htmlString.append(stat.getId() + " (" + stat.getMacAddr() + ") - "
						+ stat.getSignalStrengthPercent() + "% ("
						+ stat.getSignalStrength() + "dBm)");
				htmlString.append("</h3>");
			}
			else
			{
				htmlString
						.append("<h2>Missing data about currently-associated network</h2>");
			}
		}
		else
		{
			htmlString.append("<h3>No network</h3>");
		}

		for (WiFiSource stat : netStatus.getAvailableConnections().values())
		{
			if (!currentAP.equals(stat.getMacAddr()))
			{ // skip the currently-associated network, b/c we already got it
				htmlString.append("<p>");
				htmlString.append(stat.getId() + " (" + stat.getMacAddr() + ") - "
						+ stat.getSignalStrengthPercent() + "% ("
						+ stat.getSignalStrength() + "dBm)");
				htmlString.append("</p>");
			}
		}
		
		String htmlStringAsString = htmlString.toString();
		
		for (KmlFeature f : this.registeredFeatures)
		{
			f.setDescription(htmlStringAsString);
		}
	}

	public void apListUpdate(String newData)
	{
		this.netStatus.apListUpdate(newData);

		this.updateFeatures();
	}

	public void macAddressUpdate(String newData)
	{
		this.netStatus.macAddressUpdate(newData);
	}
}
