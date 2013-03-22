package com.video.rindex;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MyReducer extends Reducer<Text, Text, Text, Text> {

	private String output="";
	@Override
	protected void reduce(Text key, Iterable<Text> value, Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
              for(Text v : value){
            	  output+=v.toString();
              }
              context.write(key, new Text(output));
	}

}
