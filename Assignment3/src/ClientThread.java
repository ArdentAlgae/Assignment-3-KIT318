import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

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
			
			// Print all input received from the socket.
			while(true)
			{
				System.out.println(dis.readUTF());
			}
		}
		catch(IOException e) {e.printStackTrace();}
	}

}