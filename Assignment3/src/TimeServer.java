import java.net.*;
import java.io.*;
import java.util.*;
public class TimeServer{

	  public static void main(String[] args) throws Exception {
	    try{
	      // Initialise server, and an arrayList of serverThreads, which will allow the threads to communicate with each other.
	      ServerSocket server=new ServerSocket(1254);
	      HashMap<String, String> passwordMap = new HashMap<String, String>();
	      
	      // This loop will wait for connections, and accept them whenever a client connects.
	      while(true){
	    	boolean added = false; // Whether a server thread has been added to the arrayList
	        Socket serverClient=server.accept();  //server accept the client connection request
	        ServerThread sct = new ServerThread(serverClient, passwordMap); //send  the request to a separate thread
	        sct.start(); // Start the server thread that was added to the arrayList.
	      }
	    }catch(Exception e){
	      System.out.println(e);
	    }
	  }
	}
