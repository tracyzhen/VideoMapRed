package com.video.rindex;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MyMapper extends Mapper<LongWritable, Text, Text, Text>{

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		
		String[] str=value.toString().split("\t");
//		System.out.print(str[0]+"-->");
//		System.out.println(str[1]);
		context.write(new Text(str[0]), new Text(str[1]));
		
	}
}
