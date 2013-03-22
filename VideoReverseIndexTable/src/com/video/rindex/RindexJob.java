package com.video.rindex;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class RindexJob {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		try {

			Path in = new Path(
					"hdfs://localhost:9000/user/xiaojian/Cluster/result");
			Path out = new Path("hdfs://localhost:9000/user/xiaojian/Rindex");

			Job job = new Job(conf, "reverse idnex");
			job.setJarByClass(RindexJob.class);
			job.setMapperClass(MyMapper.class);
			job.setReducerClass(MyReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setOutputFormatClass(VideoOutputFormat.class);

			FileSystem fs = out.getFileSystem(conf);
			FileInputFormat.addInputPath(job, in);
			if (fs.exists(out)) {
				fs.delete(out, true);
			}
			FileOutputFormat.setOutputPath(job, out);
			job.waitForCompletion(true);

			updateClusterResult(fs, out, conf);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void updateClusterResult(FileSystem fs, Path p,
			Configuration conf) {

		try {
			FileStatus[] status = fs.listStatus(p);
			for (FileStatus item : status) {
				if (!item.isDir()) {
					Path path = item.getPath();
					if (!path.getName().equals("_SUCCESS")) {
                        new HBaseDAO().storeToHbase(fs,path,conf);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
