package uk.ac.ed.inf.powergrab;

public class Position {
	public double latitude;
	public double longitude;

	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Position nextPosition(Direction direction) {
		// Calculate the resulting coordinates from moving in the given direction
		double nextLatitude = this.latitude + Drone.TRAVEL_DISTANCE * direction.cos();
		double nextLongitude = this.longitude + Drone.TRAVEL_DISTANCE * direction.sin();

		return new Position(nextLatitude, nextLongitude);
	}

	public boolean inPlayArea() {
		// Define the diagonal boundaries of the play area
		Position KFC = new Position(55.946233, -3.184319);
		Position topOfMeadows = new Position(55.942617, -3.192473);

		return inRectangularRegion(KFC, topOfMeadows);
	}

	private boolean inRectangularRegion(Position topRight, Position bottomLeft) {
		// Return true if this position is within rectangular region of parameters
		// Otherwise return false
		return this.latitude < topRight.latitude && this.latitude > bottomLeft.latitude
				&& this.longitude < topRight.longitude && this.longitude > bottomLeft.longitude;
	}

	public boolean equals(Position otherPosition){
		return (this.latitude == otherPosition.latitude && this.longitude == otherPosition.longitude);
	}
}
