package com.google.forplay.sample.pathdrawing.core;

public class Vector2 {
	public static Vector2 ZERO = new Vector2(0, 0);
	float x;
	float y;
	
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public static float distance(Vector2 a, Vector2 b) {
		return (float) Math.sqrt(distanceSquared(a, b));
	}

	public static float distanceSquared(Vector2 a, Vector2 b) {
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		return dx * dx + dy * dy;
	}

	public float lengthSquared() {
		return x * x + y * y;
	}

	public void normalize() {
		float length = (float) Math.sqrt(lengthSquared());
		x /= length;
		y /= length;
	}
	
	public static Vector2 subtract(Vector2 a, Vector2 b) {
		return new Vector2(a.x - b.x, a.y - b.y);
	}

	public static Vector2 multiply(Vector2 vector, float scalar) {
		return new Vector2(vector.x * scalar, vector.y * scalar);
	}

	public static Vector2 add(Vector2 a, Vector2 b) {
		return new Vector2(a.x + b.x, a.y + b.y);
	}
}
