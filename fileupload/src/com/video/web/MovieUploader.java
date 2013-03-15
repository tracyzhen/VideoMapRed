package com.video.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class MovieUploader {

	
	private static String DIR="hdfs://localhost:9000/user/xiaojian/Movie/";
	private static String RESULT="hdfs://localhost:9000/user/xiaojian/Result/";
	private static String SPLITS="hdfs://localhost:9000/user/xiaojian/_Split_/";
	private static HashMap<String,String> lights;
	public static boolean upload(String dirname, File file) {

		// String localSrc="src/Dijkstra.py";
		String dst = "hdfs://localhost:9000/user/xiaojian/Movie/" + dirname + "/"
				+ file.getName();
		System.out.println(dst);
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			Configuration conf = new Configuration();
			conf.set("Hadoop.job.ugi", "hadoop-user,hadoop-user");
			FileSystem fs = FileSystem.get(URI.create(dst), conf);
			Path path = new Path(dst);
			if (!fs.exists(path)) {
				FSDataOutputStream out = fs.create(new Path(dst));
				IOUtils.copyBytes(in, out, 1024000, true);// �������СΪ1M
			} else {
				System.out.println("File exists!");
			}
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	public static InputStream download(String path) {
		path = "hdfs://localhost:9000/user/xiaojian/Movie/" + path;
		Configuration conf = new Configuration();
		try {
			FileSystem fs = FileSystem.get(URI.create(path), conf);
			if (!fs.exists(new Path(path)))
				return null;
			InputStream in = null;
			in = fs.open(new Path(path));
			return in;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static InputStream downloadFrame(String path) {
		path = "hdfs://localhost:9000/user/xiaojian/Frames" + path;
//		System.out.println(path);
		Configuration conf = new Configuration();
		try {
			FileSystem fs = FileSystem.get(URI.create(path), conf);
			if (!fs.exists(new Path(path)))
				return null;
			InputStream in = null;
			in = fs.open(new Path(path));
			return in;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static List<FileInfo> listFiles() {
		ArrayList<FileInfo> files = new ArrayList<FileInfo>();
		String dirpath="hdfs://localhost:9000/user/xiaojian/Movie";
		Path path = new Path(dirpath);
		Configuration conf = new Configuration();
		FileSystem fs;
		try {
			fs = FileSystem.get(URI.create(dirpath),conf);
			ArrayList<FileStatus> status = new ArrayList<FileStatus>(Arrays.asList(fs.listStatus(path)));
			//for(FileStatus item : status){
			FileStatus item=null;
		    while((!status.isEmpty())&&((item=status.remove(0))!=null)){
				if(item.isDir()){
					status.addAll(Arrays.asList(fs.listStatus(item.getPath())));
				}else{
					FileInfo fileinfo=new FileInfo();
					fileinfo.setUri(path2uri(item.getPath()));
					fileinfo.setSize(item.getLen());
					fileinfo.setName(item.getPath().getName());
					files.add(fileinfo);
				}
			}
			return files;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	public static List<FileInfo> listsplitFiles() {
		ArrayList<FileInfo> files = new ArrayList<FileInfo>();
		String dirpath="hdfs://localhost:9000/user/xiaojian/_Split_";
		Path path = new Path(dirpath);
		Configuration conf = new Configuration();
		FileSystem fs;
		try {
			fs = FileSystem.get(URI.create(dirpath),conf);
			ArrayList<FileStatus> status = new ArrayList<FileStatus>(Arrays.asList(fs.listStatus(path)));
			//for(FileStatus item : status){
			FileStatus item=null;
		    while((!status.isEmpty())&&((item=status.remove(0))!=null)){
				if(item.isDir()){
					status.addAll(Arrays.asList(fs.listStatus(item.getPath())));
				}else{
					FileInfo fileinfo=new FileInfo();
					fileinfo.setUri(path2uri(item.getPath()));
					fileinfo.setSize(item.getLen());
					fileinfo.setName(item.getPath().getName());
					files.add(fileinfo);
				}
			}
			return files;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	public static List<FileInfo> listFrameFiles(){
		ArrayList<FileInfo> files = new ArrayList<FileInfo>();
		String dirpath="hdfs://localhost:9000/user/xiaojian/Frames";
		Path path = new Path(dirpath);
		Configuration conf = new Configuration();
		FileSystem fs;
		try {
			fs = FileSystem.get(URI.create(dirpath),conf);
			ArrayList<FileStatus> status = new ArrayList<FileStatus>(Arrays.asList(fs.listStatus(path)));
			//for(FileStatus item : status){
			FileStatus item=null;
		    while((!status.isEmpty())&&((item=status.remove(0))!=null)){
				if(item.isDir()){
					status.addAll(Arrays.asList(fs.listStatus(item.getPath())));
				}else{
					FileInfo fileinfo=new FileInfo();
					fileinfo.setUri(path2uri(item.getPath()));
					fileinfo.setSize(item.getLen());
					fileinfo.setName(item.getPath().getName()+".");
					fileinfo.setLight(getLight(item.getPath().toString()));
					files.add(fileinfo);
				}
			}
			return files;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	public static List<FileInfo> listConvertFiles(){
		ArrayList<FileInfo> files = new ArrayList<FileInfo>();
		String dirpath="hdfs://localhost:9000/user/xiaojian/Convert";
		Path path = new Path(dirpath);
		Configuration conf = new Configuration();
		FileSystem fs;
		try {
			fs = FileSystem.get(URI.create(dirpath),conf);
			ArrayList<FileStatus> status = new ArrayList<FileStatus>(Arrays.asList(fs.listStatus(path)));
			//for(FileStatus item : status){
			FileStatus item=null;
		    while((!status.isEmpty())&&((item=status.remove(0))!=null)){
				if(item.isDir()){
					status.addAll(Arrays.asList(fs.listStatus(item.getPath())));
				}else{
					FileInfo fileinfo=new FileInfo();
					fileinfo.setUri(path2uri(item.getPath()));
					fileinfo.setSize(item.getLen());
					fileinfo.setName(item.getPath().getName());
					files.add(fileinfo);
				}
			}
			return files;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	private static String getLight(String frame){
//		System.out.println(frame);
		if(lights == null ){
			lights=new HashMap<String,String>();
			String path = "hdfs://localhost:9000/user/xiaojian/Lights";
			Configuration conf = new Configuration();
			try {
				FileSystem fs = FileSystem.get(URI.create(path), conf);
				FSDataInputStream in = null;
				in = fs.open(new Path(path));
				String line=null;
				while((line=in.readLine())!=null){
					String key=line.substring(0,line.lastIndexOf(":"));
					String value=line.substring(line.lastIndexOf(":")+1);
					lights.put(key, value);
				}
				return lights.get(frame);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "0.0";
			}
		}else{
			return lights.get(frame);
		}
	}
	
	
	public static InputStream getLightStream() {
		String path = "hdfs://localhost:9000/user/xiaojian/Lights";
		Configuration conf = new Configuration();
		try {
			FileSystem fs = FileSystem.get(URI.create(path), conf);
			InputStream in = null;
			in = fs.open(new Path(path));
			return in;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public static String[] getAllPart(String file){
		  System.out.println(file);
		  Path path=new Path(DIR+file);
		  Configuration conf=new Configuration();
		  try {
			FileSystem fs=FileSystem.get(URI.create(path.toString()),conf);
			FileStatus[] status=fs.listStatus(path.getParent());
			String[] files=new String[status.length];
			for(int i=0;i<status.length;i++){
				files[i]=path2uri(status[i].getPath());
			}
			return files;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		  
	}
	
	private static String path2uri(Path p){
		String path=p.toString();
		return path.substring(DIR.length());
	}
	
	
	public static boolean runMapRedJob(String file){
		Process pcs;
		int pos=file.lastIndexOf("/");
		String basename;
		String dir="";
		if(pos==-1){
			basename=file.substring(0, file.lastIndexOf("."));
		}else{
			basename=file.substring(pos+1, file.lastIndexOf("."));
			dir=file.substring(0, pos);
		}
		
		System.out.println("Hadoop:"+file);
		try {
			pcs = Runtime.getRuntime().exec(
					new String[] { "/usr/VideoMapRed/runHadoop.sh" ,DIR+dir,RESULT+dir});
			int result=pcs.waitFor();
			if(result==0)
				return true;
			else
				return false;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static boolean getKeyFrame(String file){
		Process pcs;
		int pos=file.lastIndexOf("/");
		String basename;
		String dir="";
		if(pos==-1){
			basename=file.substring(0, file.lastIndexOf("."));
		}else{
			basename=file.substring(pos+1, file.lastIndexOf("."));
			dir=file.substring(0, pos);
		}
		
		System.out.println("Hadoop:"+file);
		System.out.println(basename);
		System.out.println(SPLITS+basename);
		
		try {
			pcs = Runtime.getRuntime().exec(
					new String[] { "/usr/VideoMapRed/runMPKeyFrame.sh" ,SPLITS+basename,"hdfs://localhost:9000/user/xiaojian/Result/"+basename});
			int result=pcs.waitFor();
			if(result==0)
				return true;
			else
				return false;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
