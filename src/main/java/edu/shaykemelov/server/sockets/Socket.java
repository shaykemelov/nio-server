package edu.shaykemelov.server.sockets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Socket
{
	private final long socketId;
	private final SocketChannel socketChannel;
	private final SocketReader socketReader;
	private final SocketWriter socketWriter;

	private boolean endOfStreamReached;

	public Socket(final long socketId, final SocketChannel socketChannel, final SocketReader socketReader, final SocketWriter socketWriter)
	{
		this.socketId = socketId;
		this.socketChannel = socketChannel;
		this.socketReader = socketReader;
		this.socketWriter = socketWriter;
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

		socketReader.read(byteBuffer);

		return totalBytesRead;
	}

	public int write(ByteBuffer byteBuffer) throws IOException
	{
		socketWriter.write(byteBuffer);
		byteBuffer.flip();

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
