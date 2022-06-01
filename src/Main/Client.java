package Main;
import java.util.*;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.net.*;
import java.io.*;

public class Client {
	
	public static void uploadfile() {
		try {
	          String host = "131.217.172.91";
	          String user = "ubuntu";
	          String privateKey = "C:\\Users\\jhanc\\Downloads\\TestKeyPrivate.pem"; //please provide your ppk file
	          JSch jsch = new JSch();
	          Session session = jsch.getSession(user, host, 22);
	          Properties config = new Properties();
	         // session.setPassword("KIT418@utas"); ////if password is empty please comment it
	          jsch.addIdentity(privateKey);
	          System.out.println("identity added "); 
	          config.put("StrictHostKeyChecking", "no");
	          session.setConfig(config);
	          session.connect();
	       
	          Channel channel = session.openChannel("sftp");
	          channel.connect();
	          ChannelSftp sftpChannel = (ChannelSftp) channel;
	          sftpChannel.put("C:\\Users\\jhanc\\Downloads\\test\\test.txt", "/home/ubuntu/Input/test.text"); //Provide different input file/folder
	         
	          System.out.println("done");

	          sftpChannel.exit();
	          session.disconnect();
	      } catch (JSchException e) {
	          e.printStackTrace();
	      } catch (SftpException e) {
	          e.printStackTrace();
	      }
			catch(Exception e){
	      	   System.out.println(e);
			}   
		}
	
	/**
	 * Main method for the Client
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		//Open your connection to a server, at port 1254
		try 
		{
			Socket clientSocket = new Socket("127.0.0.1"/*"131.217.172.91"*/,1254); // The first address is to test code locally, the second connects to the nectar cloud.
			//Socket clientSocket = new Socket("131.217.172.91",1254); 
				
			Scanner input = new Scanner(System.in);
			String sending = "";

			// Get an output file handle from the socket and read the input
			OutputStream s1Out = clientSocket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(s1Out);
			dos.writeUTF("CLIENT");

			// Instantiate and start a thread which will read and output all input from the
			// server.
			ClientThread ct = new ClientThread(clientSocket);
			ct.start();

			// Until an error occurs, wait for the user to input text and then send that
			// text to the server.
			try {
				while (true) {
					sending = new String(input.nextLine());
					dos.writeUTF(sending);

					if (sending.equals("File")) {
						// uploadfile();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			// When done, just close the connection and exit
			dos.close();
			s1Out.close();
			clientSocket.close();

		}
		catch(Exception e)
		{
			System.out.print("Cannot connect to server \nExiting...");
		}
	}
}

