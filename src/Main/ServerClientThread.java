package Main;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import Main.Request.Type;

class ServerThread extends Thread {
	Socket serverClient;

	String username = "";
	
	String in = "";

	DataOutputStream dos;
	DataInputStream dis;

	ServerThread(Socket inSocket) {serverClient = inSocket;}

	public void run() {
		try {

			// Set up input and output. dis and dos are class variables so they can be
			// accesed by all methods, and even
			// other threads, to send messages to this thread's user.
			OutputStream s1out = serverClient.getOutputStream();
			dos = new DataOutputStream(s1out);
			InputStream s1In = serverClient.getInputStream();
			dis = new DataInputStream(s1In);
			
			//authenticate(); // Get a name and description from the user, make sure they are allowed.

			if (!in.equalsIgnoreCase("Exit"))
			{
				processInput(); // Loop that processes the user's input until exited, or an error occurs
			}
			

			dos.close();
			s1out.close();
			dis.close();
			s1In.close();
			serverClient.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	public void authenticate() {
		try {
			
			boolean signedIn = false;
			dos.writeUTF("If you do not have an account, please input \"Register\".\n"
					+ "If you do have an account, please input \"SignIn\".\n"
					+ "You may change your mind at any time by entering one of the above commands.");
			in = dis.readUTF();
			while (!signedIn) {
				if (in.equalsIgnoreCase("Register")) {
					// Have the user input a name, make them input a new one if it is taken by
					// another user or contains profanities.
					dos.writeUTF("Please enter a username: ");
					in = dis.readUTF();
					boolean taken = nameTaken(in);
					while (taken && !in.equals("SignIn")) {
						dos.writeUTF("There is already a user with this username. Please choose another.");
						in = dis.readUTF();
						taken = nameTaken(in);
					}
					username = in;

					if (!in.equalsIgnoreCase("SignIn")) {
						String password = newPassword();
						User newUser = new User();
						newUser.setPassword(password);
						newUser.setUsername(username);

						Server.userList.add(newUser);
						// Let the user know they are connected, and what commands can be used.
						dos.writeUTF("Thank you, you are now connected. You have been assigned the password: " + password);
						signedIn = true;
					}
				} else if (in.equalsIgnoreCase("SignIn")) {
					dos.writeUTF("Please enter your username: ");
					in = dis.readUTF();
					boolean taken = nameTaken(in);
					while (!taken && !in.equals("NewAccount")) {
						dos.writeUTF("There no account with this username. Please choose another.");
						in = dis.readUTF();
						taken = nameTaken(in);
					}
					username = in;

					if (!in.equalsIgnoreCase("Register")) {
						dos.writeUTF("Please enter your password.");
						in = dis.readUTF();

						for (User i : Server.userList) {
							if (i.getUsername().equals(username)) {
								if (i.getPassword().equals(in)) 
								{
									i.setSocket(serverClient);
									dos.writeUTF("Thankyou, you are now connected.");
									signedIn = true;
								} else 
								{
									dos.writeUTF("This password is incorrect. Please try again.");
									in = dis.readUTF();
								}
							}
						}

					}
				} else {
					dos.writeUTF("This is not a valid command. Please enter either \"Register\" or \"SignIn\".");
					in = dis.readUTF();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void processInput() {
		try {
			// Take the first input and begin the loop, which will end if the input is ever
			// "Exit"
			String in = "";
			String in2 = "";
			
			while (!in.equalsIgnoreCase("Exit")) {
				dos.writeUTF("Commands are 'Request' (make a request), 'Status' (get status of request), 'Stop' (delete a request), 'List' (Show all requests) ");
				in = dis.readUTF();

				if (in.equalsIgnoreCase("Request")) 
				{
					dos.writeUTF("Filter a String or File?");
					in2 = dis.readUTF();

					if (in2.equalsIgnoreCase("String")) {
						synchronized(Main.Server.requestList)
						{
							Request request = new Request();
							request.setUserName(username);
							
							request.setType(Type.STRING);
							
							request.setStatus(Request.Status.INACTIVE);
							request.setStartTime(LocalDateTime.now());
							
							dos.writeUTF("Generated Request ID: " + incrementRequestID());
							request.setRequestID(incrementRequestID());

							dos.writeUTF("Enter the string content");
							in2 = dis.readUTF();
							request.setStringContent(in2);

							dos.writeUTF("Enter the deadline (O for urgent, 1 for non urgent)");
							in2 = dis.readUTF();
							request.setDeadline(in2);

							// Placeholder
							if (in2.equals("0")) 
							{
								request.setUrgency(Request.Urgency.URGENT);
							}
							
							if (in2.equals("1")) {
								request.setUrgency(Request.Urgency.NONURGENT);
							}
							Main.Server.requestList.add(request);
							dos.writeUTF("Submitted Request");
						}
						
					}
					if (in2.equalsIgnoreCase("File")) {
						
						dos.writeUTF("Generated Request ID: " + incrementRequestID());
						
						dos.writeUTF("Enter the filepath");
						in = dis.readUTF();
						//request.setInputFilePath(in);
						
						dos.writeUTF("Enter the deadline (O for urgent, 1 for non urgent)");
						in2 = dis.readUTF();
						
						Integer requestID = incrementRequestID();
						
						File dir = new File("files/input");
						for (File file: dir.listFiles())
						{
							synchronized(Main.Server.requestList)
							{
								Request request = new Request();
								request.setUserName(username);
								
								request.setType(Type.FILE);
								request.setStatus(Request.Status.INACTIVE);
								request.setStartTime(LocalDateTime.now());
								request.setRequestID(requestID);
								request.setInputFileName(file.getName());
								request.setInputFilePath("files/input");
								request.setOutputFilePath("files/output");
								
								if (in2.equals("0")) {
									request.setUrgency(Request.Urgency.URGENT);
								
								}
								if (in2.equals("1")) {
									request.setUrgency(Request.Urgency.NONURGENT);
									
								}
								Main.Server.requestList.add(request);
							}
						}
						dos.writeUTF("Submitted Request");
					}

				}
				
				if (in.equalsIgnoreCase("Status"))
				{
					dos.writeUTF("Enter the request ID to get the status");
					in2 = dis.readUTF();
					//Boolean requestExists = false;
					
					DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); 
					
					if (authorisedRequest(in2, username))
					{
						Request r = mergeRequests(in2, username);
						
						if (r.getStatus().equals(Request.Status.COMPLETED))
						{
							long timeTaken = ChronoUnit.MILLIS.between(r.getStartTime(), r.getEndTime());
							double bill = 100*timeTaken;
							//synchronized (Main.Server.requestList)
							{
								//bill = r.generateBill();
							}
							
							String cost = String.format("%.2f",bill);
							
							dos.writeUTF("Request ID: "+r.getRequestID()+", Status: "+r.getStatus()+
									", Date started: "+date.format(r.getStartTime())+
									", Date ended: "+date.format(r.getEndTime())+
									", Time taken (ms): "+timeTaken+
									", Bill: $"+cost+
									", Profanity Level: $"+r.getProfanityLevel()+
									", Output: "+r.getOutput()
									);
						}
						else
						{
							dos.writeUTF("Request ID: "+r.getRequestID()+", Status: "+r.getStatus()+", Progress: "+r.getProgress()+", Date started: "+date.format(r.getStartTime()));
						}
					}
						
				}
				
				//Still need to stop requests once they have been started by a worker!!!!
				if (in.equalsIgnoreCase("Stop"))
				{
					dos.writeUTF("Enter the request ID to stop"); 
					in2 = dis.readUTF();
					
					for (Request r : Main.Server.requestList)
					{
						if (r.getRequestID().toString().equals(in2))
						{
							//Deletes request from list
//							if (r.getStatus().equals(Request.Status.INACTIVE))
//							{
//								Main.TimeServer.deleteUrgentRequest(r);
//								Main.TimeServer.deleteNonUrgentRequest(r);
//								dos.writeUTF("Request ID: "+r.getRequestID()+" has been stopped");
//							}
							if (!r.getStatus().equals(Request.Status.COMPLETED))
							{
								r.setStatus(Request.Status.CANCELLED);
							}
						}
					}
					
				}
				
				//Delete later. For testing purposes
				if (in.equalsIgnoreCase("List"))
				{
					DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
					
					dos.writeUTF("List all requests");
					for (Request r : Main.Server.requestList)
					{
						if (r.getType().equals(Request.Type.STRING))
						{
							dos.writeUTF("Request ID: "+r.getRequestID()+", Status: "+r.getStatus()+", Progress: "+r.getProgress()+"%"+", Date started: "+date.format(r.getStartTime()));
						}
						
						if (r.getType().equals(Request.Type.FILE))
						{
							dos.writeUTF("Request ID: "+r.getRequestID()+", Status: "+r.getStatus()+", File "+r.getCurrentFile()+"/"+r.getNumFiles()+", Date started: "+date.format(r.getStartTime()));
						}
						
					}
					
				}
				
				//Delete later. For testing purposes
				//This lists all the worker threads
				if (in.equalsIgnoreCase("Workers"))
				{
					dos.writeUTF("List all worker threads");
					dos.writeUTF("Worker count: "+Main.Server.workerList.size());
					for (Worker w : Main.Server.workerList)
					{
						dos.writeUTF("Worker ID: "+w.getWorkerID() +", Health: cpu("+w.getCpu()+"),"+"  storage("+w.getStorage()+")");
					}
					
				}
				
				
				//Delete later. For testing purposes
				//Use to input a bunch of requests
				if (in.equalsIgnoreCase("test"))
				{
					for (int i = 0; i < 30; i++)
					{
						synchronized(Main.Server.requestList)
						{
						Request request = new Request();
						request.setUserName(username);
						
						request.setType(Type.STRING);
						
						request.setStatus(Request.Status.INACTIVE);
						request.setStartTime(LocalDateTime.now());
						
						dos.writeUTF("Generated Request ID: " + incrementRequestID());
						request.setRequestID(incrementRequestID());
						request.setStringContent("input :"+i+" badwords  testing testing testing badwords");
						request.setDeadline("0");
						request.setUrgency(Request.Urgency.URGENT);
						
						Main.Server.requestList.add(request);
						}
					}
				}
				
				
				//in = dis.readUTF(); // Wait for a new input before repeating the loop.
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Loops through all other threads in the arrayList, and returns true if this
	// thread's name is the name of
	// any other thread.
	public boolean nameTaken(String name) {
		for (User i : Server.userList) {
			if (name.equals(i.getUsername())) {
				return true;
			}
		}
		return false;
	}
	
	public String newPassword() {
		Random rnd = new Random();
		  String password = "";
		  for(int i = 0; i < 10; i++)
		  {
			  password = password + (char) (33 + rnd.nextInt(93));
		  }
		  return password;
	}
	
	public Integer incrementRequestID()
	{
		Integer inc = Server.requestList.size();
		
		return (inc+1);
	}
	
	public Boolean authorisedRequest(String requestID, String username) throws IOException
	{
		Boolean requestExists = false;
		Boolean userAuthorised = false;
		for(Request r: Server.requestList)
		{
			if (r.getRequestID().toString().equals(requestID))
			{
				requestExists = true;
				if (r.getUserName().equals(username))
				{
					userAuthorised = true;
				}
			}
		}
		
		if (!requestExists)
		{
			dos.writeUTF("No such request ID");
			return false;
		}
		else
		{
			if(!userAuthorised)
			{
				dos.writeUTF("Not authorized to view this request");
				return false;
			}
			else
			{
				return true;
			}
		}
		
		
	}
	
	public Request mergeRequests(String requestID, String username) throws IOException
	{
		Integer totalFiles = 0;
		Integer filesCompleted = 0;
		
		Request req = new Request();
		req.setUserName(username);
		req.setType(Type.FILE);
		req.setRequestID( Integer.parseInt(requestID));
		req.setStatus(Request.Status.INACTIVE);
		req.setStartTime(LocalDateTime.now());
		
		for(Request r: Server.requestList)
		{
			if (r.getUserName().equals(username))
			{
				if (r.getRequestID().toString().equals(requestID))
				{
					totalFiles++;
					req.setNumFiles(totalFiles);
					
					//If String
					if (r.getType().equals(Request.Type.STRING))
					{
						req.setOutput(r.getOutput());
					}
					else
					{
						req.setOutput(r.getOutputFilePath());
					}
					
					
					if (r.getStatus().equals(Request.Status.COMPLETED))
					{
						filesCompleted++;
						req.setCurrentFile(filesCompleted);
						
						req.setStatus(Request.Status.COMPLETED);
						req.setEndTime(r.getEndTime());
					}
					else if (r.getStatus().equals(Request.Status.PROCESSING))
					{
						req.setStatus(Request.Status.PROCESSING);
					}
					
					if (req.getStartTime().isAfter(r.getStartTime()))
					{
						req.setStartTime(r.getStartTime());
					}
				}
					
			}

		}
		return req;
		
	}
}
