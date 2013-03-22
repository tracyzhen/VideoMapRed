package com.video.mapred;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class VideoReducer extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

//		System.out.println("Reducer:"+key.toString());
	
		StringBuilder output=new StringBuilder();
		for(Text vo :values){
//			System.out.println("AverageLight" + vo);
			output=output.append(vo.toString());
		}
		
		context.write(key, new Text(output.toString()));
	}

}
