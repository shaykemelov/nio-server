package edu.shaykemelov;

import java.io.IOException;

import edu.shaykemelov.server.NioServerImpl;

public final class NioServersFactory
{
	private NioServersFactory()
	{
	}

	public static NioServer create(final int port, final int incomingSocketsQueueSize) throws IOException
	{
		return NioServerImpl.builder(port)
				.setIncomingSocketsQueueSize(incomingSocketsQueueSize)
				.build();
	}

	public static NioServer create(final int port, final int incomingSocketsQueueSize, final int readBufferSize, final int writeBufferSize) throws IOException
	{
		return NioServerImpl.builder(port)
				.setIncomingSocketsQueueSize(incomingSocketsQueueSize)
				.setReadBufferSize(readBufferSize)
				.setWriteBufferSize(writeBufferSize)
				.build();
	}
}
