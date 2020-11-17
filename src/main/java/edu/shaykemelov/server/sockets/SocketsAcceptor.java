package edu.shaykemelov.server.sockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.Queue;

import edu.shaykemelov.protocols.http.HttpReader;
import edu.shaykemelov.protocols.http.HttpWriter;

public class SocketsAcceptor implements Runnable
{
	private final int port;
	private final Queue<Socket> socketsQueue;

	private volatile boolean running;

	private long nextSocketId = 0;

	public SocketsAcceptor(final int port, final Queue<Socket> socketsQueue)
	{
		this.port = port;
		this.socketsQueue = socketsQueue;

		this.running = false;
	}

	@Override
	public void run()
	{
		try (var serverSocketChannel = ServerSocketChannel.open())
		{
			serverSocketChannel.bind(new InetSocketAddress(port));

			this.running = true;

			while (running)
			{
				try
				{
					final var socketChannel = serverSocketChannel.accept();
					final var socket = new Socket(nextSocketId++, socketChannel, new HttpReader(), new HttpWriter());
					socketsQueue.add(socket);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void stop()
	{
		running = false;
	}
}
