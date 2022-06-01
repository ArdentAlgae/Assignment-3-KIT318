package Main;

import java.time.Duration;
import java.time.LocalDateTime;

public class Request {

	String userName; // ID of user who generated request
	String stringContent; // Content of the string if the request is a string
	String inputFilePath; // Path of files
	
	String inputFileName;


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
	

	Status status;
	
	Urgency urgency;
	
	Profanity profanityLevel;
	
	
	enum Profanity {
		NONE, LOW, MODERATE, HIGH
	}
	
	/**
	 * @return the urgency
	 */
	public Urgency getUrgency() {
		return urgency;
	}

	/**
	 * @param urgency the urgency to set
	 */
	public void setUrgency(Urgency urgency) {
		this.urgency = urgency;
	}



	enum Urgency {
		URGENT, NONURGENT
	}
	
	/**
	 * @return the profanityLevel
	 */
	public Profanity getProfanityLevel() {
		return profanityLevel;
	}

	/**
	 * @param profanityLevel the profanityLevel to set
	 */
	public void setProfanityLevel(Profanity profanityLevel) {
		this.profanityLevel = profanityLevel;
	}
	
	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}
	
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
	
	public double generateBill()
	{
		double bill = 0;
		boolean finished = false;
		boolean startTimesChecked = false;
		TimeIntPair prev = Server.timeIntList.getFirst();
		for(TimeIntPair current: Server.timeIntList)
		{
			if(current.time.isAfter(startTime))
			{
				if(current.time.isAfter(endTime))
				{
					if(prev.time.isAfter(startTime))
					{
						bill += Duration.between(prev.time, endTime).toNanos()*Server.PRICERATE/prev.numWorkers;
					}
					else
					{
						bill += Duration.between(startTime, endTime).toNanos()*Server.PRICERATE/prev.numWorkers;
					}
					
					finished = true;
					break;
				}
				else
				{
					if(prev.time.isAfter(startTime))
					{
						bill += Duration.between(prev.time, current.time).toNanos()*Server.PRICERATE/prev.numWorkers;
					}
					else
					{
						bill += Duration.between(startTime, current.time).toNanos()*Server.PRICERATE/prev.numWorkers;
					}
				}
			}
			
			if(!startTimesChecked && current != prev)
			{
				boolean requestBeforeCurrent = false;
				
				if(!requestBeforeCurrent)
				{
					Server.timeIntList.removeFirst();
				}
				else
				{
					startTimesChecked = true;
				}
			}
			
			prev = current;
		}
		if(!finished)
		{
			if(prev.time.isAfter(startTime))
			{
				bill += Duration.between(prev.time, endTime).toNanos()*Server.PRICERATE/prev.numWorkers;
			}
			else
			{
				bill += Duration.between(startTime, endTime).toNanos()*Server.PRICERATE/prev.numWorkers;
			}
		}
		return bill;
	}

}
