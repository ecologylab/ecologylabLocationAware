package ecologylab.services.messages;

import ecologylab.oodss.messages.RequestMessage;

public interface RequestTranslator {

	public RequestMessage getTranslatedMessage(String startLineString);
}
