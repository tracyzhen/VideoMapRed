package com.video.mapred;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class VideoUtil {
//	public static final String FFMPEG = "/usr/bin/ffmpeg";
	public static final String FFMPEG = "ffmpeg";

	public static long getLength(String file) {
		String length = null;
		Runtime run = Runtime.getRuntime();
		Process p;
		try {
			p = run.exec(new String[] { "/usr/VideoMapRed/Duration.sh", file});
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String s;
			s = br.readLine();
			if (s != null)
				length = s;
			p.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("here is the length of the rawfile "+length);
		String[] timeStr=length.split(":");
		long time = (long)(Integer.parseInt(timeStr[0]) * 3600
				+ Integer.parseInt(timeStr[1]) * 60 + Double
				.parseDouble(timeStr[2]));
//		long result = Long.parseLong(time);
		System.out.println(time);
		return time;
	}
	
	/**
	 * test util
	 * @param args
	 */
	public static void main(String[] args){
//		System.out.println(formatTime(6573));
		System.out.println("the length is "+getLength("/home/xiaojian/Videos/test_01.webm"));
		System.out.println(formatConvert("/home/xiaojian/Videos/test_01.flv"));
//		System.out.println(formatTime(60));
	}
	
	public static String formatConvert(String file){
		Runtime run = Runtime.getRuntime();
		Process p;
		String outputfile=file.substring(0,file.lastIndexOf("."))+".webm";
		try {
			p = run.exec(new String[] { "/usr/VideoMapRed/format.sh", file,outputfile});
			p.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File src=new File(outputfile);
		String dst = "hdfs://localhost:9000/user/xiaojian/Convert/"
		+ src.getName().substring(0, src.getName().lastIndexOf("."))+"/"+src.getName();
		InputStream in=null;
//		System.out.println(dst);
		try {
//			InputStream in = new BufferedInputStream(new FileInputStream(file));
			 in=new FileInputStream(src);
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
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputfile;
		
	}
	
	

	public static String standardize(String raw) {
		
		/**
		 * test
		 */
		System.out.println("standardize:   " + raw);
		//generate output name
		String output = raw.substring(0, raw.lastIndexOf("."))
				+ "_standarlized" + raw.substring(raw.lastIndexOf("."));
		
		//generate command
		List<String> command = new ArrayList<String>();
		command.add(FFMPEG);
		command.add("-i");
		command.add(raw);
		command.add("-sameq");
		command.add("-y");
		command.add("-r");
		command.add("30");
		command.add("-g");
		command.add("300");
		command.add(output);
		
		try{
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(command);
			builder.start();
			Process p = builder.start();
			doWaitFor(p);
			p.destroy();

			System.out.println("In process");

			return output;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	
	
	public static String formatTime(long rawTime){
		int hh = 0, mm = 0, ss = 0;
		String hh_s,mm_s,ss_s;
		String result;
		
		hh += rawTime / 3600;
		int mm_remain = (int) (rawTime % 3600);
		mm += mm_remain / 60;
		ss += mm_remain % 60;
		
		if (hh >= 10){
			hh_s = Integer.toString(hh);
		}else{
			hh_s = "0" + Integer.toString(hh);
		}
		if (mm >= 10){
			mm_s = Integer.toString(mm);
		}else{
			mm_s = "0" + Integer.toString(mm);
		}
		if (ss >= 10){
			ss_s = Integer.toString(ss);
		}else{
			ss_s = "0" + Integer.toString(ss);
		}
		
		result = hh_s + ":" + mm_s + ":" + ss_s;
		return result;
	}
	
	public static class SubThread extends Thread{
		public void run(){
			
		}
	}
	
	public static int doWaitFor(Process p) {
        InputStream in = null;
        InputStream err = null;
        int exitValue = -1; // returned to caller when p is finished
        try {
            System.out.println("coming");
            in = p.getInputStream();
            err = p.getErrorStream();
            boolean finished = false; // Set to true when p is finished
            while (!finished) {
                try {
                    while (in.available() > 0) {
                        //Character c = new Character((char) in.read());
                    	in.read();
                        //System.out.print(c);
                    }
                    while (err.available() > 0) {
                        //Character c = new Character((char) err.read());
                    	err.read();
                        //System.out.print(c);
                    }
                    exitValue = p.exitValue();
                    finished = true;
                } catch (IllegalThreadStateException e) {
                    Thread.currentThread();
					Thread.sleep(500);
                }
            }
        } catch (Exception e) {
            System.err.println("doWaitFor();: unexpected exception - "
                    + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            if (err != null) {
                try {
                    err.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return exitValue;
    }

}

