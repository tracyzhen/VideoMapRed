package com.video.mapred;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class VideoRecordWriter extends RecordWriter<Text,Text>{

	private DataOutputStream output;
	
	public VideoRecordWriter(DataOutputStream output){
		this.output = output;		
	}
	@Override
	public void close(TaskAttemptContext context) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		output.close();
	}

	@Override
	public void write(Text key, Text value) throws IOException,
			InterruptedException {
		/*
		System.out.println("++++++++++++++++++++++++++++");
		System.out.println("VideoRecordWriter: key:"+key.toString() +", value: "+value.toString());
		System.out.println("++++++++++++++++++++++++++++");
		*/
		output.writeBytes(key.toString());
		output.writeBytes(":" + value.toString());
		output.writeBytes("\n");
		
		// TODO Auto-generated method stub

		

	}
	
	/*Test*/
	/*
	public static void main(String[] args){
		String str1="web_01_1.webm|web_01_2.webm|web_01_3.webm";
		String[] splits=str1.split("\\|");
		for(String str : splits){
			System.out.println(str);
		}
	}
	*/

}
