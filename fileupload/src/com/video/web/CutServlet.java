package com.video.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CutServlet extends HttpServlet {
	
	
	 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	          System.out.println("hi");
	          if (request.getParameter("getfile") != null 
	                  && !request.getParameter("getfile").isEmpty()) {
	        	  String file=request.getParameter("getfile");
	        	  System.out.println(file);
	        	  if(MovieUploader.runMapRedJob(file)){
	        		  PrintWriter writer = response.getWriter();
	                  writer.write("Success");
	        	  }else{
	        		  PrintWriter writer = response.getWriter();
	                  writer.write("Error");
	        	  }
	          }else{
	        	  List<FileInfo> files=MovieUploader.listsplitFiles();
		  			PrintWriter writer = response.getWriter();
		  			response.setContentType("application/json");
		  			JSONArray json = new JSONArray();
		  			for (FileInfo item : files) {

		  				JSONObject jsono = new JSONObject();
		  				try {
		  					jsono.put("name", item.getName());
		  					jsono.put("size", item.getSize());
//		  					 new FileInputStream(item).available()
		  					jsono.put("url", "upload?getfile=" + item.getUri());
//		  					jsono.put("cuturi", "cut?getfile="+item.getUri());
//		  					jsono.put("first", item.getIsfirstpart());
//		  					jsono.put("listuri","upload?playlist="+item.getUri() );
		  					/*jsono.put("thumbnail_url",
		  							"upload?getthumb=" + item.getName());
		  					jsono.put("delete_url", "upload?delfile=" + item.getName());
		  					jsono.put("delete_type", "GET");
		  					*/
		  					json.put(jsono);
		  				} catch (JSONException e) {
		  					e.printStackTrace();
		  				}

		  			}
		  			writer.write(json.toString());
		  			writer.close();
        	  
	          }
	 }
}
