package Main;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class WorkerCountThread extends Thread {

	Socket serverClient;

	String username = "";

	String in = "";

	WorkerCountThread(Socket inSocket) {
		serverClient = inSocket;
	}

	public void run() {
		try {
			// Checks the list of requests and number of workers and adds workers as
			// necessary
			while (true) {

				//If there are no workers this adds one
				synchronized (Main.Server.workerList) {
					if (Main.Server.workerList.size() < 1) {
						Socket workerSocket = new Socket("127.0.0.1", 1254);
						DataOutputStream dos2;
						OutputStream s2out = workerSocket.getOutputStream();
						dos2 = new DataOutputStream(s2out);
						dos2.writeUTF("WORKER");
						Thread.sleep(10);
					}
				}

				//If the request size goes over 10 then more workers will be created
				synchronized (Main.Server.requestList) {
					Integer requestSize = Main.Server.returnAllRequestsToProcess(Main.Server.requestList).size();
					if (requestSize > 10) {

						if (Main.Server.workerList.size() < 8) {
							Socket workerSocket = new Socket("127.0.0.1", 1254);
							DataOutputStream dos2;
							OutputStream s2out = workerSocket.getOutputStream();
							dos2 = new DataOutputStream(s2out);
							dos2.writeUTF("WORKER");
							Thread.sleep(1);
						}

					}
				}

				//If a worker is terminated without finishing this will move the request to another worker
				synchronized (Main.Server.workerList) {
					for (Worker wt : Main.Server.workerList) {
						if (wt.getCurrentThread().getState() == Thread.State.TERMINATED) {
							System.out.print("\nWorker ID: " + wt.getWorkerID() + " has failed...");
							// Thread has been terminated
							synchronized (Main.Server.workerList) {
								Server.workerList.remove(wt);
							}
							synchronized (Main.Server.timeIntList) {
								Server.updateTimeList();
							}

							synchronized (Main.Server.requestList) {
								for (Request r : Server.requestList) {
									if (r.getRequestID() == wt.getProcessingRequestID()
											&& !r.getStatus().equals(Request.Status.COMPLETED)) {
										r.setStatus(Request.Status.INACTIVE);
									}
								}
							}

						}
					}
				}

			}

		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

}
