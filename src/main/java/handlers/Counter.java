package handlers;

public class Counter {

	public int numRequests;
	public long timeStamp;
	
	public Counter() {
		this.numRequests = 1;
		this.timeStamp = System.currentTimeMillis();
	}
	
	public boolean increment() {
		long currTime = System.currentTimeMillis();
		if (currTime - this.timeStamp > 60) {
			this.numRequests = 0;
			this.timeStamp = currTime;
		}
		this.numRequests++;
		System.err.println(numRequests < 100);
		return numRequests < 100;
	}

}
