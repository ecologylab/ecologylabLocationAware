/**
 * 
 */
package ecologylab.services.distributed.server.contextmanager;

import java.nio.channels.SelectionKey;

import ecologylab.collections.Scope;
import ecologylab.oodss.distributed.impl.NIOServerIOThread;
import ecologylab.oodss.distributed.server.NIOServerProcessor;
import ecologylab.oodss.distributed.server.clientsessionmanager.HTTPGetClientSessionManager;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.SimplTypesScope;
import ecologylab.serialization.formatenums.StringFormat;
import ecologylab.services.messages.KmlRequest;
import ecologylab.services.messages.KmlResponse;

/**
 * This client manager simply serves whatever object is located at KML_DATA in the ObjectRegistry.
 * This is expected to be a Kml object.
 * 
 * @author Zach
 */
public class KMLGetClientSessionManager extends HTTPGetClientSessionManager
{
	/**
	 * @param token
	 * @param maxPacketSize
	 * @param server
	 * @param frontend
	 * @param socketKey
	 * @param translationScope
	 * @param registry
	 */
	public KMLGetClientSessionManager(String token, int maxPacketSize, NIOServerIOThread server,
			NIOServerProcessor frontend, SelectionKey socketKey, SimplTypesScope translationScope,
			Scope<?> registry)
	{
		super(token, maxPacketSize, server, frontend, socketKey, translationScope, registry);

		this.initialized = true;
	}

	@Override
	protected void translateResponseMessageToStringBufferContents(RequestMessage requestMessage,
			ResponseMessage responseMessage, StringBuilder outgoingMessageBuf)
			throws SIMPLTranslationException
	{
		outgoingMessageBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		
		SimplTypesScope.serialize(((KmlResponse) responseMessage).getKml(), outgoingMessageBuf, StringFormat.XML);
		
		outgoingMessageBuf.append("\r\n");
	}

	@Override
	protected void clearOutgoingMessageBuffer(StringBuilder outgoingMessageBuf)
	{
		outgoingMessageBuf.setLength(0);
	}

	@Override
	protected void clearOutgoingMessageHeaderBuffer(StringBuilder outgoingMessageHeaderBuf)
	{
		outgoingMessageHeaderBuf.setLength(0);
	}

	@Override
	protected void createHeader(int messageSize, StringBuilder outgoingMessageHeaderBuf,
			RequestMessage incomingRequest, ResponseMessage outgoingResponse, long uid)
	{
		outgoingMessageHeaderBuf.append("HTTP/1.1 200 OK");
		outgoingMessageHeaderBuf.append(HTTP_HEADER_LINE_DELIMITER);

		outgoingMessageHeaderBuf.append(CONTENT_LENGTH_STRING + ':');
		outgoingMessageHeaderBuf.append(messageSize);
		outgoingMessageHeaderBuf.append(HTTP_HEADER_LINE_DELIMITER);

		/*
		 * outgoingMessageHeaderBuf.append(UNIQUE_IDENTIFIER_STRING);
		 * outgoingMessageHeaderBuf.append(':'); outgoingMessageHeaderBuf.append(uid);
		 * outgoingMessageHeaderBuf.append(HTTP_HEADER_LINE_DELIMITER);
		 */

		outgoingMessageHeaderBuf.append("content-type: text/xml");

	}

	@Override
	protected RequestMessage translateGetRequest(CharSequence messageCharSequence, String startLineString)
			throws SIMPLTranslationException
	{
		return KmlRequest.STATIC_INSTANCE;
	}

	@Override
	protected RequestMessage translateOODSSRequest(CharSequence messageCharSequence, String startLineString)
			throws SIMPLTranslationException
	{
		return KmlRequest.STATIC_INSTANCE;
	}

	@Override
	protected RequestMessage translatePostRequest(CharSequence messageCharSequence, String startLineString)
			throws SIMPLTranslationException
	{
		return KmlRequest.STATIC_INSTANCE;
	}

	@Override
	protected RequestMessage translateOtherRequest(CharSequence messageCharSequence, String startLineString)
			throws SIMPLTranslationException
	{
		return KmlRequest.STATIC_INSTANCE;
	}
}
