package edu.shaykemelov.protocols.http.state;

import static edu.shaykemelov.protocols.http.state.HttpUtils.CARRIAGE_RETURN_CODE;
import static edu.shaykemelov.protocols.http.state.HttpUtils.LINEFEED_CODE;

import java.nio.ByteBuffer;

import edu.shaykemelov.protocols.http.HttpRequest;

public class RequestLineState implements State
{
	private final StringBuilder requestLineBuilder;

	private boolean carriageReturnFound;

	public RequestLineState()
	{
		this.requestLineBuilder = new StringBuilder();
		this.carriageReturnFound = false;
	}

	@Override
	public void nextState(final HasState hasState)
	{
		hasState.setNewState(new HeadersState());
	}

	@Override
	public boolean read(final ByteBuffer byteBuffer)
	{
		while (byteBuffer.hasRemaining())
		{
			final var b = byteBuffer.get();

			if (CARRIAGE_RETURN_CODE == b)
			{
				carriageReturnFound = true;
			}
			else if (carriageReturnFound && LINEFEED_CODE == b)
			{
				return true;
			}
			else
			{
				requestLineBuilder.append((char) b);
			}
		}

		return false;
	}

	@Override
	public void append(final HttpRequest.Builder builder)
	{
		builder.setRequestLine(requestLineBuilder.toString());
	}
}
