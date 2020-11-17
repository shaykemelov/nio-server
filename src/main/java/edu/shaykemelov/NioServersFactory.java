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
		return new NioServerImpl(port, incomingSocketsQueueSize);
	}
}
