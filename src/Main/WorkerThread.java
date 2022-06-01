package Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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
	String username = "";
	
	String in = "";
	
	Worker w;

	OutputStream s1out;
	DataOutputStream dos;
	InputStream s1In;
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
			s1out = serverClient.getOutputStream();
			dos = new DataOutputStream(s1out);
			s1In = serverClient.getInputStream();
			dis = new DataInputStream(s1In);
			
			//Input Worker details
			w = new Worker();
			
			synchronized(Main.Server.workerList)
			{
				if (Server.workerList.isEmpty())
				{
					w.setWorkerID(1);
				}
			else
				{
				//w.setWorkerID(Server.workerList.getLast().getWorkerID()+1);
					for (int i = 1; i <= Server.workerList.size()+1; i++)
					{
						boolean idExists = false;
						for (Worker work : Server.workerList)
						{
							if (work.getWorkerID() == i)
							{
								idExists = true;
							}
						}
						if (!idExists)
						{
							w.setWorkerID(i);
							break;
						}
					}
				}
			}
			
			w.setCurrentThread(Thread.currentThread());
			
			synchronized(Main.Server.workerList)
			{
				Main.Server.workerList.add(w);
			}
			
			synchronized(Main.Server.timeIntList)
			{
				Main.Server.updateTimeList();
			}
			
			//Print to server for testing
			System.out.print("\nWorker ID: "+w.getWorkerID()+" Started...");
			
			processInput(); // Loop that will be used to process requests
			
			dos.close();
			s1out.close();
			dis.close();
			s1In.close();
			serverClient.close();
		} catch (Exception ex) {
			System.out.print("\nError: ");
			ex.printStackTrace();
			//endWorkerThread();
		}
	}


	public void processInput() throws UnknownHostException, IOException, InterruptedException {

		Boolean loop = true;
		while (loop) {
			//Get the Worker Health
			OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
			File disk = new File("C:");
			
			synchronized (Main.Server.workerList) 
			{
				w.setCpu(osBean.getProcessCpuLoad()/Main.Server.workerList.size());
				w.setStorage(disk.getTotalSpace()/Main.Server.workerList.size());
			}

			// If there are more than 2 workers and the conditions that led to the creation
			// of extra workers are no longer in place, and this worker thread.
			synchronized (Main.Server.requestList) {
					int workerListSize = Main.Server.workerList.size();
					if (Main.Server.returnAllRequestsToProcess(Main.Server.requestList).size() < 10) {
						if (workerListSize > 2) {
							if (w.getWorkerID() == workerListSize) {
								// endWorkerThread();
								loop = false;
							}
						}

					}
				}

			

			// TODO Job prioritisation
			synchronized (Main.Server.requestList) {
				if (!Main.Server.requestList.isEmpty()) {
					if (!Main.Server.returnAllRequestsToProcess(Main.Server.requestList).isEmpty()) {
						LinkedList<Request> requestProcess = Main.Server.returnAllRequestsToProcess(Main.Server.requestList);
						LinkedList<Request> requestProcessed = new LinkedList<Request>();
						
						if (!Server.returnUrgentRequests(requestProcess).isEmpty())
						{
							requestProcessed = Server.returnUrgentRequests(requestProcess);
						}
						else
						{
							requestProcessed = Server.returnNonUrgentRequests(requestProcess);
						}
						
						Request r = Main.Server.returnAllRequestsToProcess(requestProcessed).getFirst();

						if (r.getType().equals(Request.Type.STRING) && r.getStatus().equals(Request.Status.INACTIVE)) {
							System.out.print("\nWorker ID: " + w.getWorkerID() + " is processing Request ID: "
									+ r.getRequestID());
							w.setProcessingRequestID(r.getRequestID());
							r.setProfanityLevel(Request.Profanity.NONE);
							r.setStatus(Request.Status.PROCESSING);

							// Uncomment to test worker failure handling
							if (w.getWorkerID() == 1 && Main.Server.hasWorkerFailed == false) {
								//Main.Server.hasWorkerFailed = true;
								//w.currentThread.stop();
							}

							String output = ProfanityManager.ManageString(r.getStringContent(), r);
							r.setOutput(output);
							r.setEndTime(LocalDateTime.now());
							r.setStatus(Request.Status.COMPLETED);
							w.setProcessingRequestID(null);

						}

						if (r.getType().equals(Request.Type.FILE) && r.getStatus().equals(Request.Status.INACTIVE)) {
							System.out.print("\nWorker ID: " + w.getWorkerID() + " is processing Request ID: "
									+ r.getRequestID());
							w.setProcessingRequestID(r.getRequestID());

							// Uncomment to test worker failure handling
							if (w.getWorkerID() == 1 && Main.Server.hasWorkerFailed == false) {
								Main.Server.hasWorkerFailed = true;
								// w.currentThread.stop();
							}

							// TODO need to sort out file filtering
							r.setStatus(Request.Status.PROCESSING);
							// ProfanityManager.ManageFiles(r.getInputFilePath(), r.getOutputFilePath(), r);
							ProfanityManager.ManageSingleFile(r.getInputFileName(), r.getOutputFilePath(), r);
							r.setEndTime(LocalDateTime.now());
							r.setStatus(Request.Status.COMPLETED);
							w.setProcessingRequestID(null);
						}
					}

				}
			}

		}
		System.out.print("\nWorker id: "+w.getWorkerID()+" has finished...");
		synchronized(Main.Server.workerList)
		{
			Server.workerList.remove(w);
		}
	}

}
