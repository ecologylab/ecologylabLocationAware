/**
 * 
 */
package ecologylab.standalone;

import java.io.File;
import java.util.List;

import ecologylab.sensor.BaseSensorTranslations;
import ecologylab.services.logging.Logging;
import ecologylab.services.logging.WiFiGPSStatusOp;
import ecologylab.services.logging.translationScope.LoggingTranslationsProvider;
import ecologylab.xml.ElementState;
import ecologylab.xml.TranslationScope;
import ecologylab.xml.XMLTranslationException;

/**
 * Demonstrates how to load log data, and how to access it.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
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
		TranslationScope[] spacesToCompose =
		{ BaseSensorTranslations.get(), LoggingTranslationsProvider.get() };
		TranslationScope translations = TranslationScope.get(
				"gps wifi log translations", spacesToCompose);

		// translate the file from XML into a Logging of type WiFiGPSStatusOp
		Logging<WiFiGPSStatusOp> ops = (Logging<WiFiGPSStatusOp>) ElementState
				.translateFromXML(f, translations);

		// extract the sequence of operations
		List<WiFiGPSStatusOp> opSeq = ops.getOpSequence();

		// show off some of the contents. :)
		System.out.println("First op: ");
		opSeq.get(0).serialize(System.out);

		System.out.println();
		System.out.println("9th op: ");
		opSeq.get(10).serialize(System.out);

		System.out.println();
		System.out.println("last op (" + (opSeq.size() - 1) + "): ");
		opSeq.get(opSeq.size() - 1).serialize(System.out);

		// in order to use these objects, you can use the getLocationStatus() and
		// getNetworkStatus() methods in WiFiGPSStatusOp
	}
}
