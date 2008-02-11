/**
 * 
 */
package ecologylab.xml.library.kml;

import ecologylab.xml.ElementState;
import ecologylab.xml.library.kml.feature.Placemark;
import ecologylab.xml.library.kml.feature.container.Document;
import ecologylab.xml.library.kml.feature.container.Folder;

/**
 * From http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#kml
 * 
 * The root element of a KML file. This element is required. It follows the xml
 * declaration at the beginning of the file. The hint attribute is used as a
 * signal to Google Earth to display the file as celestial data.
 * 
 * The <kml> element may also include the namespace for any external XML schemas
 * that are referenced within the file.
 * 
 * A basic <kml> element contains 0 or 1 Feature and 0 or 1 NetworkLinkControl.
 * 
 * Because of this, only one of placemark, folder, document, photooverlay,
 * screenoverlay, or groundoverlay can be non-null; this is enforced through the
 * setter methods.
 * 
 * @author Zach
 */
public class Kml extends ElementState
{
	@xml_nested @xml_tag("Placemark") Placemark	placemark	= null;

	@xml_nested @xml_tag("Document") Document		document		= null;

	@xml_nested @xml_tag("Folder") Folder			folder		= null;

	// TODO Folder, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay

	@xml_attribute String								xmlns			= "http://earth.google.com/kml/2.2";

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
	}
}
