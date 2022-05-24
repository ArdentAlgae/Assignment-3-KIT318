package Main;

import java.net.*;
import java.io.*;
import java.util.*;

public class TimeServer {

	public static LinkedList<Request> urgentRequest = new LinkedList<Request>();
	public static LinkedList<Request> nonUrgentRequest = new LinkedList<Request>();
	public static LinkedList<User> userList = new LinkedList<User>();
	
	public static HashMap<Integer, Boolean> connectedSocket = new HashMap<Integer, Boolean>();
	
	public static LinkedList<Worker> workerList = new LinkedList<Worker>();

	public static HashMap<Integer, Boolean> returnHashMap() {
		return connectedSocket;
	}
	
	
	
	public static LinkedList<Request> returnAllRequests()
	{
		LinkedList<Request> allRequest = (LinkedList<Request>) urgentRequest.clone();
		
		for (Request r : nonUrgentRequest)
		{
			allRequest.add(r);
		}
		
		return allRequest;
	}
	
	
	public static void deleteUrgentRequest(Request r)
	{
		LinkedList<Request> filteredRequest = new LinkedList<Request>();
		LinkedList<Request> request = (LinkedList<Request>) urgentRequest.clone();
		
		for (Request req : request)
		{
			if (req.getRequestID() != r.getRequestID())
			{
				filteredRequest.add(req);
			}
		}
		
		urgentRequest =  filteredRequest;
	}
	
	public static void deleteNonUrgentRequest(Request r)
	{
		LinkedList<Request> filteredRequest = new LinkedList<Request>();
		LinkedList<Request> request = (LinkedList<Request>) nonUrgentRequest.clone();
		
		for (Request req : request)
		{
			if (req.getRequestID() != r.getRequestID())
			{
				filteredRequest.add(req);
			}
		}
		
		nonUrgentRequest =  filteredRequest;
	}
	
	
	
	
	

	public static void main(String[] args) throws Exception {
		
		
		
		try {
			// Initialise server, and an arrayList of serverThreads, which will allow the
			// threads to communicate with each other.
			ServerSocket server = new ServerSocket(1254);
			System.out.print("Server started");
			
			//Start the thread for counting the number of workers
			Socket workerSocket = new Socket("127.0.0.1",1254);
			Socket workerCount = server.accept();
			WorkerCountThread wct = new WorkerCountThread(workerCount); // send the request to a separate thread
			wct.start(); // Start the server thread that was added to the arrayList.
			
			String caseSocket = "";
			
			
			// This loop will wait for connections, and accept them whenever a client
			// connects.
			while (true) {
				//boolean added = false; // Whether a server thread has been added to the arrayList
				Socket openSocket = server.accept(); // server accept the client connection request
				
				InputStream s1In = openSocket.getInputStream();
				DataInputStream dis = new DataInputStream(s1In);
				
				caseSocket = dis.readUTF();
				//s1In.close();
				//dis.close();
				
				if (caseSocket.equalsIgnoreCase("WORKER"))
				{
					WorkerThread wct2 = new WorkerThread(openSocket); // send the request to a separate thread
					wct2.start(); // Start the server thread that was added to the arrayList.
				}
				else
				{
					ServerThread sct = new ServerThread(openSocket); // send the request to a separate thread
					sct.start(); // Start the server thread that was added to the arrayList.
				}
				
				
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
