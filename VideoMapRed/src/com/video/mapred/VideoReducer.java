package com.video.mapred;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class VideoReducer extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		System.out.println("-----------------------Reducer enter time---------:"+ System.currentTimeMillis()/1000);
		System.out.println("Reducer:"+key.toString());
		
		StringBuilder output=new StringBuilder();
		for(Text vo :values){
//			output=output.append(vo.toString().subSequence(0, vo.toString().lastIndexOf("|")));
			output=output.append(vo.toString());
		}
		System.out.println("-----------------------");
		
		context.write(key, new Text(output.toString()));
		System.out.println("-----------------------Reducer leave time---------:"+ System.currentTimeMillis()/1000);
	}

}
