package Main;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import Main.Request.Type;

class ServerThread extends Thread {
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

	ServerThread(Socket inSocket) {
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
			
			authenticate(); // Get a name and description from the user, make sure they are allowed.

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

						TimeServer.userList.add(newUser);
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

						for (User i : TimeServer.userList) {
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
					Request request = new Request();
					request.setUserName(username);

					dos.writeUTF("Filter a String or File?");
					in2 = dis.readUTF();

					if (in2.equalsIgnoreCase("String")) {
						request.setType(Type.STRING);
						
						request.setStatus("Inactive");
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
							Main.TimeServer.urgentRequest.add(request);
						}
						
						if (in2.equals("1")) {
							Main.TimeServer.nonUrgentRequest.add(request);
						}
						dos.writeUTF("Submitted Request");
					}
					if (in2.equalsIgnoreCase("File")) {
						request.setType(Type.FILE);
						
						request.setStatus("Inactive");
						request.setStartTime(LocalDateTime.now());
						
						dos.writeUTF("Generated Request ID: " + incrementRequestID());
						request.setRequestID(incrementRequestID());
						
						dos.writeUTF("Enter the filepath");
						in = dis.readUTF();
						request.setFilePath(in);

						dos.writeUTF("Enter the deadline (O for urgent, 1 for non urgent)");
						in2 = dis.readUTF();
						if (in2.equals("0")) {
							Main.TimeServer.urgentRequest.add(request);
						}
						if (in2.equals("1")) {
							Main.TimeServer.nonUrgentRequest.add(request);
						}
						dos.writeUTF("Submitted Request");

					}
					
					
					
				}
				
				if (in.equalsIgnoreCase("Status"))
				{
					dos.writeUTF("Enter the request ID to get the status");
					in2 = dis.readUTF();
					Boolean requestExists = false;
					
					DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); 
					
					for (Request r : Main.TimeServer.returnAllRequests())
					{
						
						if (r.getUserName().equals(username))
						{
							if (r.getRequestID().toString().equals(in2))
							{
								
								dos.writeUTF("Request ID: "+r.getRequestID()+", Status: "+r.getStatus()+", Date started: "+date.format(r.getStartTime()));
								requestExists = true;

							}
						
							if (!requestExists)
							{
								dos.writeUTF("No such request ID");
							}
						}
						else
						{
							dos.writeUTF("Not authorised");
						}
					}
						
				}
				
				//Still need to stop requests once they have been started by a worker!!!!
				if (in.equalsIgnoreCase("Stop"))
				{
					dos.writeUTF("Enter the request ID to stop");
					in2 = dis.readUTF();
					
					for (Request r : Main.TimeServer.returnAllRequests())
					{
						if (r.getRequestID().toString().equals(in2))
						{
							if (r.getStatus().equalsIgnoreCase("inactive"))
							{
								Main.TimeServer.deleteUrgentRequest(r);
								Main.TimeServer.deleteNonUrgentRequest(r);
								dos.writeUTF("Request ID: "+r.getRequestID()+" has been stopped");
							}
						}
					}
					
				}
				
				//Delete later. For testing purposes
				if (in.equalsIgnoreCase("List"))
				{
					DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
					
					dos.writeUTF("List all requests");
					for (Request r : Main.TimeServer.returnAllRequests())
					{
						dos.writeUTF("Request ID: "+r.getRequestID()+", Status: "+r.getStatus()+", Date started: "+date.format(r.getStartTime()));
					}
					
				}
				
				if (in.equalsIgnoreCase("No"))
				{
					
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
		for (User i : TimeServer.userList) {
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
		Integer nonUrgent = TimeServer.nonUrgentRequest.size();
		Integer urgent = TimeServer.urgentRequest.size();
		
		return (nonUrgent+urgent+1);
	}
}
