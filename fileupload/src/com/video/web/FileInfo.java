package com.video.web;

public class FileInfo {

	private String name;
	private long size;
	private String uri;
	private int isfirstpart;
	private String light;
//	private String playalluri;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		if(isFirstfile(name))
			this.setIsfirstpart(1);
		else
			this.setIsfirstpart(0);
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public int getIsfirstpart() {
		return isfirstpart;
	}
	public void setIsfirstpart(int isfirstpart) {
		this.isfirstpart = isfirstpart;
	}
	
	
	public String getLight() {
		return light;
	}
	
	public void setLight(String light) {
		this.light = light;
	}
	private boolean isFirstfile(String name){
		//file_01.webm
//		System.out.println(name);
		int end=name.lastIndexOf(".");
		int begin=name.lastIndexOf("_");
		if(name.subSequence(begin+1, end).equals("01"))
			return true;
		else
			return false;
	}
	
}
