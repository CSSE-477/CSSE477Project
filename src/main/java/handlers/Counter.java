package handlers;

import utils.SwsLogger;

public class Counter {

	public int numRequests;
	public long timeStamp;
	public long timeout;
	public int maxRequests = 100;
	
	public Counter() {
		this.numRequests = 1;
		this.timeStamp = System.currentTimeMillis();
		this.timeout = 60 * 1000; // Default to 1m timeout
	}
	
	public boolean increment() {
		long currTime = System.currentTimeMillis();
		if (currTime - this.timeStamp > this.timeout) {
			this.numRequests = 0;
			this.timeStamp = currTime;
			SwsLogger.accessLogger.info("60 seconds has passed. Resetting rejection counter");
		}
		this.numRequests++;
		boolean isServiced = this.numRequests < this.maxRequests;
		if (!isServiced) this.timeout = (long) Math.pow(this.timeout, 2);  //double Timeout every additional time
		return isServiced;
	}
}
