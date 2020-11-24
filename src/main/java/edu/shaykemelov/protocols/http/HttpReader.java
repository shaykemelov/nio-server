package edu.shaykemelov.protocols.http;

import java.nio.ByteBuffer;

import edu.shaykemelov.protocols.http.state.BodyState;
import edu.shaykemelov.protocols.http.state.HasState;
import edu.shaykemelov.protocols.http.state.HeadersState;
import edu.shaykemelov.protocols.http.state.RequestLineState;
import edu.shaykemelov.protocols.http.state.State;
import edu.shaykemelov.server.sockets.SocketReader;

public class HttpReader implements SocketReader, HasState
{
	private State currentState;
	private HttpRequest.Builder builder;

	public HttpReader()
	{
		this.currentState = new RequestLineState();
		this.builder = HttpRequest.builder();
	}

	@Override
	public void read(final ByteBuffer byteBuffer)
	{
		byteBuffer.flip();

		var read = currentState.read(byteBuffer);

		if (read)
		{
			currentState.append(builder);

			if (currentState instanceof HeadersState)
			{
				final var contentLength = builder.getHeaderValue("Content-Length");

				if (null == contentLength)
				{
					final var httpRequest = builder.build();
					System.out.println(httpRequest.toString());
					System.out.println();

					builder = HttpRequest.builder();
					setNewState(new RequestLineState());
				}
				else
				{
					nextState();
				}
			}
			else if (currentState instanceof BodyState)
			{
				final var httpRequest = builder.build();
				System.out.println(httpRequest.toString());
				System.out.println();
				nextState();
			}
			else
			{
				nextState();
			}
		}

		while (byteBuffer.hasRemaining())
		{
			read = currentState.read(byteBuffer);
			if (read)
			{
				nextState();
			}
		}

		byteBuffer.clear();
	}

	@Override
	public void nextState()
	{
		currentState.nextState(this);
	}

	@Override
	public void setNewState(final State newState)
	{
		this.currentState = newState;
	}
}