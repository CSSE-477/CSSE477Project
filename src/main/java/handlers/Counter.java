package handlers;

import utils.SwsLogger;

public class Counter {

	public int numRequests;
	public long timeStamp;
	
	public Counter() {
		this.numRequests = 1;
		this.timeStamp = System.currentTimeMillis();
	}
	
	public boolean increment() {
		long currTime = System.currentTimeMillis();
		if (currTime - this.timeStamp > 60 * 1000) {
			this.numRequests = 0;
			this.timeStamp = currTime;
			SwsLogger.accessLogger.info("60 seconds has passed. Resetting rejection counter");
		}
		this.numRequests++;
		return numRequests < 10000000;
	}

}
