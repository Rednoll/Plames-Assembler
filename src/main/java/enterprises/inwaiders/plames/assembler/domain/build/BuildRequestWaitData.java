package enterprises.inwaiders.plames.assembler.domain.build;

public class BuildRequestWaitData {
	
	private int placeInQueue = -1;
	private double estimatedWaitingTime = -1;
	
	public BuildRequestWaitData() {
		
	}
	
	public BuildRequestWaitData(int placeInQueue, double estimatedWaitingTime) {
		
		this.placeInQueue = placeInQueue;
		this.estimatedWaitingTime = estimatedWaitingTime;
	}
	
	public double getEstimatedWaitingTime() {
		
		return this.estimatedWaitingTime;
	}
	
	public int getPlaceInQueue() {
		
		return this.placeInQueue;
	}
}
