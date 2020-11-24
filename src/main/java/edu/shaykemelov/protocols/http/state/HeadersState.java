package edu.shaykemelov.protocols.http.state;

import static edu.shaykemelov.protocols.http.state.HttpUtils.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import edu.shaykemelov.protocols.http.HttpRequest;

public class HeadersState implements State
{
	private final Map<String, String> headers;

	private StringBuilder headerNameBuilder;
	private StringBuilder headerValueBuilder;
	private boolean headerNameFound;
	private boolean carriageReturnFound;

	public HeadersState()
	{
		this.headers = new HashMap<>();
		this.headerNameBuilder = new StringBuilder();
		this.headerValueBuilder = new StringBuilder();
		this.headerNameFound = false;
		this.carriageReturnFound = false;
	}

	@Override
	public void nextState(final HasState hasState)
	{
		hasState.setNewState(new BodyState());
	}

	@Override
	public boolean read(final ByteBuffer byteBuffer)
	{
		while (byteBuffer.hasRemaining())
		{
			final var b = byteBuffer.get();

			if (HEADER_DELIMITER == b && !headerNameFound)
			{
				// Нашли название http заголовка
				headerNameFound = true;
			}
			else if (CARRIAGE_RETURN_CODE == b && !headerNameFound)
			{
				// Нашли все заголовки
				return true;
			}
			else if (CARRIAGE_RETURN_CODE == b)
			{
				// Возврат коретки т.е. прочитали значение заголовка
				carriageReturnFound = true;
			}
			else if (carriageReturnFound && LINEFEED_CODE == b)
			{
				// Нашли название и значение заголовка, сохраняем во временную мапку
				headers.put(headerNameBuilder.toString().strip(), headerValueBuilder.toString().strip()); // FIXME: создаются лишнии строки
				// чичстим все для следующей строки
				headerNameFound = false;
				carriageReturnFound = false;
				headerNameBuilder = new StringBuilder();
				headerValueBuilder = new StringBuilder();
			}
			else if (headerNameFound)
			{
				headerValueBuilder.append((char) b);
			}
			else
			{
				headerNameBuilder.append((char) b);
			}
		}

		return false;
	}

	@Override
	public void append(final HttpRequest.Builder builder)
	{
		builder.setHeaders(headers);
	}
}