package edu.shaykemelov.protocols.http;

import java.nio.ByteBuffer;

import edu.shaykemelov.server.sockets.SocketsReader;

public class HttpReader implements SocketsReader
{
	@Override
	public void read(final ByteBuffer byteBuffer)
	{
		byteBuffer.flip();
	}
}
