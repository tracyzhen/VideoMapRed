package com.video.mapred;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.video.bean.Video;

public class VideoRecordReader_  extends RecordReader<Text,Video>{

	private long start;
	private long end;
	
	private Text key=null;
	private Video value=null;
	private VideoProcess videoProcess;
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public Video getCurrentValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return (start == end ? 1.0f : 0.0f);
	}

	@Override
	public void initialize(InputSplit arg0, TaskAttemptContext arg1)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		FileSplit split=(FileSplit)arg0;
		start=0;
		final Path file=split.getPath();
		System.out.println("Reader:"+file);
		
		//copy to local
		Configuration conf=arg1.getConfiguration();
		FileSystem fs=file.getFileSystem(conf);
		InputStream in=fs.open(file);
		String localfile="/usr/VideoMapRed/tmp/"+split.getPath().getName();
		
		FileOutputStream ostream=new FileOutputStream(new File(localfile));
		byte[] contents = new byte[(int) split.getLength()];
		IOUtils.readFully(in, contents, 0, (int)split.getLength());
		ostream.write(contents);
		ostream.close();
		in.close();
		
		Path localpath=new Path(localfile);
		
		videoProcess=new VideoProcess(localpath);
		end=videoProcess.getNumOfClips();
		
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		if(start < end){
			key=new Text();
			key.set(videoProcess.getNextName(start));
			value=new Video(videoProcess.getNextClip(start));
			value.setIndex((int)start);
			start++;
			return true;
		}else{
			videoProcess.cleanUp();
			return false;
		}
	}

}
