package edu.shaykemelov.protocols.http.state;

import static edu.shaykemelov.protocols.http.state.HttpUtils.CARRIAGE_RETURN_CODE;
import static edu.shaykemelov.protocols.http.state.HttpUtils.LINEFEED_CODE;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import edu.shaykemelov.protocols.http.HttpRequest;

public class BodyState implements State
{
	private final List<Byte> content; // FIXME: плохая структура данных для того чтобы хранить тело http-запроса, нужно написать хороший расширяемый массив

	private boolean carriageReturnFound;

	public BodyState()
	{
		this.content = new ArrayList<>();
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
				content.add(b);
			}
		}

		return false;
	}

	@Override
	public void append(final HttpRequest.Builder builder)
	{
		final var bytes = new byte[content.size()];
		for (int i = 0; i < content.size(); i++)
		{
			bytes[i] = content.get(i);
		}

		builder.setContent(bytes);
	}
}
