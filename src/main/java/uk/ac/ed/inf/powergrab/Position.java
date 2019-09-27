package uk.ac.ed.inf.powergrab;

public class Position {
	public double latitude;
	public double longitude;
	public double travelDistance = 0.0003;
	
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Position nextPosition(Direction direction) {
		double directionAngle = Math.toRadians(getDirectionAngle(direction));
		double nextLatitude = this.latitude + this.travelDistance * Math.cos(directionAngle);
		double nextLongitude = this.longitude + this.travelDistance* Math.sin(directionAngle);
		
		return new Position(nextLatitude, nextLongitude);
	}
	
	public boolean inPlayArea() {
		Position KFC = new Position(55.946233, -3.184319);
		Position topOfMeadows = new Position(55.942617, -3.192473);
		
		return inRectangularRegion(KFC, topOfMeadows);
	}
	
	private boolean inRectangularRegion(Position topRight, Position bottomLeft) {
		return this.latitude < topRight.latitude && this.latitude > bottomLeft.latitude
				&& this.longitude < topRight.longitude && this.longitude > bottomLeft.longitude;
	}
	
	private double getDirectionAngle(Direction d) {
		double angle = 0;
		switch(d) {
		case N:
			angle = 0;
			break;
		case NNE:
			angle = 22.5;
			break;
		case NE:
			angle = 45;
			break;
		case ENE:
			angle = 67.5;
			break;
		case E:
			angle = 90;
			break;
		case ESE:
			angle = 112.5;
			break;
		case SE:
			angle = 135;
			break;
		case SSE:
			angle = 157.5;
			break;
		case S:
			angle = 180;
			break;
		case SSW:
			angle = 202.5;
			break;
		case SW:
			angle = 225;
			break;
		case WSW:
			angle = 247.5;
			break;
		case W:
			angle = 270;
			break;
		case WNW:
			angle = 292.5;
			break;
		case NW:
			angle = 315;
			break;
		case NNW:
			angle = 337.5;
			break;
		}
		
		return angle;
	}
}
