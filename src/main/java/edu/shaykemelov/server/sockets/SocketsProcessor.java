package edu.shaykemelov.server.sockets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class SocketsProcessor implements Runnable
{
	private final Queue<Socket> socketsQueue;
	private final Map<Long, Socket> socketsMap;

	private final Selector readSelector;
	private final ByteBuffer readByteBuffer;

	private final Selector writeSelector;
	private final ByteBuffer writeByteBuffer;

	private volatile boolean running;

	public SocketsProcessor(Queue<Socket> socketsQueue) throws IOException
	{
		this.socketsQueue = socketsQueue;
		this.socketsMap = new HashMap<>();
		this.readSelector = Selector.open();
		this.writeSelector = Selector.open();
		this.readByteBuffer = ByteBuffer.allocate(1024 * 1024);
		this.writeByteBuffer = ByteBuffer.allocate(1024 * 1024);
	}

	@Override
	public void run()
	{
		running = true;

		while (running)
		{
			try
			{
				executeCycle();
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void stop()
	{
		running = false;
	}

	private void executeCycle() throws IOException
	{
		takeNewSockets();
		readFromSockets();
		writeToSockets();
	}

	private void takeNewSockets() throws IOException
	{
		var socket = socketsQueue.poll();

		while (socket != null)
		{
			final var socketChannel = socket.getSocketChannel();
			socketChannel.configureBlocking(false);
			socketChannel.register(readSelector, SelectionKey.OP_READ, socket);

			socketsMap.put(socket.getSocketId(), socket);

			socket = socketsQueue.poll();
		}
	}

	private void readFromSockets() throws IOException
	{
		final var readReady = readSelector.selectNow();

		if (readReady > 0)
		{
			final var selectionKeys = readSelector.selectedKeys();

			for (final var selectionKey : selectionKeys)
			{
				readFromSocket(selectionKey);
			}

			selectionKeys.clear();
		}
	}

	private void readFromSocket(final SelectionKey selectionKey) throws IOException
	{
		final var socket = (Socket) selectionKey.attachment();
		socket.read(readByteBuffer);
	}

	private void writeToSockets() throws IOException
	{
		final var writeReady = writeSelector.selectNow();

		if (writeReady > 0)
		{
			final var selectionKeys = writeSelector.selectedKeys();
			for (final var selectionKey : selectionKeys)
			{
				writeToSocket(selectionKey);
			}

			selectionKeys.clear();
		}
	}

	protected void writeToSocket(final SelectionKey selectionKey) throws IOException
	{
		final var socket = (Socket) selectionKey.attachment();
		socket.write(writeByteBuffer);
	}
}
