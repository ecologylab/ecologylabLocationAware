/**
 * 
 */
package ecologylab.standalone;

import java.io.File;

import ecologylab.sensor.BaseSensorTranslations;
import ecologylab.services.logging.Logging;
import ecologylab.services.logging.LoggingTranslations;
import ecologylab.services.logging.WiFiGPSStatusOp;
import ecologylab.xml.ElementState;
import ecologylab.xml.TranslationSpace;
import ecologylab.xml.XMLTranslationException;
import ecologylab.xml.types.element.ArrayListState;

/**
 * Demonstrates how to load log data, and how to access it.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
public class DemoLogUse
{

	/**
	 * @param args
	 * @throws XMLTranslationException
	 */
	public static void main(String[] args) throws XMLTranslationException
	{
		// create a new file from the argument to the application
		File f = new File(args[0]);

		// compose the necessary translation spaces
		// in this case, we need generic logging, and the sensor space
		TranslationSpace[] spacesToCompose =
		{ BaseSensorTranslations.get(), LoggingTranslations.get() };
		TranslationSpace translations = TranslationSpace.get(
				"gps wifi log translations", spacesToCompose);

		// translate the file from XML into a Logging of type WiFiGPSStatusOp
		Logging<WiFiGPSStatusOp> ops = (Logging<WiFiGPSStatusOp>) ElementState
				.translateFromXML(f, translations);

		// extract the sequence of operations
		ArrayListState<WiFiGPSStatusOp> opSeq = ops.getOpSequence();

		// show off some of the contents. :)
		System.out.println("First op: ");
		opSeq.get(0).translateToXML(System.out);

		System.out.println();
		System.out.println("9th op: ");
		opSeq.get(10).translateToXML(System.out);

		System.out.println();
		System.out.println("last op (" + (opSeq.size() - 1) + "): ");
		opSeq.get(opSeq.size() - 1).translateToXML(System.out);

		// in order to use these objects, you can use the getLocationStatus() and
		// getNetworkStatus() methods in WiFiGPSStatusOp
	}
}
