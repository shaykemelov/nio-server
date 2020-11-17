package edu.shaykemelov.server;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import edu.shaykemelov.NioServer;
import edu.shaykemelov.server.sockets.Socket;
import edu.shaykemelov.server.sockets.SocketsAcceptor;
import edu.shaykemelov.server.sockets.SocketsProcessor;

public class NioServerImpl implements NioServer
{
	private final SocketsAcceptor socketsAcceptor;
	private final SocketsProcessor socketsProcessor;

	public NioServerImpl(final int port, final int incomingSocketQueueSize) throws IOException
	{
		final Queue<Socket> socketsQueue = new ArrayBlockingQueue<>(incomingSocketQueueSize);
		this.socketsAcceptor = new SocketsAcceptor(port, socketsQueue);
		this.socketsProcessor = new SocketsProcessor(socketsQueue);
	}

	@Override
	public void start()
	{
		final var socketsProcessorThread = new Thread(socketsProcessor);
		socketsProcessorThread.start();

		final var socketsAcceptorThread = new Thread(socketsAcceptor);
		socketsAcceptorThread.start();
	}

	@Override
	public void stop()
	{
		socketsAcceptor.stop();
		socketsProcessor.stop();
	}
}
