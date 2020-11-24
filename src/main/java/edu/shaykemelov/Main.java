package edu.shaykemelov;

import java.io.IOException;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		final var port = Integer.parseInt(args[0]);
		final var incomingSocketsQueueSize = Integer.parseInt(args[1]);
		final var readBufferSize = Integer.parseInt(args[2]);
		final var writeBufferSize = Integer.parseInt(args[3]);

		final var server = NioServersFactory.create(port, incomingSocketsQueueSize, readBufferSize, writeBufferSize);
		server.start();
	}
}
