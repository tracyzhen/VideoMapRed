package com.video.mapred;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


import com.video.bean.Video;
import com.video.ffmpeg.VideoFFmpeg;

public class VideoMapper extends Mapper<Text,Video,Text,Text>{
	
	private static String DIR="hdfs://localhost:9000/user/xiaojian/_Split_/";
	private String fileparts="";
	
	public void map(Text key, Video value, Context context)
			throws IOException, InterruptedException {
		fileparts="";
//		System.out.println("-----------------------Mapper enter time---------:"+ System.currentTimeMillis()/1000);
//		System.out.println("VodeoMapper");
		String videoName=key.toString();
//		String baseName = videoName.substring(0, videoName.lastIndexOf("."));
		String baseName = videoName.substring(videoName.lastIndexOf("/")+1, videoName.lastIndexOf("."));
		String rawVideo=videoName.substring(0, videoName.lastIndexOf(".")) +"_"+value.getIndex() +
				videoName.substring(videoName.lastIndexOf("."));
//		System.out.println("Video name:"+videoName);
//		System.out.println("Index:"+value.getIndex());
		FileOutputStream ostream=new FileOutputStream(new File(rawVideo));
		ostream.write(value.getVideoByteArray());
		ostream.close();
		
		
		String[] parts = new VideoFFmpeg().split(rawVideo);

		
		long begin=System.currentTimeMillis();
		File file=null;
		InputStream in;
		for(String path :parts){
			file=new File(path);
			
			String dst = VideoMapper.DIR + baseName + "/"
					+ file.getName();
//			System.out.println(dst);
			try {
//				InputStream in = new BufferedInputStream(new FileInputStream(file));
				 in=new FileInputStream(file);
				Configuration conf = new Configuration();
				FileSystem fs = FileSystem.get(URI.create(dst), conf);
				Path fspath = new Path(dst);
				if (!fs.exists(fspath)) {
					FSDataOutputStream out = fs.create(new Path(dst));
					IOUtils.copyBytes(in, out, 1024000, true);
				} else {
					System.out.println("File exists!");
				}
				in.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            fileparts+=file.getName()+"|";
			if(!file.delete()){
				//删除本地文件
				System.out.println("File in use!!!!!!!!!!");
			}
		}
		File tmp=new File(rawVideo);
		tmp.delete();
//		System.out.println("-----------------------");
		long end=System.currentTimeMillis();
//		System.out.println("Take: "+ (end-begin)/1000);
//		System.out.println("fileparts:"+fileparts);
		context.write(new Text(key.toString().substring(key.toString().lastIndexOf("/")+1)), new Text(fileparts));
//		System.out.println("-----------------------Mapper leave time---------:"+ System.currentTimeMillis()/1000);
	}
}
