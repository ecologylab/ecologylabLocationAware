/**
 * 
 */
package ecologylab.composite.kml.sensor;

import java.awt.Color;

import ecologylab.generic.Debug;
import ecologylab.rendering.tweener.ColorTweener;
import ecologylab.sensor.network.wireless.WiFiAdapter;
import ecologylab.sensor.network.wireless.data.WiFiAdapterStatus;
import ecologylab.sensor.network.wireless.data.WiFiSource;
import ecologylab.sensor.network.wireless.listener.WiFiStringDataListener;
import ecologylab.xml.library.kml.style.Icon;
import ecologylab.xml.library.kml.style.IconStyle;

/**
 * Provides an IconStyle for use in KML, whose color is based on the current
 * status of the WiFi adapter on the machine it is running on.
 * 
 * Icon tint is based on the provided color scale; the point on the scale is
 * selected based on the % connectivity from the current WiFi adapter.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
public class WiFiColoredIcon extends Debug implements WiFiStringDataListener,
		Cloneable
{
	/**
	 * A WiFiColoredIcon that shows a white blank Google Earth paddle when
	 * disconnected and runs between a starred paddle with green for a full
	 * connection to a starred paddle in red for a poor connection.
	 * 
	 * NOTE: THIS INSTANCE IS NOT SAFE TO BE REUSED FOR MULTIPLE WIFI ADAPTERS ON
	 * A SINGLE VM; in order to use this instance in that way, you should call
	 * clone() on it first.
	 */
	public static final WiFiColoredIcon	GREEN_RED_STANDARD	= new WiFiColoredIcon(
																						"http://maps.google.com/mapfiles/kml/paddle/wht-stars.png",
																						"http://maps.google.com/mapfiles/kml/paddle/wht-blank.png",
																						Color.RED,
																						Color.GREEN,
																						Color.WHITE);

	protected WiFiAdapterStatus			netStatus				= new WiFiAdapterStatus();

	protected ColorTweener					colorTweener;

	protected Color							offlineColor;

	protected IconStyle						icon;

	protected String							onlineIconURL;

	protected String							offlineIconURL;

	/**
	 * 
	 */
	public WiFiColoredIcon(String onlineIconURL, String offlineIconURL,
			Color onlinePoorConnectionColor, Color onlineStrongConnectionColor,
			Color offlineColor)
	{
		this.onlineIconURL = onlineIconURL;
		this.offlineIconURL = offlineIconURL;

		this.offlineColor = offlineColor;

		this.colorTweener = new ColorTweener(onlinePoorConnectionColor,
				onlineStrongConnectionColor, 100);

		this.icon = new IconStyle(null, offlineIconURL, 1.0f, 0f,
				Icon.CENTERED_HOTSPOT, offlineColor, "normal");
	}

	protected void configureIconOffline()
	{
		this.icon.setHref(offlineIconURL);
		this.icon.setColor(offlineColor);
	}

	protected void updateIconForState()
	{
		String currentAP = netStatus.getCurrentMacAddr();

		if (WiFiAdapter.NOT_ASSOCIATED.equals(currentAP))
		{
			// configure for not connected
			this.configureIconOffline();
		}
		else
		{
			WiFiSource stat = netStatus.getAvailableConnections().get(currentAP);

			if (stat != null)
			{
				this.icon.setHref(this.onlineIconURL);
				this.icon.setColor(this.colorTweener.getState(stat
						.getSignalStrengthPercent()));
			}
			else
			{
				warning("The WiFi adapter is reporting MAC: "
						+ currentAP
						+ ", but the MAC does not exist in the list of available APs.");

				this.configureIconOffline();
			}
		}
	}

	public void apListUpdate(String newData)
	{
		this.netStatus.apListUpdate(newData);

		this.updateIconForState();
	}

	public void macAddressUpdate(String newData)
	{
		this.netStatus.macAddressUpdate(newData);

		this.updateIconForState();
	}

	/**
	 * Provides a reference to the IconStyle from this object. This IconStyle
	 * never becomes re-instantiated, so users can simply request it once, and
	 * then reuse it to have the latest visualization of the WiFi status.
	 * 
	 * @return
	 */
	public IconStyle getIcon()
	{
		return icon;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override protected WiFiColoredIcon clone()
			throws CloneNotSupportedException
	{
		return new WiFiColoredIcon(onlineIconURL, offlineIconURL,
				this.colorTweener.getState(0), this.colorTweener.getState(99),
				offlineColor);
	}
}