package uk.ac.ed.inf.powergrab;

public enum Direction {
	// Define all 16 Directions with their angles (in degrees)
	N(0),
	NNE(22.5),
	NE(45),
	ENE(67.5),
	E(90),
	ESE(112.5),
	SE(135),
	SSE(157.5),
	S(180),
	SSW(202.5),
	SW(225),
	WSW(247.5),
	W(270),
	WNW(292.5),
	NW(315),
	NNW(337.5);
	
	// Store sin and cos so there is no need to recompute them
	private final double sin;
	private final double cos;

	// Constructor
	// Called only once for each Direction
	Direction(double angle) {
		// Compute sin and cos for the Direction
		this.sin = Math.sin(Math.toRadians(angle));
		this.cos = Math.cos(Math.toRadians(angle));
	}

	public double sin() {
		// Sin of Direction angle
		return this.sin;
	}

	public double cos() {
		// Cos of Direction angle
		return this.cos;
	}
}
