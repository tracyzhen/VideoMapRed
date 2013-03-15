package com.video.web;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String infoPath ="/home/xiaojian/lightofframe.txt";
		LightOfFrame topThree[] =new LightOfFrame[3];
		double picLight=124.0;
		
		for(int i=0;i<3;i++){
			topThree[i]=new LightOfFrame();
		}
		try {
			BufferedReader br=new BufferedReader(new FileReader(infoPath));
			String temp="";
			 for(int i=0;i<3;i++){
				 temp =br.readLine();
			    if((temp!= null)&&(temp.length() )> 0) {
			    	String[] lightInfo = temp.split("_");
			    
			    	 topThree[i].setPath(lightInfo[0]);
			    	 topThree[i].setLight(Double.parseDouble(lightInfo[1]));
			    	 topThree[i].setLikehood(Math.abs(picLight-topThree[i].getLight()));
			     }
			     
			}
			 //对topThree排序
			 for(int i=0;i<3;i++){
				 for(int j=i+1;j<3;j++){
					 if(topThree[i].getLikehood()>topThree[j].getLikehood()){
						 String temppath = topThree[j].getPath();
						 double templh = topThree[j].getLikehood();
						 topThree[j].setPath(topThree[i].getPath());
						 topThree[j].setLikehood(topThree[i].getLikehood());
						 topThree[i].setPath(temppath);
						 topThree[i].setLikehood(templh);
					 }
				 }
			 }
			 
			// System.out.println(br.readLine());
			 while((( temp =br.readLine())!= null)&&(temp.length() )> 0) {
//				 temp =br.readLine();
//				 System.out.println(temp);
				 String path = temp.substring(0, temp.indexOf("_"));
				 String light = temp.substring(temp.indexOf("_")+1, temp.length());
//				 System.out.println(path);
//				 System.out.println(light);
				 
				 double likehood = Math.abs(picLight-Double.parseDouble(light));
//				 if(topThree[0].getLikehood()>likehood){
//					 topThree[0].setPath(path);
//					 topThree[0].setLikehood(likehood);
//					 
//				 }
				 int location=0;
				 for(int i=0;i<3;i++){
					 if(topThree[i].getLikehood()>likehood){
						 location = i;
//						 System.out.println(i);
						 break;
					 }
				 }
				 if(location==0){
					 topThree[2].setPath(topThree[1].getPath());
					 topThree[2].setLikehood(topThree[1].getLikehood());
					 topThree[1].setPath(topThree[0].getPath());
					 topThree[1].setLikehood(topThree[0].getLikehood());
					 topThree[0].setPath(path);
					 topThree[0].setLikehood(likehood);
				 }
				 if(location==1){
					 topThree[2].setPath(topThree[1].getPath());
					 topThree[2].setLikehood(topThree[1].getLikehood()); 
					 topThree[1].setPath(path);
					 topThree[1].setLikehood(likehood);
				 }
				 if(location==2){
					 topThree[2].setPath(path);
					 topThree[2].setLikehood(likehood);
				 }
				 
				 
				 
				 for(int j=0;j<3;j++){
					 System.out.println(topThree[j].getPath());
				 }
				 
				 
				 
				 
				 
				 
			 }
			if(br != null) br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
