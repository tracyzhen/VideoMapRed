package com.video.cluster;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MyMapper extends Mapper<LongWritable, Text, Text, Text> {
    
	public static boolean hasLoadCenter=false;
	
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		super.setup(context);
		if(hasLoadCenter){
			return ;
		}else{
			Configuration conf=context.getConfiguration();
			Path centroidPath=new Path(ClusterJob.KMEANS_CENTER_PATH);
			FileSystem fs=centroidPath.getFileSystem(conf);
			new KMeansCluster().loadKMeansCenter(fs,centroidPath,conf,KMeansCluster.kcenter);
			hasLoadCenter=true;
		}
	}

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		
		System.out.println("\nkey:"+key +" value:"+value.toString());
		Cluster cl= new KMeansCluster().calCenter(value.toString());
		Text outKey=new Text(cl.getCenter().getKeyframe()+":"+cl.getCenter().getLight());
		Text outVal=new Text(cl.getKeyframe()+":"+cl.getLight());
		context.write(outKey,outVal);
	}
}
