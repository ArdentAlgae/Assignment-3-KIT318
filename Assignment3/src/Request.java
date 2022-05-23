package Main;

import java.time.LocalDateTime;

public class Request {

	String userName; // ID of user who generated request
	String stringContent; // Content of the string if the request is a string
	String inputFilePath; // Path of files
	
	String inputFileName;

	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}





	String deadline; // Deadline of request
	
	String output; // Output of request if string
	String outputFilePath; // Output of request if file
	
	Integer requestID; // ID of request

	Type type; // Check if file or string
	
	LocalDateTime startTime;
	LocalDateTime endTime;
	Integer timeTaken;
	Float bill;
	
	
	
	float progress;
	Integer numFiles;
	Integer currentFile;
	
	public Integer getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(Integer currentFile) {
		this.currentFile = currentFile;
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float currentProgress) {
		this.progress = currentProgress;
	}

	public Integer getNumFiles() {
		return numFiles;
	}

	public void setNumFiles(Integer numFiles) {
		this.numFiles = numFiles;
	}

	
	
	
	
	Status status;
	
	enum Status {
		INACTIVE, PROCESSING, COMPLETED, CANCELLED
	}
	
	
	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public Integer getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(Integer timeTaken) {
		this.timeTaken = timeTaken;
	}

	public Float getBill() {
		return bill;
	}

	public void setBill(Float bill) {
		this.bill = bill;
	}

	
	
	

	enum Type {
		STRING, FILE
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(String outputFile) {
		this.outputFilePath = outputFile;
	}

	public Integer getRequestID() {
		return requestID;
	}

	public void setRequestID(Integer integer) {
		this.requestID = integer;
	}


	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getStringContent() {
		return stringContent;
	}

	public void setStringContent(String stringContent) {
		this.stringContent = stringContent;
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

	public void setInputFilePath(String filePath) {
		this.inputFilePath = filePath;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
