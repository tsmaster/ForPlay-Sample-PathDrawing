package com.google.forplay.sample.pathdrawing.core;

import java.util.ArrayList;

public class WaypointList {
	ArrayList<Vector2> points = new ArrayList<Vector2>(0);

	public Vector2 peek() {
		if (points.size() == 0) {
			return null;
		}
		return points.get(0);
	}

	public void clear() {
		points.clear();
	}

	public int size() {
		return points.size();
	}

	public void dequeue() {
		points.remove(0);
	}

	public void enqueue(Vector2 vector) {
		points.add(vector);
	}

	public Vector2 getWaypoint(int i) {
		return points.get(i);
	}

}
