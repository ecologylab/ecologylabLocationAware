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
	
	//atom:author??
	
	//atom:link??
	
	@xml_leaf String address;
	
	//xal:AddressDetails??
	
	@xml_leaf @xml_tag("phoneNumber") String phoneNumber;
	
	//Snippet??
	
	@xml_leaf String description;
	
	//AbstractView??
	
	//TimePrimitive??
	
	// styleUrl??
	// StyleSelector??
	// Region??
	// ExtendData??
	
	/**
	 * 
	 */
	public KmlFeature()
	{
		// TODO Auto-generated constructor stub
	}

}
