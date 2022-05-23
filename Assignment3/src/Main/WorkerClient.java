package Main;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class WorkerClient {
	public static void main(String args[]) throws IOException {
		
		//Open your connection to a server, at port 1254
		//Socket workerSocket = new Socket("127.0.0.1",1254);
		//Scanner input = new Scanner(System.in);
		//String sending = "";
		
		

		// Instantiate and start a thread which will read and output all input from the server.
		//WorkerThread ct=new WorkerThread(workerSocket);
		//ct.start();
		  
		//Get an output file handle from the socket and read the input
		//OutputStream s1Out = workerSocket.getOutputStream();
		//DataOutputStream dos = new DataOutputStream(s1Out);
		
		//while(true)
		{
			//if (Main.TimeServer.returnAllRequests().size() > 2 && Main.TimeServer.numberWorkers < 3)
			{
				//WorkerClient.main(args);
			}
			//sending = new String(input.nextLine());
			//dos.writeUTF(sending);
		}
		
		//When done, just close the connection and exit
		//dos.close();
		//s1Out.close();
		//clientSocket.close();
	}

}
