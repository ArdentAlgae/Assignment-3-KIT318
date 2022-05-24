package Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
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
			
			
			processInput(w); // Loop that will be used to process requests
			
			dos.close();
			s1out.close();
			dis.close();
			s1In.close();
			serverClient.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}


	public void processInput(Worker w) throws UnknownHostException, IOException, InterruptedException {
		
		while (true)
		{
			
			Thread.sleep(1000);
			
			//TODO need to query the worker cpu, storage, health etc...
			OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
			w.setCpu(osBean.getProcessCpuLoad());
			
			
			
			if (!Main.TimeServer.returnAllRequests().isEmpty())
			{
				  for (Request r : Main.TimeServer.returnAllRequests()) 
				  {
					  if (r.getType().equals(Request.Type.STRING) && r.getStatus().equals(Request.Status.INACTIVE))
					  	{
						  r.setStatus(Request.Status.PROCESSING);
						  String output = ProfanityManager.ManageString(r.getStringContent(), r);
						  r.setOutput(output);
						  r.setEndTime(LocalDateTime.now()); r.setStatus(Request.Status.COMPLETED);
					  	}
					  
					  if (r.getType().equals(Request.Type.FILE) && r.getStatus().equals(Request.Status.INACTIVE))
					  {
						  //TODO need to sort out file filtering
						  r.setStatus(Request.Status.PROCESSING);
						  //ProfanityManager.ManageFiles(r.getInputFilePath(), r.getOutputFilePath(), r);
						  ProfanityManager.ManageSingleFile(r.getInputFileName(), r.getOutputFilePath(), r);
						  r.setEndTime(LocalDateTime.now()); r.setStatus(Request.Status.COMPLETED);
					  }
				  }
			}
			
			  
			
//			  System.out.print("LOOPING"); if (r.getType().equals(Request.Type.STRING) &&
//			  r.getStatus().equals(Request.Status.INACTIVE)) {
//			  r.setStatus(Request.Status.PROCESSING);
//			  r.setOutput(ProfanityManager.ManageString(r.getStringContent()));
//			  r.setEndTime(LocalDateTime.now()); r.setStatus(Request.Status.COMPLETED); } }
//			 

		}
		
	}

	
}
