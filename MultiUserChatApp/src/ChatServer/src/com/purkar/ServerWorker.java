package ChatServer.src.com.purkar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.lang.*;

/**
 * @author ASUS
 *
 */
public class ServerWorker extends Thread{
	
	private final Socket clientSocket;
	private final Server server;
	private OutputStream outputStream;
	private String login = null;
	private HashSet<String> topicSet = new HashSet<>();
	
	public ServerWorker(Server server, Socket clientSocket)
	{
		this.clientSocket = clientSocket;
		this.server = server;
	}
	
	public void run()
	{
		
		try {
			handleClientSocket();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void handleClientSocket() throws InterruptedException, IOException
	{
			InputStream inputStream = clientSocket.getInputStream();
			this.outputStream = clientSocket.getOutputStream();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			
			String line;
			while((line=reader.readLine()) != null)
			{
				
				String [] tokens = line.split("\\s");
				String cmd = tokens[0];
				if("login".equalsIgnoreCase(cmd))
				{
					handleLogin(outputStream, tokens);
				}
				else if("logoff".equals(cmd)||"quit".equalsIgnoreCase(cmd))
				{
					handleLogoff();
					break;
				}
				else if("msg".equalsIgnoreCase(cmd))
				{
					handleMsg(tokens);
				}
				else if ("join".equals(cmd))
				{
					handleJoin(tokens);
				}
				else if ("leave".equals(cmd))
				{
					handleLeave(tokens);
				}
				
				String msg =  "You typed: "+line;
				outputStream.write(msg.getBytes());
			}
			
			clientSocket.close();
			
	}
	
	private void handleLeave(String [] tokens)
	{
		if (tokens.length > 1)
		{
			String topic = tokens[1];
			topicSet.remove(topic);
		}
		
	}
	
	public boolean isMemberOfTopic(String topic)
	{
		return topicSet.contains(topic);
	}
	
	// format is join topic 
	private void handleJoin(String [] tokens)
	{
		if (tokens.length > 1)
		{
			String topic = tokens[1];
			topicSet.add(topic);
		}
		
	}
	
	
	// format is msg "login" text
	private void handleMsg(String [] tokens) throws IOException
	{
		String sendTo = tokens[1];
		String body = tokens[2];
		
		boolean isTopic = sendTo.charAt(0) == '#';
		
		List<ServerWorker> workerList = server.getWorkerList();
		for(ServerWorker worker:workerList)
		{
			if (isTopic)
			{
				if(worker.isMemberOfTopic(sendTo))
				{
					String OutMsg = "msg " +"#"+sendTo+ login + " " + body + "\n";
					worker.send(OutMsg);
				}
			}
			else if(sendTo.equals(worker.getLogin()))
			{
				String OutMsg = "msg " + login + " " + body + "\n";
				worker.send(OutMsg);
			}
		}
		
	}

	private void handleLogoff() throws IOException 
	{
		server.removeWorker(this);
		List <ServerWorker> workerList = server.getWorkerList();
		for(ServerWorker sw: workerList)
		{
			if(!login.equals(sw.getLogin()) && sw.getLogin() != null)
			{
				String onlineMsg = "'Offline"+login+"\n";
				sw.send(onlineMsg);
			}
		}
		
	}

	private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
		if(tokens.length ==3)
		{
			String user_name = tokens[1];
			String password = tokens[2];
			
			if ("guest".equals(user_name) && "guest".equals(password) ||
					"temp".equals(user_name) && "temp".equals(password))
			{
				String msg = "ok Login";
				this.login = user_name;
				outputStream.write(msg.getBytes());
				List <ServerWorker> workerList = server.getWorkerList();
				
				//send current user all other users online status:
				for(ServerWorker sw: workerList)
				{
					if(!login.equals(sw.getLogin()) && sw.getLogin() != null)
					{
						String onlineMsg = "online"+sw.getLogin()+"\n";
						sw.send(onlineMsg);
					}
				}
				
				// send other users current user status
				String Msg2 = "online"+user_name+"\n";
				for(ServerWorker sw: workerList)
				{
					if (!login.equals(sw.getLogin()) )
					{
						sw.send(Msg2);
					}
				}
			}
			else
			{
				String msg = "error login";
				outputStream.write(msg.getBytes());
			}
		}
		// TODO Auto-generated method stub
		
	}

	private void send(String onlineMsg) throws IOException {
		if (login != null)
		{
			outputStream.write(onlineMsg.getBytes());
		}
		
	}
	
	private String getLogin()
	{
		return this.login;
	}

}
