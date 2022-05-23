package Main;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

public class ClientThread extends Thread{
	Socket clientSocket;
	public ClientThread(Socket clientSocket)
	{
		this.clientSocket=clientSocket;
	}
	
	public void run()
	{
		
		try {
			//Get an output file handle from the socket and read the input
			InputStream s1In = clientSocket.getInputStream();
			DataInputStream dis = new DataInputStream(s1In);
			
			while(true)
				{
				try {System.out.println(dis.readUTF());}
				catch(SocketException | EOFException e) 
					{   
						System.out.print("Exiting...");
						System.exit(0);
					}
					
				}
			
		}
		catch(IOException e) {e.printStackTrace();}
	}

}


