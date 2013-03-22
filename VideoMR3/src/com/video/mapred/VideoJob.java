package com.video.mapred;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class VideoJob {
	
	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();
		
	    
//		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
//	    if (otherArgs.length != 2) {
//	      System.err.println("Usage: video <in> <out>");
//	      System.exit(2);
//	    }  
	    
	     
	    
		String[] otherArgs=new String[]{"hdfs://localhost:9000/user/xiaojian/_Split_/A_01",
				"hdfs://localhost:9000/user/xiaojian/Result/hbase1"    		
		};
		
		
	    Job job = new Job(conf, "video split");
	    job.setJarByClass(VideoJob.class);    
	    job.setMapperClass(VideoMapper.class);
	    job.setReducerClass(VideoReducer.class); 
	    
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	        
	    job.setInputFormatClass(VideoInputFormat.class);
	    job.setOutputFormatClass(VideoOutputFormat.class);
	 
	    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));   
	    
//	    Path outputPath = new Path(job.getWorkingDirectory().toString()+"/"+args[1]);
//	    FileSystem fs = outputPath.getFileSystem(conf);
//	    if(fs.exists(outputPath)){
//	    	fs.delete(outputPath, true);
//	    }	    
//	    System.exit(job.waitForCompletion(true) ? 0 : 1);    
	    System.out.println("Job enter:"+ System.currentTimeMillis()/1000);
	    if(job.waitForCompletion(true)){
	    	System.out.println("Job leave:"+ System.currentTimeMillis()/1000);
	    	System.exit(0);
	    }else{
	    	System.exit(1);
	    }
	 }
}
