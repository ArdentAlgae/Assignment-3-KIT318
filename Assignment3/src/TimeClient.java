package Main;
import java.io.*;
import java.net.*;
import java.util.*;


class ServerThread extends Thread {
	  Socket serverClient;
	  int threadNum; // The thread's index in the arrayList
	  int requested = -1; // The index of the last user this user requested to chat with, used to tell whether a searched for name has been found.
	  					  // the -1 indicates that it applies to no existing thread.
	  int connected = -1; // The index of the user this user is connected to.
	  int pending = -1; // The index of the most recent user to request a chat.
	  
	  
	  
	  
	  
	  
	  DataOutputStream dos;
	  DataInputStream dis;
	  //ArrayList<ServerThread> serverList; // The arrayList
	  HashMap<String, String> passwordMap;
	  
	  
	  String[] profanityList; // The list of profanities to be blocked.
	  
	  
	  
	  
	  ServerThread(Socket inSocket, HashMap<String, String> inPasswordMap){
	    serverClient = inSocket;
	    passwordMap = inPasswordMap;
	    
	    //serverList = inServerList;
	  }
	  public void run(){
	    try{
	    	// Set up input and output. dis and dos are class variables so they can be accesed by all methods, and even
	    	// other threads, to send messages to this thread's user.
	    	OutputStream s1out = serverClient.getOutputStream();
			dos = new DataOutputStream(s1out);
			InputStream s1In = serverClient.getInputStream();
			dis = new DataInputStream(s1In);
			
			authenticate(); // Get a name and description from the user, make sure they are allowed.
	    	
			processInput(); // Loop that processes the user's input until exited, or an error occurs
			
	    	
	      dos.close();
	      s1out.close();
	      dis.close();
	      s1In.close();
	      serverClient.close();
	    }catch(Exception ex){
	      System.out.println(ex);
	    }
	  }
	  
	  public void authenticate()
	  {
		  try
		  {
			  boolean signedIn = false;
			  dos.writeUTF("If you do not have an account, please input \"NewAccount\".\n" +
					  		"If you do have an account, please input \"SignIn\".\n" +
					  		"You may change your mind at any time by entering one of the above commands.");
			  String in = dis.readUTF();
			  while(!signedIn)
			  {
				  if(in.equals("NewAccount"))
				  {
					  // Have the user input a name, make them input a new one if it is taken by another user or contains profanities.
					  dos.writeUTF("Please enter a username: ");
					  in = dis.readUTF();
					  boolean taken = nameTaken(in);
					  while(taken && !in.equals("SignIn"))
					  {
						  dos.writeUTF("There is already a user with this username. Please choose another.");
						  in = dis.readUTF();
						  taken = nameTaken(in);
					  }
					  if(!in.equals("SignIn"))
					  {
						  String password = newPassword();
						  passwordMap.put(in, password);
						// Let the user know they are connected, and what commands can be used.
						  dos.writeUTF("Thankyou, you are now connected. You have been assigned the password: " + password);
						  signedIn = true;
						  
						 // String in2 = "";
						  
						  Request request = new Request();
						  
						  if (!in.equals("exit"))
						  {
							  dos.writeUTF("Filter a String or File?");
							  in = dis.readUTF();
							  
							  if(in.equalsIgnoreCase("String"))
							  {
								  dos.writeUTF("Generated Request ID");
								  request.setRequestID(newPassword());
								  
								  dos.writeUTF("Enter the string content");
								  in = dis.readUTF();
								  request.setStringContent(in);
								  
								  dos.writeUTF("Enter the deadline");
								  in = dis.readUTF();
								  
								  
								  //Placeholder
								  if (in.equals("0"))
								  {
									  Main.TimeServer.urgentRequest.add(request);
								  }
								  if (in.equals("1"))
								  {
									  Main.TimeServer.nonUrgentRequest.add(request);
								  }
								  

							  }
							  if(in.equalsIgnoreCase("File"))
							  {
								  dos.writeUTF("Enter the filepath");
								  in = dis.readUTF();
								  

							  }
							  
						  }
						  
						  
						  
						  
					  }
				  }
				  else if(in.equals("SignIn"))
				  {
					  dos.writeUTF("Please enter your username: "); 
					  in = dis.readUTF();
					  boolean taken = nameTaken(in);
					  while(!taken && !in.equals("NewAccount"))
					  {
						  dos.writeUTF("There no account with this username. Please choose another.");
						  in = dis.readUTF();
						  taken = nameTaken(in);
					  }
					  if(!in.equals("NewAccount"))
					  {
						  dos.writeUTF("Please enter your password.");
						  String name = in;
						  in = dis.readUTF();
						  while(!in.equals(passwordMap.get(name)))
						  {
							  dos.writeUTF("This password is incorrect. Please try again.");
							  in = dis.readUTF();
						  }
						  if(!in.equals("NewAccount"))
						  {
							  dos.writeUTF("Thankyou, you are now connected.");
							  signedIn = true;
						  }
					  }
				  }
				  else
				  {
					  dos.writeUTF("This is not a valid command. Please enter either \"NewAccount\" or \"SignIn\".");
					  in = dis.readUTF();
				  }
			  }
		  }
		  catch (IOException e)
		  {
				// TODO Auto-generated catch block
				e.printStackTrace();
		  }
	  }
	  
	  public String newPassword()
	  {
		  Random rnd = new Random();
		  String password = "";
		  for(int i = 0; i < 10; i++)
		  {
			  password = password + (char) (33 + rnd.nextInt(93));
		  }
		  return password;
	  }
	  
	  public void processInput()
	  {
		  try {
			// Take the first input and begin the loop, which will end if the input is ever "Exit"
			String stringIn = dis.readUTF();
	    	while(!stringIn.equals("Exit"))
	    	{
	    		// if "Help" entered, list all commands
	    		if(stringIn.equals("Help"))
	    		{
	    			dos.writeUTF("Help: listing all the commands\r\n"
	    					+ "Listusers: listing all the currently registered (connected) users\r\n"
	    					+ "RequestChat username: send request for chat with another user\r\n"
	    					+ "AcceptChat: accept chat invitation from another user\r\n"
	    					+ "RejectChat: reject the chat invitation from another user\r\n"
	    					+ "ExitChat: disconnect the chat with another user\r\n"
	    					+ "Exit: disconnect from server\r\n"
	    					+ "All commands are case sensitive.\r\n"
	    					+ "RequestChat is the only command that will work if there is something typed after it.");
	    		}
	    		
	    		stringIn = dis.readUTF(); // Wait for a new input before repeating the loop.
	    	}
	    	
		  }
		  catch (IOException e)
		  {
				// TODO Auto-generated catch block
				e.printStackTrace();
		  }
	  }
	  
	  // Loops through all other threads in the arrayList, and returns true if this thread's name is the name of
	  // any other thread.
	  public boolean nameTaken(String name)
	  {
		  for (String i : passwordMap.keySet())
		  {
			  if(name.equals(i))
			  {
				  return true;
			  }
		  }
		  return false;
	  }
	}
