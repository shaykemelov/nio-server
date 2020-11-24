package edu.shaykemelov.protocols.http;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpRequest
{
	public static final class Builder
	{
		private static final Pattern REQUEST_LINE_PATTERN = Pattern.compile("(\\S+)\\s(\\S+)\\s(\\S+)");

		private HttpMethod httpMethod;
		private URI requestURI;
		private Map<String, String> headers;
		private byte[] content;

		private Builder()
		{

		}

		public void setRequestLine(final String requestLine)
		{
			final var matcher = REQUEST_LINE_PATTERN.matcher(requestLine);
			if (matcher.find())
			{
				httpMethod = HttpMethod.valueOf(matcher.group(1));
				requestURI = URI.create(matcher.group(2));
			}// TODO: обработать ситуацию, когда пришел некорректный request line у http запрроса
		}

		public String getHeaderValue(final String headerName)
		{
			return headers.get(headerName);
		}

		public void setHeaders(final Map<String, String> headers)
		{
			this.headers = headers;
		}

		public void setContent(final byte[] content)
		{
			this.content = content;
		}

		public HttpRequest build()
		{
			return new HttpRequest(httpMethod, requestURI, headers, content);
		}
	}

	public static HttpRequest.Builder builder()
	{
		return new HttpRequest.Builder();
	}

	private final HttpMethod httpMethod;
	private final URI requestURI;
	private final Map<String, String> headers;
	private final byte[] content;

	private HttpRequest(final HttpMethod httpMethod, final URI requestURI, final Map<String, String> headers, final byte[] content)
	{
		this.httpMethod = httpMethod;
		this.requestURI = requestURI;
		this.headers = Map.copyOf(headers);
		this.content = content == null ? null : content.clone();
	}

	public HttpMethod getHttpMethod()
	{
		return httpMethod;
	}

	public URI getRequestURI()
	{
		return requestURI;
	}

	public Map<String, String> getHeaders()
	{
		return headers;
	}

	public byte[] getContent()
	{
		return content;
	}

	@Override
	public String toString()
	{
		return "HTTP method: " + httpMethod + System.lineSeparator()
		       + "Request URI: " + requestURI + System.lineSeparator()
		       + "Headers: " + headers + System.lineSeparator()
		       + "Body: " + Arrays.toString(content);
	}
}
