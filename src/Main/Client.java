package Main;
import java.util.*;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.net.*;
import java.io.*;

public class Client {
	
	
	/**
	 * Main method for the Client
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		//Open your connection to a server, at port 1254
		try 
		{
			Socket clientSocket = new Socket("127.0.0.1"/*"131.217.172.91"*/,1254); // The first address is to test code locally, the second connects to the nectar cloud.
			//Socket clientSocket = new Socket("131.217.172.91",1254); 
				
			Scanner input = new Scanner(System.in);
			String sending = "";

			// Get an output file handle from the socket and read the input
			OutputStream s1Out = clientSocket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(s1Out);
			dos.writeUTF("CLIENT");

			// Instantiate and start a thread which will read and output all input from the
			// server.
			ClientThread ct = new ClientThread(clientSocket);
			ct.start();

			// Until an error occurs, wait for the user to input text and then send that
			// text to the server.
			try {
				while (true) {
					sending = new String(input.nextLine());
					dos.writeUTF(sending);

					if (sending.equals("File")) {
						// uploadfile();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			// When done, just close the connection and exit
			dos.close();
			s1Out.close();
			clientSocket.close();

		}
		catch(Exception e)
		{
			System.out.print("Cannot connect to server \nExiting...");
		}
	}
}

