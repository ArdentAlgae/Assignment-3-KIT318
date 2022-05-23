package Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import Main.Request.Type;

public class WorkerThread extends Thread {
	
	Socket serverClient;
	int threadNum; // The thread's index in the arrayList
	int requested = -1; // The index of the last user this user requested to chat with, used to tell
						// whether a searched for name has been found.
						// the -1 indicates that it applies to no existing thread.
	int connected = -1; // The index of the user this user is connected to.
	int pending = -1; // The index of the most recent user to request a chat.

	String username = "";
	
	String in = "";

	DataOutputStream dos;
	DataInputStream dis;

	WorkerThread(Socket inSocket) {
		serverClient = inSocket;

		// serverList = inServerList;
	}

	public void run() {
		try {

			// Set up input and output. dis and dos are class variables so they can be
			// accesed by all methods, and even
			// other threads, to send messages to this thread's user.
			OutputStream s1out = serverClient.getOutputStream();
			dos = new DataOutputStream(s1out);
			InputStream s1In = serverClient.getInputStream();
			dis = new DataInputStream(s1In);
			
			//Input Worker details
			Worker w = new Worker();
			w.setWorkerID(Main.TimeServer.workerList.size()+1);
			Main.TimeServer.workerList.add(w);
			
			//Print to server for testing
			System.out.print("\nWorker ID: "+w.getWorkerID()+" Started...");
			
			
			processInput(); // Loop that will be used to process requests
			
			dos.close();
			s1out.close();
			dis.close();
			s1In.close();
			serverClient.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}


	public void processInput() throws UnknownHostException, IOException, InterruptedException {
		
		
		while (true)
		{
			
			
		}
		
	}

	
}
