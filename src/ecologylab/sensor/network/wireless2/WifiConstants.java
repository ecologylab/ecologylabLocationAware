package ecologylab.sensor.network.wireless2;


public interface WifiConstants
{
	/* values for interface states */
	public static final int WLAN_INTERFACE_STATE_NOT_READY               = 0;
	public static final int WLAN_INTERFACE_STATE_CONNECTED               = 1;
	public static final int WLAN_INTERFACE_STATE_AD_HOC_NETWORK_FORMED   = 2;
	public static final int WLAN_INTERFACE_STATE_DISCONNECTING           = 3;
	public static final int WLAN_INTERFACE_STATE_DISCONNECTED            = 4;
	public static final int WLAN_INTERFACE_STATE_ASSOCIATING             = 5;
	public static final int WLAN_INTERFACE_STATE_DISCOVERING             = 6;
	public static final int WLAN_INTERFACE_STATE_AUTHENTICATING          = 7;
	
	public static final int INTF_OPER_STATUS_UP									= 1;
	public static final int INTF_OPER_STATUS_DOWN								= 2;
	public static final int INTF_OPER_STATUS_TESTING							= 3;
	public static final int INTF_OPER_STATUS_UNKNOWN							= 4;
	public static final int INTF_OPER_STATUS_DORMANT							= 5;
	public static final int INTF_OPER_STATUS_NOT_PRESENT						= 6;
	public static final int INTF_OPER_STATUS_LOWER_LAYER_DOWN				= 7;
}
