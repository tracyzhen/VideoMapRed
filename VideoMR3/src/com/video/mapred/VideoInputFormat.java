package com.video.mapred;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;


import com.video.bean.Video;

public class VideoInputFormat extends FileInputFormat<Text, Video>{

	@Override
	public RecordReader<Text, Video> createRecordReader(InputSplit arg0,
			TaskAttemptContext arg1) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return new VideoRecordReader();
//		return new VideoRecordReader_();
	}
	
	public boolean isSplitable(JobContext context, Path filename){
		return false;
	}

	

}
