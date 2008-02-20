/**
 * 
 */
package ecologylab.standalone;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * Provides an application that repairs damaged log files.
 * 
 * Inserts hard line breaks in between each log op, and ensures that the closing
 * tags for the file are intact.
 * 
 * Then truncates the file down to its actual size.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
public class LogRepair
{

	/**
	 * @param args
	 *           usage LogRepair <file to be repaired> <custom XML for ops (no
	 *           pointy braces)> <custom logging tag (optional)> <custom
	 *           op_sequence tag (optional)>
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		System.out.println("args: ");
		for (String s : args)
		{
			System.out.println(s);
		}
		
		String opTag, loggingTag, opSeqTag;
		String opTagSearch;
		int advanceLength;

		if (args.length < 2 || (opTag = args[1]) == null)
		{
			throw new Exception("must provide the XML tag for ops");
		}

		opTagSearch = "><" + opTag;
		advanceLength = opTagSearch.length();

		if (args.length < 3 || (loggingTag = args[2]) == null)
		{
			loggingTag = "logging";
		}

		if (args.length < 4 || (opSeqTag = args[3]) == null)
		{
			opSeqTag = "op_sequence";
		}

		CharsetDecoder decoder = Charset.forName("ASCII").newDecoder();
		CharsetEncoder encoder = Charset.forName("ASCII").newEncoder();

		File logFile = new File(args[0]);

		String newName = args[0] + ".tmp";

		File newLogFile = new File(newName);

		if (newLogFile.exists())
		{
			throw new Exception(newName + " already exists; cannot continue.");
		}

		newLogFile.createNewFile();

		FileChannel writeChannel = new RandomAccessFile(newLogFile, "rw")
				.getChannel();
		FileChannel readChannel = new RandomAccessFile(logFile, "r").getChannel();

		long currentChunk = 1; // 1 indexed
		long chunkSize = 512; // bytes
		long currentChunkSize = chunkSize;

		ByteBuffer b = ByteBuffer.allocate((int) chunkSize);
		CharBuffer c;
		StringBuilder modifyBuilder = null;

		int foundIndex;

		// read a chunk of the file
		if (readChannel.size() < currentChunk * chunkSize)
		{ // want to make sure we don't read past the end of the file
			currentChunkSize = readChannel.size()
					- ((currentChunk - 1) * chunkSize);
		}

		while (readChannel.read(b) != -1)
		{
			c = decoder.decode(b);

			// modify the char buffer
			modifyBuilder = new StringBuilder(c.toString());

			foundIndex = 0;

			// find the first problem area
			foundIndex = modifyBuilder.indexOf(opTagSearch, foundIndex);

			while (foundIndex != -1)
			{
				// move to between the two tags
				foundIndex += 1;

				modifyBuilder.insert(foundIndex, "\r\n");

				// account for moving forward one, and then inserting two characters
				foundIndex += (advanceLength - 3);

				foundIndex = modifyBuilder.indexOf(opTagSearch, foundIndex);
			}

			// write the new section of file
			char[] chars = new char[modifyBuilder.length()];
			modifyBuilder.getChars(0, modifyBuilder.length(), chars, 0);
			writeChannel.write(encoder.encode(CharBuffer.wrap(chars)));

			// advance to the next section of the file
		}

		// nothing left to read, so we have all the carriage returns entered
		writeChannel.force(true);
	}
}
