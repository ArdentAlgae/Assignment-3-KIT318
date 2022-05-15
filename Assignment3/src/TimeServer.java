package Main;
import java.net.*;
import java.io.*;
import java.util.*;
public class TimeServer{
	
	public static LinkedList<Request> urgentRequest = new LinkedList<Request>();
    public static LinkedList<Request> nonUrgentRequest = new LinkedList<Request>();
    
    public static void addUrgent(Request r)
    {
    	urgentRequest.add(r);
    }

	  public static void main(String[] args) throws Exception {
		  
		  
	    try{
	      // Initialise server, and an arrayList of serverThreads, which will allow the threads to communicate with each other.
	      ServerSocket server=new ServerSocket(1254);
	      HashMap<String, String> passwordMap = new HashMap<String, String>();
	      
	      
	      // This loop will wait for connections, and accept them whenever a client connects.
	      while(true){
	    	  
//	    	 if (!urgentRequest.isEmpty())
//		        {
//		        	for (int i=0; i < urgentRequest.size(); i++)
//			        {
//		        		urgentRequest.get(i).getRequestID();
//		        		System.out.print(urgentRequest.get(i).getRequestID());	       
//		        		}
//		        }
//		        
//		        if (!nonUrgentRequest.isEmpty())
//		        {
//		        	for (int i=0; i < nonUrgentRequest.size(); i++)
//			        {
//		        		nonUrgentRequest.get(i).getRequestID();
//		        		System.out.print(nonUrgentRequest.get(i).getRequestID());	
//			        }
//		        }
	    	  
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
