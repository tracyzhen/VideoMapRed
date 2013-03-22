package com.video.cluster;

public class Cluster {
	String keyframe;
	double light;
	double distance = Double.MAX_VALUE;
	Cluster center;

	public Cluster(String val) {
		this.setKeyframe(val.substring(0, val.lastIndexOf(":")));
		this.setLight(Double.parseDouble(val.substring(val.lastIndexOf(":") + 1)));
	}

	public String getKeyframe() {
		return keyframe;
	}

	public void setKeyframe(String keyframe) {
		this.keyframe = keyframe;
	}

	public double getLight() {
		return light;
	}

	public void setLight(double light) {
		this.light = light;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Cluster getCenter() {
		return center;
	}

	public void setCenter(Cluster center) {
		this.center = center;
	}

}
