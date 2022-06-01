package Main;

import java.time.LocalDateTime;

public class TimeIntPair {
	public LocalDateTime time;
	public int numWorkers;
	
	public TimeIntPair(LocalDateTime time, int numWorkers)
	{
		this.time = time;
		this.numWorkers = numWorkers;
	}
}
