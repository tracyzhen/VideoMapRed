package com.video.bean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class Video implements Writable{

	private long length;
	private byte[] videoByteArray;
	private int index;
	
	public Video(){}
	
	public Video(byte[] video){
		videoByteArray=video;
		length=video.length;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		length=in.readLong();
		videoByteArray=new byte[(int)length];
		in.readFully(videoByteArray);
		index=in.readInt();
		
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeLong(length);
		out.write(videoByteArray);
		out.writeInt(index);
	}
	
	public static Video read(DataInput in) throws IOException{
		Video vo=new Video();
		vo.readFields(in);
		return vo;
	}
	
	public byte[] getVideoByteArray(){
		return this.videoByteArray;
	}
	
	public int getIndex(){
		return index;
	}
	
	public void setIndex(int index){
		this.index=index;
	}

	public long getLength(){
		return videoByteArray.length;
	}
}
