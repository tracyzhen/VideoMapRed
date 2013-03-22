package com.video.mapred;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	
	private static String DIR="hdfs://localhost:9000/user/xiaojian/Frames/";
	private static String LIGHTS="hdfs://localhost:9000/user/xiaojian/Lights";
	private static String LOCALDIR="/usr/VideoMapRed/tmp/";
	
	public void map(Text key, Video value, Context context)
			throws IOException, InterruptedException {
//		System.out.println("-----------------------Mapper enter time---------:"+ System.currentTimeMillis()/1000);
//		System.out.println("VodeoMapper");
		String videoName=key.toString();
//		test_01_0_1.webm
		String tmp=videoName.substring(0,videoName.lastIndexOf("_"));
		String baseName=tmp.substring(0,tmp.lastIndexOf("_"));
		
		String rawVideo=LOCALDIR+videoName;
		FileOutputStream ostream=new FileOutputStream(new File(rawVideo));
		ostream.write(value.getVideoByteArray());
		ostream.close();
		
		String keyframe = new VideoFFmpeg().getKeyFrame(rawVideo);
		double light=(double)Math.round(new VideoFFmpeg().averageLight(keyframe)*1000)/1000;
        File file=new File(keyframe);
//        key=new Text(file.getName());
        Path dst=new Path(DIR+baseName+"/"+file.getName());
        Path lightfile=new Path(LIGHTS);
        
        InputStream in=new FileInputStream(file);
        Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(DIR+baseName+"/"+keyframe), conf);
		
		if (!fs.exists(dst)) {
			FSDataOutputStream out = fs.create(dst);
			IOUtils.copyBytes(in, out, 1024000, true);
		} else {
			System.out.println("File exists!");
		}
		in.close();
		if(!file.delete()){
			//删除本地文件
			System.out.println("File in use!!!!!!!!!!");
		}
		File tmpfile=new File(rawVideo);
		tmpfile.delete();
		
		String content=dst.toString()+":"+light+"\n";
		if(fs.exists(lightfile)){
//			OutputStream os=fs.append(lightfile);
//			os.write(content.getBytes());
//			os.flush();
//			os.close();
		}else{
		   fs.create(lightfile);
		}
		
		HBaseDAO.storeToHbase(fs, content, conf);
		
		
		
		
		context.write(key, new Text(light+""));
//		System.out.println("-----------------------Mapper leave time---------:"+ System.currentTimeMillis()/1000);
	}
}
