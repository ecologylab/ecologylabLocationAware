/**
 * 
 */
package ecologylab.xml.library.kml.style.icon;

import ecologylab.xml.ElementState;

/**
 * Created according to
 * http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#icon
 * 
 * Defines an image associated with an Icon style or overlay. <Icon> has the
 * same child elements as <Link>. The required <href> child element defines the
 * location of the image to be used as the overlay or as the icon for the
 * placemark. This location can either be on a local file system or a remote web
 * server.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu) (Java classes only)
 */
public class Icon extends ElementState
{
	/** An HTTP address or a local file specification used to load an icon. */
	@xml_leaf private String										href;

	/**
	 * Specifies a time-based refresh mode, which can be one of the following:
	 * 
	 * onChange - refresh when the file is loaded and whenever the Link
	 * parameters change (the default).
	 * 
	 * onInterval - refresh every n seconds (specified in <refreshInterval>).
	 * 
	 * onExpire - refresh the file when the expiration time is reached. If a
	 * fetched file has a NetworkLinkControl, the <expires> time takes precedence
	 * over expiration times specified in HTTP headers. If no <expires> time is
	 * specified, the HTTP max-age header is used (if present). If max-age is not
	 * present, the Expires HTTP header is used (if present). (See Section
	 * RFC261b of the Hypertext Transfer Protocol - HTTP 1.1 for details on HTTP
	 * header fields.)
	 */
	@xml_leaf @xml_tag("refreshMode") private String		refreshMode;

	/** Indicates to refresh the file every n seconds. */
	@xml_leaf @xml_tag("refreshInterval") private float	refreshInterval;

	/**
	 * Specifies how the link is refreshed when the "camera" changes. Can be one
	 * of the following:
	 * 
	 * never (default) - Ignore changes in the view. Also ignore <viewFormat>
	 * parameters, if any.
	 * 
	 * onStop - Refresh the file n seconds after movement stops, where n is
	 * specified in <viewRefreshTime>.
	 * 
	 * onRequest - Refresh the file only when the user explicitly requests it.
	 * (For example, in Google Earth, the user right-clicks and selects Refresh
	 * in the Context menu.)
	 * 
	 * onRegion - Refresh the file when the Region becomes active. See <Region>.
	 */
	@xml_leaf @xml_tag("viewRefreshMode") private String	viewRefreshMode;

	/**
	 * After camera movement stops, specifies the number of seconds to wait
	 * before refreshing the view. (See <viewRefreshMode> and onStop above.)
	 */
	@xml_leaf @xml_tag("viewRefreshTime") private float	viewRefreshTime;

	/**
	 * Scales the BBOX parameters before sending them to the server. A value less
	 * than 1 specifies to use less than the full view (screen). A value greater
	 * than 1 specifies to fetch an area that extends beyond the edges of the
	 * current view.
	 */
	@xml_leaf @xml_tag("viewBoundScale") private float		viewBoundScale;

	/**
	 * Specifies the format of the query string that is appended to the Link's
	 * <href> before the file is fetched.(If the <href> specifies a local file,
	 * this element is ignored.) If you specify a <viewRefreshMode> of onStop and
	 * do not include the <viewFormat> tag in the file, the following information
	 * is automatically appended to the query string:
	 * 
	 * BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]
	 * 
	 * This information matches the Web Map Service (WMS) bounding box
	 * specification. If you specify an empty <viewFormat> tag, no information is
	 * appended to the query string. You can also specify a custom set of viewing
	 * parameters to add to the query string. If you supply a format string, it
	 * is used instead of the BBOX information. If you also want the BBOX
	 * information, you need to add those parameters along with the custom
	 * parameters. You can use any of the following parameters in your format
	 * string (and Google Earth will substitute the appropriate current value at
	 * the time it creates the query string):
	 * 
	 * [lookatLon], [lookatLat] - longitude and latitude of the point that
	 * <LookAt> is viewing
	 * 
	 * [lookatRange], [lookatTilt], [lookatHeading] - values used by the <LookAt>
	 * element (see descriptions of <range>, <tilt>, and <heading> in <LookAt>)
	 * 
	 * [lookatTerrainLon], [lookatTerrainLat], [lookatTerrainAlt] - point on the
	 * terrain in degrees/meters that <LookAt> is viewing
	 * 
	 * [cameraLon], [cameraLat], [cameraAlt] - degrees/meters of the eyepoint for
	 * the camera
	 * 
	 * [horizFov], [vertFov] - horizontal, vertical field of view for the camera
	 * 
	 * [horizPixels], [vertPixels] - size in pixels of the 3D viewer
	 * 
	 * [terrainEnabled] - indicates whether the 3D viewer is showing terrain
	 * 
	 * 
	 */
	@xml_leaf @xml_tag("viewFormat") private String			viewFormat;

	/**
	 * Appends information to the query string, based on the parameters
	 * specified. (Google Earth substitutes the appropriate current value at the
	 * time it creates the query string.) The following parameters are supported:
	 * 
	 * [clientVersion]
	 * 
	 * [kmlVersion]
	 * 
	 * [clientName]
	 * 
	 * [language]
	 */
	@xml_leaf @xml_tag("httpQuery") private String			httpQuery;

	public Icon()
	{
	}

	/**
	 * Constructs a new icon with the given URL.
	 * 
	 * @param iconURL
	 */
	public Icon(String iconURL)
	{
		this.href = iconURL;
	}

	public void setHref(String href)
	{
		this.href = href;
	}
}
