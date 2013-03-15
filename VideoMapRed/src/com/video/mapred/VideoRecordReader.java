package com.video.mapred;

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

public class VideoRecordReader extends RecordReader<Text,Video>{

	private FileSplit fileSplit;
	private Configuration conf;
	private InputStream in;
	private Text key=new Text("");
	private Video value=null;
	private boolean processed=false;
	
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
		return processed ? 1.0f : 0.0f;
	}

	@Override
	public void initialize(InputSplit arg0, TaskAttemptContext arg1)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		this.fileSplit = (FileSplit) arg0;
        this.conf = arg1.getConfiguration();

        final Path file = fileSplit.getPath();

        FileSystem fs = file.getFileSystem(conf);
        in = fs.open(file);
		
		
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		if (!processed) {
            byte[] contents = new byte[(int) fileSplit.getLength()];
            Path file = fileSplit.getPath();
            key.set(file.getName());

            try {
                IOUtils.readFully(in, contents, 0, contents.length);
                value=new Video(contents);
//                value.set(contents, 0, contents.length);
                value.setIndex(1);
            } finally {
                IOUtils.closeStream(in);
            }

            processed = true;
            return true;
        }

        return false;
	}

	
}
