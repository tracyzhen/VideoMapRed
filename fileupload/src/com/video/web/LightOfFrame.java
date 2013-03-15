package com.video.web;

public class LightOfFrame {

	/**
	 * @param args
	 */
	String path=" ";
	double light=0.0;
	double likehood=0.0;
	
	public void setLikehood(double likehood){
		this.likehood=likehood;
	}
	public void setLight(double light){
		this.light=light;
	}
	public void setPath(String path){
		this.path=path;
	}
	public double getLikehood(){
		return likehood;
	}
	public double getLight(){
		return light;
	}
	public String getPath(){
		return path;
	}

}
