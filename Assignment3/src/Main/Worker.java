package Main;

import java.util.LinkedList;

public class Worker {
	String socket;
	String status;	//available/ not available
	int workerID;
	
	String cpu; //
	String storage;
	
	
	Integer numberRequest;
	
	
	public String getSocket() {
		return socket;
	}


	public void setSocket(String socket) {
		this.socket = socket;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public int getWorkerID() {
		return workerID;
	}


	public void setWorkerID(int i) {
		this.workerID = i;
	}


	public String getCpu() {
		return cpu;
	}


	public void setCpu(String cpu) {
		this.cpu = cpu;
	}


	public String getStorage() {
		return storage;
	}


	public void setStorage(String storage) {
		this.storage = storage;
	}


	public Integer getNumberRequest() {
		return numberRequest;
	}


	public void setNumberRequest(Integer numberRequest) {
		this.numberRequest = numberRequest;
	}


	

}
