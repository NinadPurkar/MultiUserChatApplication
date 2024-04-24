package ChatServer.src.com.purkar;

import java.io.IOException;
import java.net.*;
import java.util.*;

import ChatServer.src.com.purkar.ServerWorker;

public class Server extends Thread{

	private final int serverport;
	
	private ArrayList<ServerWorker> workerList = new ArrayList<>();
	
	public Server(int serverport)
	{
		this.serverport = serverport;
	}
	
	public ArrayList<ServerWorker> getWorkerList()
	{
		return this.workerList;
	}
	
	public void removeWorker(ServerWorker serverWorker)
	{
		this.workerList.remove(serverWorker);
	}
	
	public void run()
	{
		try {
			ServerSocket serverSocket = new ServerSocket(serverport);
			while(true)
			{
				Socket clientSocket = serverSocket.accept();
				ServerWorker serverWorker = new ServerWorker(this,clientSocket);
				workerList.add(serverWorker);
				serverWorker.start();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
