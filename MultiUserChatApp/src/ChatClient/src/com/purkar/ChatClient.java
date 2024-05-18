package ChatClient.src.com.purkar;

import java.util.*;
import java.net.Socket;
import java.io.*;

public class ChatClient {
	private int port;
	private String server;
	private Socket socket;
	private InputStream serverinput;
	private OutputStream serveroutput;
	private BufferedReader bufferedReader;
	
	
	public ChatClient(String server , int port)
	{
		this.server = server;
		this.port = port;
	}
	public static void main(String gg[])
	{
		ChatClient chatclient = new ChatClient("localhost", 8080);
		if (chatclient.connect())
		{
			System.out.println("connect established");
		}
		else 
		{
			System.out.println("Connection failed");
		}
		
	}
	
	public boolean connect()
	{
		try
		{
			this.socket = new Socket(server, port);
			this.serverinput = socket.getInputStream();
			this.serveroutput = socket.getOutputStream();
			this.bufferedReader = new BufferedReader(new InputStreamReader(serverinput));
			return true; 
		}
		catch(Exception e)
		{
			return false;
		}
	}

}
