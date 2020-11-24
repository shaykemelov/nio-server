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
	public final static class Builder
	{
		private final int port;

		private Integer incomingSocketsQueueSize;
		private Integer readBufferSize;
		private Integer writeBufferSize;

		private Builder(final int port)
		{
			this.port = port;
		}

		public Builder setIncomingSocketsQueueSize(final int incomingSocketsQueueSize)
		{
			this.incomingSocketsQueueSize = incomingSocketsQueueSize;
			return this;
		}

		public Builder setReadBufferSize(final int readBufferSize)
		{
			this.readBufferSize = readBufferSize;
			return this;
		}

		public Builder setWriteBufferSize(final int writeBufferSize)
		{
			this.writeBufferSize = writeBufferSize;
			return this;
		}

		public NioServer build() throws IOException
		{
			if (incomingSocketsQueueSize != null && readBufferSize != null && writeBufferSize != null)
			{
				return new NioServerImpl(port, incomingSocketsQueueSize, readBufferSize, writeBufferSize);
			}
			else if (incomingSocketsQueueSize != null)
			{
				return new NioServerImpl(port, incomingSocketsQueueSize);
			}
			else
			{
				return new NioServerImpl(port);
			}
		}
	}

	public static Builder builder(final int port)
	{
		return new Builder(port);
	}

	private final SocketsAcceptor socketsAcceptor;
	private final SocketsProcessor socketsProcessor;

	private NioServerImpl(final int port) throws IOException
	{
		this(port, 1000);
	}

	private NioServerImpl(final int port, final int incomingSocketsQueueSize) throws IOException
	{
		this(port, incomingSocketsQueueSize, 1024 * 1024, 1024 * 1024);
	}

	private NioServerImpl(final int port, final int incomingSocketQueueSize, final int readBufferSize, final int writeBufferSize) throws IOException
	{
		final Queue<Socket> socketsQueue = new ArrayBlockingQueue<>(incomingSocketQueueSize);
		this.socketsAcceptor = new SocketsAcceptor(port, socketsQueue);
		this.socketsProcessor = new SocketsProcessor(socketsQueue, readBufferSize, writeBufferSize);
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
