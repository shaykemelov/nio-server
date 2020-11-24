package edu.shaykemelov.server.sockets;

import java.nio.ByteBuffer;

public interface SocketWriter
{
	void write(ByteBuffer byteBuffer);
}
