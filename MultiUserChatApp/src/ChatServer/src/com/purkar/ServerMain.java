package ChatServer.src.com.purkar;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.*;


public class ServerMain
{

	public static void main(String gg[])
	{
		Server server =  new Server(8080);
		server.start();
		
	}
}