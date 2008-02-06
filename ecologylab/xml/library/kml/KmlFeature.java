/**
 * 
 */
package ecologylab.xml.library.kml;

import ecologylab.xml.xml_inherit;

/**
 * @author Zach
 *
 */
@xml_inherit
public abstract class KmlFeature extends KmlObject
{
	@xml_leaf String name;
	
	@xml_leaf int visibility; // actually a boolean -- can only be 0 or 1
	
	@xml_leaf int open; // actually a boolean -- can only be 0 or 1
	
	//TODO atom:author??
	
	//TODO atom:link??
	
	@xml_leaf String address;
	
	//TODO xal:AddressDetails??
	
	@xml_leaf @xml_tag("phoneNumber") String phoneNumber;
	
	//TODO Snippet??
	
	@xml_leaf String description;
	
	//TODO AbstractView??
	
	//TODO TimePrimitive??
	
	//TODO  styleUrl??
	//TODO  StyleSelector??
	//TODO  Region??
	//TODO  ExtendData??
	
	/**
	 * 
	 */
	public KmlFeature()
	{
		// TODO Auto-generated constructor stub
	}

}
