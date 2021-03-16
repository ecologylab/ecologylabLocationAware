/**
 * 
 */
package ecologylab.standalone.ImageGeotagger.DirectoryMonitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Notifies a listener when an image arrives, and what the image is.
 * 
 * @author Z O. Toups (ztoups@nmsu.edu)
 */
public class ImgArrivesMonitor extends ImgDirMonitor
{
	private final ActionListener	al;

	public static final String		IMAGE_ARRIVED	= "IMAGE_ARRIVED";

	/**
	 * @param directory
	 * @throws Exception
	 */
	public ImgArrivesMonitor(File directory, ActionListener al) throws Exception
	{
		super(directory);

		this.al = al;
	}

	/**
	 * @see ecologylab.standalone.ImageGeotagger.DirectoryMonitor.ImgDirMonitor#processFile(java.io.File)
	 */
	@Override
	protected void processFile(File file)
	{
		al.actionPerformed(new ActionEvent(file, 0, IMAGE_ARRIVED));
	}
}
