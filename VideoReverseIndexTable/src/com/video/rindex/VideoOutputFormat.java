package com.video.rindex;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;



public class VideoOutputFormat extends FileOutputFormat<Text,Text> {

	@Override
	public RecordWriter<Text, Text> getRecordWriter(TaskAttemptContext arg0)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		Configuration conf = arg0.getConfiguration();
		Path file = getDefaultWorkFile(arg0, "");
		/**
		 * test
		 */
//		System.out.println("********************************************");
//		System.out.println("the path got in VideoOutputFormat is:      "+file.toString());
//		System.out.println("********************************************");
		
		FileSystem fs = file.getFileSystem(conf);
		FSDataOutputStream output = fs.create(file);
		
		return new VideoRecordWriter(output);
	}

}
