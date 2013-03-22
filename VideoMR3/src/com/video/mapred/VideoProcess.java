package com.video.mapred;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.Path;


public class VideoProcess {

	private final int clipLength=1;
	private String rawfile;
	private String basename;
	private String format;
	private long videoLength;
	
	private int numOfClips;
	private int currentClip;
	
	private ArrayList<byte[]> outputClipsByteArray=new ArrayList<byte[]>();
	private ArrayList<String> outputClipsNameArray=new ArrayList<String>();
	
	public VideoProcess(Path file){
		rawfile=file.toString();
//		rawfile=file;
		basename=rawfile.substring(0, rawfile.lastIndexOf("."));
		format=rawfile.substring(rawfile.lastIndexOf("."));
		
		rawfile=VideoUtil.formatConvert(rawfile);
		format=".webm";
		rawfile=VideoUtil.standardize(rawfile);
		videoLength=getLength();
		
		divideVideo();
		numOfClips = outputClipsByteArray.size();
		currentClip = 0;
		
	}
	
	public float getProcess(){
		return Math.min(1.0f, (currentClip)/(float)(numOfClips - currentClip));
	}
	
	public int getNumOfClips(){
		return numOfClips;
	}
	
	private long getLength(){
		System.out.println("in function getLength() of file "+rawfile);
		return VideoUtil.getLength(rawfile);
	}
	
	
	
	public void divideVideo(){
		 
			System.out.println("try to divide the video:   " + rawfile);
			
			for (long count = 0, i = 0; count <= videoLength; count += clipLength, i++) {
				// generate output name
				String output = rawfile.substring(0, rawfile.lastIndexOf("."))
						+ "_"+i + rawfile.substring(rawfile.lastIndexOf("."));

				// generate args
				List<String> command = new ArrayList<String>();
				command.add(VideoUtil.FFMPEG);
				command.add("-i");
				command.add(rawfile);
				command.add("-y");
				command.add("-sameq");
				command.add("-ss");
				command.add(VideoUtil.formatTime(count));
				command.add("-t");
				command.add("1");
				command.add(output);

				try {
					ProcessBuilder builder = new ProcessBuilder();
					builder.command(command);
					builder.start();
					Process p = builder.start();
					VideoUtil.doWaitFor(p);
					p.destroy();

					System.out.println("In process");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				FileInputStream file_input;
				try {
					file_input = new FileInputStream(output);
					byte[] temp = new byte[file_input.available()];
					file_input.read(temp);
					outputClipsByteArray.add(temp);
					outputClipsNameArray.add(basename+format);
					System.out.println(basename+format);
					file_input.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/**
				 * test
				 */
				System.out.println("divide ok:   " + output);
			}
	}
	
	public void cleanUp(){
		outputClipsByteArray=null;
		String path="/usr/VideoMapRed/tmp/";                
		File[] files  = (new File(path)).listFiles();  
		for(int i=0;i<files.length;i++){               
		files[i].delete();                         
		}    
	}
	
	public byte[] getNextClip(long index){
		return outputClipsByteArray.get((int)index);
	}
	
	public String getNextName(long index){
		return outputClipsNameArray.get((int)index);
	}
	
	
	public static void main(String[] args){
//		System.out.println(formatTime(6573));
//		new VideoProcess("/home/xiaojian/Videos/test_01.flv");
	}
	
	
	
}
