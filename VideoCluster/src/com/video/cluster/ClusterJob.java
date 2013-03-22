package com.video.cluster;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class ClusterJob {

	private static int iterator = 0;
	public static final String KMEANS_CENTER_PATH = "hdfs://localhost:9000/user/xiaojian/Cluster/centroid.txt";
	public static final String KMEANS_IN_PATH = "hdfs://localhost:9000/user/xiaojian/Lights";
	public static final String KMEANS_OUT_PATH = "hdfs://localhost:9000/user/xiaojian/Cluster/depth_";
	public static final String KMEANS_CENTER_KEY = "centroid.path";

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		while (!MyReducer.isStable) {
			System.out.println("not stable");
			DoJob(conf);
		}
		System.out.println("stable");
		Path p=new Path(KMEANS_OUT_PATH+(iterator-1)+"/");
		updateClusterResult(p.getFileSystem(conf),p,conf);

		// String[] otherArgs = new GenericOptionsParser(conf,
		// args).getRemainingArgs();
		// if (otherArgs.length != 2) {
		// System.err.println("Usage: video <in> <out>");
		// System.exit(2);
		// }

	}
	
	public static void updateClusterResult(FileSystem fs, Path p, Configuration conf){
		
		String dst = "hdfs://localhost:9000/user/xiaojian/Cluster/result";
		try {
			FileStatus[] status=fs.listStatus(p);
			for(FileStatus item : status){
				if(!item.isDir()){
					Path path=item.getPath();
					if(!path.getName().equals("_SUCCESS")){
						Path dstpath=new Path(dst);
						FSDataOutputStream out ;
						if (!fs.exists(dstpath)) {
							out= fs.create(dstpath);
							IOUtils.copyBytes(fs.open(path), out, 1024000, true);
						}else{
							fs.delete(dstpath, true);
							out= fs.create(dstpath);
							IOUtils.copyBytes(fs.open(path), out, 1024000, true);
						}
						out.close();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public static void DoJob(Configuration conf) throws IOException {

		String[] otherArgs = new String[] {
				"hdfs://localhost:9000/user/xiaojian/Lights",
				"hdfs://localhost:9000/user/xiaojian/Result/cluster" };

		Job job = new Job(conf, "k means clustering "+ iterator);
		job.setJarByClass(ClusterJob.class);
		job.setMapperClass(MyMapper.class);
		MyMapper.hasLoadCenter=false;
		job.setReducerClass(MyReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		// FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		Path center = new Path(KMEANS_CENTER_PATH);
		Path in = new Path(KMEANS_IN_PATH);
		Path out = new Path(KMEANS_OUT_PATH + iterator);
		conf.set("KMEANS_CENTER_KEY",center.toString());
		FileSystem fs=out.getFileSystem(conf);
		
		FileInputFormat.addInputPath(job, in);
		if(fs.exists(out)){
			fs.delete(out,true);
		}
		FileOutputFormat.setOutputPath(job, out);

		System.out.println("Job enter:" + System.currentTimeMillis() / 1000);
		try {
			job.waitForCompletion(true);
			System.out.println("Job leave:" + System.currentTimeMillis() / 1000);
			iterator++;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
