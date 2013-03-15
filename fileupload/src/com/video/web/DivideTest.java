package com.video.web;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class DivideTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DivideTest d = new DivideTest();
		d.test();
	}

	public void test(){
//		String path = "/home/xiaojian/";
		
//		String cmd = "";
//		Runtime.getRuntime.exec("/opt/usr/..../aaa.sh").waitFor();
		Process pcs;
		String duration;
		String filename="/home/xiaojian/input.webm";
		String outputfile;
		try {
		//	pcs = Runtime.getRuntime().exec(new String[]{"/home/xiaojian/split.sh",filename,outputfile});
			pcs = Runtime.getRuntime().exec(new String[]{"/home/xiaojian/Duration.sh",filename});
		
		
		
		InputStreamReader ir = new InputStreamReader(pcs.getInputStream());
		LineNumberReader input = new LineNumberReader(ir);
		String line = null;
		while((line = input.readLine())!=null){
			duration=line;
			System.out.println(line);
			String[] timeStr = duration.split(":");
			
			int time = (int) (Integer.parseInt(timeStr[0])*3600+Integer.parseInt(timeStr[1])*60+Double.parseDouble(timeStr[2]));
			String stime;
			String etime;
			System.out.println(time);
		for(int i=0;i<time;i++){
			stime= "00:00:"+i;
			etime= "00:00:"+(i+1);
//			stime= "00:00:20";
//			etime= "00:00:30";
			System.out.println(stime+"   "+etime);
				outputfile = filename.substring(0, filename.lastIndexOf("."))+"_0"+(i+1)+filename.substring(filename.lastIndexOf("."));
			pcs = Runtime.getRuntime().exec(new String[]{"/home/xiaojian/split.sh",filename,outputfile,stime,etime});
		}
		}
		if(null!=input){
			input.close();
		}
		if(null!=ir){
			ir.close();
		}
		int extValue = pcs.waitFor();
		
		
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

