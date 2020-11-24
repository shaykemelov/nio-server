package edu.shaykemelov.protocols.http.state;

import java.nio.ByteBuffer;

import edu.shaykemelov.protocols.http.HttpRequest;

public interface State
{
	void nextState(HasState hasState);

	boolean read(ByteBuffer byteBuffer);

	void append(HttpRequest.Builder builder);
}
