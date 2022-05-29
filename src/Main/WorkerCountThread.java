package Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class WorkerCountThread extends Thread {
	
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

	WorkerCountThread(Socket inSocket) {
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
			
			
			//Checks the list of requests and number of workers and adds workers as necessary
			while (true)
			{
				//Create new worker
				Thread.sleep(10);
				
				//Currently limits number of workers to 2
				int numRequests = Main.TimeServer.returnAllRequestsToProcess().size(), numWorkers = Main.TimeServer.workerList.size();
				if ((numRequests > 1 && numWorkers < 2) || numWorkers == 0 || numRequests > 3 * numWorkers || numWorkers < 1 ) //For more than 2 workers to be created, there must be more than 3 queued requests per worker.
				{
					Socket workerSocket = new Socket("127.0.0.1",1254);
					DataOutputStream dos2;
					OutputStream s2out = workerSocket.getOutputStream();
					dos2 = new DataOutputStream(s2out);
					dos2.writeUTF("WORKER");
				}
				
				
				LinkedList<Worker> workerThreads = (LinkedList<Worker>) Main.TimeServer.workerList.clone();
				for (Worker wt : workerThreads)
				{
					if (wt.getCurrentThread().getState() == Thread.State.TERMINATED)
					{
						System.out.print("\nWorker ID: " +wt.getWorkerID()+" has failed");
						//Thread has been terminated
						TimeServer.workerList.remove(wt);
						for (Request r : TimeServer.returnAllRequests())
						{
							if (r.getRequestID() == wt.getProcessingRequestID() && !r.getStatus().equals(Request.Status.COMPLETED) )
							{
								r.setStatus(Request.Status.INACTIVE);
							}
						}
					}
					
				}
			}
			
			//dos.close();
			//s1out.close();
			//dis.close();
			//s1In.close();
			//serverClient.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

}
