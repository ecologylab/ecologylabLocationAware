/**
 * 
 */
package ecologylab.services.distributed.server.contextmanager;

import java.io.UnsupportedEncodingException;
import java.nio.channels.SelectionKey;

import ecologylab.collections.Scope;
import ecologylab.services.distributed.impl.NIOServerIOThread;
import ecologylab.services.distributed.server.NIOServerProcessor;
import ecologylab.services.distributed.server.clientsessionmanager.ClientSessionManager;
import ecologylab.services.messages.KmlRequest;
import ecologylab.services.messages.KmlResponse;
import ecologylab.services.messages.RequestMessage;
import ecologylab.services.messages.ResponseMessage;
import ecologylab.xml.TranslationScope;
import ecologylab.xml.XMLTranslationException;

/**
 * This client manager simply serves whatever object is located at KML_DATA in
 * the ObjectRegistry. This is expected to be a Kml object.
 * 
 * @author Zach
 */
public class KMLGetClientSessionManager extends ClientSessionManager
{
	/**
	 * @param token
	 * @param maxPacketSize
	 * @param server
	 * @param frontend
	 * @param socketKey
	 * @param translationSpace
	 * @param registry
	 */
	public KMLGetClientSessionManager(Object token, int maxPacketSize,
			NIOServerIOThread server, NIOServerProcessor frontend,
			SelectionKey socketKey, TranslationScope translationSpace,
			Scope<?> registry)
	{
		super(token, maxPacketSize, server, frontend, socketKey,
				translationSpace, registry);

		this.initialized = true;
	}

	@Override protected void translateResponseMessageToStringBufferContents(
			RequestMessage requestMessage, ResponseMessage responseMessage,
			StringBuilder outgoingMessageBuf) throws XMLTranslationException
	{
		outgoingMessageBuf
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		((KmlResponse) responseMessage).getKml().translateToXML(
				outgoingMessageBuf);
		outgoingMessageBuf.append("\r\n");
	}

	@Override protected RequestMessage translateStringToRequestMessage(
			CharSequence messageSequence) throws XMLTranslationException,
			UnsupportedEncodingException
	{
		return KmlRequest.STATIC_INSTANCE;
	}

	@Override protected void clearOutgoingMessageBuffer(
			StringBuilder outgoingMessageBuf)
	{
		outgoingMessageBuf.setLength(0);
	}

	@Override protected void clearOutgoingMessageHeaderBuffer(
			StringBuilder outgoingMessageHeaderBuf)
	{
		outgoingMessageHeaderBuf.setLength(0);
	}

	protected void createHeader(StringBuilder outgoingMessageBuf,
			StringBuilder outgoingMessageHeaderBuf,
			RequestMessage incomingRequest, ResponseMessage outgoingResponse,
			long uid)
	{
		outgoingMessageHeaderBuf.append("HTTP/1.1 200 OK");
		outgoingMessageHeaderBuf.append(HTTP_HEADER_LINE_DELIMITER);
		
		outgoingMessageHeaderBuf.append(CONTENT_LENGTH_STRING+':');
		outgoingMessageHeaderBuf.append(outgoingMessageBuf.length());
		outgoingMessageHeaderBuf.append(HTTP_HEADER_LINE_DELIMITER);

/*		outgoingMessageHeaderBuf.append(UNIQUE_IDENTIFIER_STRING);
		outgoingMessageHeaderBuf.append(':');
		outgoingMessageHeaderBuf.append(uid);
		outgoingMessageHeaderBuf.append(HTTP_HEADER_LINE_DELIMITER);*/

		outgoingMessageHeaderBuf.append("content-type: text/xml");
		outgoingMessageHeaderBuf.append(HTTP_HEADER_TERMINATOR);

	}

	@Override protected void prepareBuffers(StringBuilder incomingMessageBuf, StringBuilder outgoingMessageBuf, StringBuilder outgoingMessageHeaderBuf)
	{
	}
}
