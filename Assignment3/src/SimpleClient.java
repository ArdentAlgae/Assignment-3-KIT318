import java.util.*;
import java.net.*;
import java.io.*;
public class SimpleClient {
	public static void main(String args[]) throws IOException {
		//Open your connection to a server, at port 1254
		Socket clientSocket = new Socket("127.0.0.1",1254);
		Scanner input = new Scanner(System.in);
		String sending = "";
		
		// Instantiate and start a thread which will read and output all input from the server.
		ClientThread ct=new ClientThread(clientSocket);
		ct.start();
		  
		//Get an output file handle from the socket and read the input
		OutputStream s1Out = clientSocket.getOutputStream();
		DataOutputStream dos = new DataOutputStream(s1Out);
		
		// Until an error occurs, wait for the user to input text and then send that text to the server.
		try
		{
			while(true)
			{
				sending = new String(input.nextLine());
				dos.writeUTF(sending);
			}
		}
		catch(IOException e) {e.printStackTrace();}
		
		//When done, just close the connection and exit
		dos.close();
		s1Out.close();
		clientSocket.close();
	}
}
