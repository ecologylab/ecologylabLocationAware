/**
 * 
 */
package ecologylab.serialization.library.kml;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.library.kml.feature.Placemark;
import ecologylab.serialization.library.kml.feature.container.Document;
import ecologylab.serialization.library.kml.feature.container.Folder;
import ecologylab.serialization.library.kml.overlay.GroundOverlay;
import ecologylab.serialization.library.kml.overlay.PhotoOverlay;
import ecologylab.serialization.library.kml.overlay.ScreenOverlay;

/**
 * From http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#kml
 * 
 * The root element of a KML file. This element is required. It follows the xml declaration at the
 * beginning of the file. The hint attribute is used as a signal to Google Earth to display the file
 * as celestial data.
 * 
 * The <kml> element may also include the namespace for any external XML schemas that are referenced
 * within the file.
 * 
 * A basic <kml> element contains 0 or 1 Feature and 0 or 1 NetworkLinkControl.
 * 
 * Because of this, only one of placemark, folder, document, photooverlay, screenoverlay, or
 * groundoverlay can be non-null; this is enforced through the setter methods.
 * 
 * @author Zach
 */
public class Kml extends ElementState
{
	@simpl_composite
	@xml_tag("Placemark")
	Placemark			placemark			= null;

	@simpl_composite
	@xml_tag("Document")
	Document			document			= null;

	@simpl_composite
	@xml_tag("Folder")
	Folder				folder				= null;

	@simpl_composite
	@xml_tag("GroundOverlay")
	GroundOverlay	groundOverlay	= null;

	@simpl_composite
	@xml_tag("PhotoOverlay")
	PhotoOverlay	photoOverlay	= null;

	@simpl_composite
	@xml_tag("ScreenOverlay")
	ScreenOverlay	screenOverlay	= null;

	// TODO NetworkLink

	@simpl_scalar
	String				xmlns					= "http://earth.google.com/kml/2.2";

	/**
	 * No-argument constructor for automatic translation to/from KML.
	 */
	public Kml()
	{
	}

	public Placemark getPlacemark()
	{
		return placemark;
	}

	public void setPlacemark(Placemark placemark)
	{
		this.placemark = placemark;

		this.document = null;
		this.folder = null;
		this.photoOverlay = null;
		this.groundOverlay = null;
		this.screenOverlay = null;
	}

	public Document getDocument()
	{
		return document;
	}

	public void setDocument(Document document)
	{
		this.document = document;

		this.placemark = null;
		this.folder = null;
		this.photoOverlay = null;
		this.groundOverlay = null;
		this.screenOverlay = null;
	}

	public Folder getFolder()
	{
		return folder;
	}

	public void setFolder(Folder folder)
	{
		this.folder = folder;

		this.document = null;
		this.placemark = null;
		this.photoOverlay = null;
		this.groundOverlay = null;
		this.screenOverlay = null;
	}

	/**
	 * @return the groundOverlay
	 */
	public GroundOverlay getGroundOverlay()
	{
		return groundOverlay;
	}

	/**
	 * @param groundOverlay
	 *          the groundOverlay to set
	 */
	public void setGroundOverlay(GroundOverlay groundOverlay)
	{
		this.groundOverlay = groundOverlay;

		this.document = null;
		this.placemark = null;
		this.folder = null;
		this.photoOverlay = null;
		this.screenOverlay = null;
	}

	/**
	 * @return the photoOverlay
	 */
	public PhotoOverlay getPhotoOverlay()
	{
		return photoOverlay;
	}

	/**
	 * @param photoOverlay
	 *          the photoOverlay to set
	 */
	public void setPhotoOverlay(PhotoOverlay photoOverlay)
	{
		this.photoOverlay = photoOverlay;

		this.document = null;
		this.placemark = null;
		this.folder = null;
		this.groundOverlay = null;
		this.screenOverlay = null;
	}

	/**
	 * @return the screenOverlay
	 */
	public ScreenOverlay getScreenOverlay()
	{
		return screenOverlay;
	}

	/**
	 * @param screenOverlay
	 *          the screenOverlay to set
	 */
	public void setScreenOverlay(ScreenOverlay screenOverlay)
	{
		this.screenOverlay = screenOverlay;

		this.document = null;
		this.placemark = null;
		this.folder = null;
		this.groundOverlay = null;
		this.photoOverlay = null;
	}
}
