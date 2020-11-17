package edu.shaykemelov.server.sockets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Socket
{
	private final long socketId;
	private final SocketChannel socketChannel;
	private final SocketsReader socketsReader;
	private final SocketsWriter socketsWriter;

	private boolean endOfStreamReached;

	public Socket(final long socketId, final SocketChannel socketChannel, final SocketsReader socketsReader, final SocketsWriter socketsWriter)
	{
		this.socketId = socketId;
		this.socketChannel = socketChannel;
		this.socketsReader = socketsReader;
		this.socketsWriter = socketsWriter;
		this.endOfStreamReached = false;
	}

	public int read(ByteBuffer byteBuffer) throws IOException
	{
		int bytesRead = socketChannel.read(byteBuffer);
		int totalBytesRead = bytesRead;

		while (bytesRead > 0)
		{
			bytesRead = socketChannel.read(byteBuffer);
			totalBytesRead += bytesRead;
		}

		if (bytesRead == -1)
		{
			endOfStreamReached = true;
		}

		socketsReader.read(byteBuffer);

		return totalBytesRead;
	}

	public int write(ByteBuffer byteBuffer) throws IOException
	{
		int bytesWritten = socketChannel.write(byteBuffer);
		int totalBytesWritten = bytesWritten;

		while (bytesWritten > 0 && byteBuffer.hasRemaining())
		{
			bytesWritten = socketChannel.write(byteBuffer);
			totalBytesWritten += bytesWritten;
		}

		return totalBytesWritten;
	}

	public SocketChannel getSocketChannel()
	{
		return socketChannel;
	}

	public long getSocketId()
	{
		return socketId;
	}
}
