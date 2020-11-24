package edu.shaykemelov.server.sockets;

import java.nio.ByteBuffer;

public interface SocketReader
{
	void read(ByteBuffer byteBuffer);
}
