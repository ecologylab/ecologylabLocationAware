/**
 * 
 */
package ecologylab.services.distributed.server.contextmanager;

import java.nio.channels.SelectionKey;

import ecologylab.appframework.ObjectRegistry;
import ecologylab.services.distributed.impl.NIOServerBackend;
import ecologylab.services.distributed.server.NIOServerFrontend;
import ecologylab.services.distributed.server.clientmanager.ClientManager;
import ecologylab.xml.TranslationSpace;

/**
 * @author toupsz
 *
 */
public class KMLServerClientManager extends ClientManager
{

	/**
	 * @param sessionId
	 * @param maxPacketSize
	 * @param server
	 * @param frontend
	 * @param socketKey
	 * @param translationSpace
	 * @param registry
	 */
	public KMLServerClientManager(Object sessionId, int maxPacketSize,
			NIOServerBackend server, NIOServerFrontend frontend,
			SelectionKey socketKey, TranslationSpace translationSpace,
			ObjectRegistry<?> registry)
	{
		super(sessionId, maxPacketSize, server, frontend, socketKey,
				translationSpace, registry);
		// TODO Auto-generated constructor stub
	}

}
