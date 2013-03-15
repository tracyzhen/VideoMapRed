package com.video.web;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.imgscalr.Scalr;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadServlet extends HttpServlet {

//    private static final long serialVersionUID = 1L;
    private File fileUploadPath;

    @Override
    public void init(ServletConfig config) {
        fileUploadPath = new File(this.getClass().getResource("/").getPath() +"../.."+config.getInitParameter("upload_path"));
       // System.out.println(this.getClass().getResource("/").getPath() +"../.."+config.getInitParameter("upload_path"));
    }
        
    /**
        * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
        * 
        */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	
        System.out.println("GET");
        if (request.getParameter("getfile") != null 
                && !request.getParameter("getfile").isEmpty()) {
            //File file = new File(fileUploadPath,request.getParameter("getfile"));
        	System.out.println("getfile");
        	String file=request.getParameter("getfile");
        	InputStream in=MovieUploader.download(file);
        	
        	if(in!=null){
        		int bytes=0;
        		ServletOutputStream op=response.getOutputStream();
        		
        		byte[] bbuf=new byte[1024];
        		int len=0;
        		while((in!=null)&&((bytes =in.read(bbuf))!=-1)){
        			op.write(bbuf, 0, bytes);
        			len+=bytes;
        		}
        		
        		response.setContentType(getMimeType(file));
               // response.setContentLength((int) file.length());
        		response.setContentLength(len);
                response.setHeader( "Content-Disposition", "inline; filename=\"" + file + "\"" );
        		in.close();
                op.flush();
                op.close();
        	}
        	/*
            if (file.exists()) {
                int bytes = 0;
                ServletOutputStream op = response.getOutputStream();

                response.setContentType(getMimeType(file));
                response.setContentLength((int) file.length());
                response.setHeader( "Content-Disposition", "inline; filename=\"" + file.getName() + "\"" );

                byte[] bbuf = new byte[1024];
                DataInputStream in = new DataInputStream(new FileInputStream(file));

                while ((in != null) && ((bytes = in.read(bbuf)) != -1)) {
                    op.write(bbuf, 0, bytes);
                }
                */

             
            
        } else if (request.getParameter("playlist") != null 
                && !request.getParameter("playlist").isEmpty()) {
        	System.out.println("getlist");
        	String file=request.getParameter("playlist");
        	String[] list=MovieUploader.getAllPart(file);
        	
        	PrintWriter writer = response.getWriter();
			response.setContentType("application/json");
			JSONArray json = new JSONArray();
			for (String item : list) {

				JSONObject jsono = new JSONObject();
				try {
					jsono.put("uri",item);
					json.put(jsono);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			writer.write(json.toString());
			writer.close();
        	
        	
        	
        }else if (request.getParameter("delfile") != null && !request.getParameter("delfile").isEmpty()) {
            File file = new File(fileUploadPath, request.getParameter("delfile"));
            if (file.exists()) {
                file.delete(); // TODO:check and report success
            } 
        } else if (request.getParameter("getthumb") != null && !request.getParameter("getthumb").isEmpty()) {
            File file = new File(fileUploadPath, request.getParameter("getthumb"));
                if (file.exists()) {
                    String mimetype = getMimeType(file.getName());
                    if (mimetype.endsWith("png") || mimetype.endsWith("jpeg") || mimetype.endsWith("gif")) {
                        BufferedImage im = ImageIO.read(file);
                        if (im != null) {
                            BufferedImage thumb = Scalr.resize(im, 75); 
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            if (mimetype.endsWith("png")) {
                                ImageIO.write(thumb, "PNG" , os);
                                response.setContentType("image/png");
                            } else if (mimetype.endsWith("jpeg")) {
                                ImageIO.write(thumb, "jpg" , os);
                                response.setContentType("image/jpeg");
                            } else {
                                ImageIO.write(thumb, "GIF" , os);
                                response.setContentType("image/gif");
                            }
                            ServletOutputStream srvos = response.getOutputStream();
                            response.setContentLength(os.size());
                            response.setHeader( "Content-Disposition", "inline; filename=\"" + file.getName() + "\"" );
                            os.writeTo(srvos);
                            srvos.flush();
                            srvos.close();
                        }
                    }
            } // TODO: check and report success
        } else {
        	
        	//File[] files = fileUploadPath.listFiles();
        	List<FileInfo> files=MovieUploader.listFiles();
			PrintWriter writer = response.getWriter();
			response.setContentType("application/json");
			JSONArray json = new JSONArray();
			for (FileInfo item : files) {

				JSONObject jsono = new JSONObject();
				try {
					jsono.put("name", item.getName());
					jsono.put("size", item.getSize());
//					 new FileInputStream(item).available()
					jsono.put("url", "upload?getfile=" + item.getUri());
					jsono.put("cuturi", "cut?getfile="+item.getUri());
					jsono.put("frameuri", "frame?getfile="+item.getUri());
					jsono.put("first", item.getIsfirstpart());
					jsono.put("listuri","upload?playlist="+item.getUri() );
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

        	
        	/*
            PrintWriter writer = response.getWriter();
            writer.write("call POST with multipart form data");
            */
        }
    }
    
    /**
        * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
        * 
        */
    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
        }

        ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
        PrintWriter writer = response.getWriter();
        response.setContentType("application/json");
        JSONArray json = new JSONArray();
        try {
            List<FileItem> items = uploadHandler.parseRequest(request);
            String dirname="";
            for (FileItem item : items) {
                if (!item.isFormField()) {
                        File file = new File(fileUploadPath, item.getName());
                        item.write(file);
                        dirname=hash(item.getName());
                        JSONObject jsono = new JSONObject();
                        jsono.put("name", item.getName());
                        jsono.put("size", item.getSize());
                        jsono.put("url", "upload?getfile=" + dirname+"/"+item.getName());
                        jsono.put("cuturi", "cut?getfile="+ dirname+"/"+item.getName());
                        if(isFirstfile(item.getName())){
                        	jsono.put("first", 1);
                        	jsono.put("listuri","upload?playlist="+dirname+"/"+item.getName() );
                        }else
                        	jsono.put("first", 0);
                       // jsono.put("thumbnail_url", "upload?getthumb=" + item.getName());
                        //jsono.put("delete_url", "upload?delfile=" + item.getName());
                        //jsono.put("delete_type", "GET");
                        json.put(jsono);
                        if(MovieUploader.upload(dirname,file)){
                        	file.delete();
                        }
                        
                }
            }
        } catch (FileUploadException e) {
                throw new RuntimeException(e);
        } catch (Exception e) {
                throw new RuntimeException(e);
        } finally {
            writer.write(json.toString());
            writer.close();
        }

    }

    private String hash(String filename){
    	int pos=filename.lastIndexOf("_");
    	return filename.substring(0, pos);
    }
    
    //�ж��ǲ��ǵ�Ӱ�ײ�Ƭ��
    private boolean isFirstfile(String name){
		//file_01.webm
		int end=name.lastIndexOf(".");
		int begin=name.lastIndexOf("_");
		if((name.subSequence(begin+1, end).equals("01")) || (name.subSequence(begin+1, end).equals("1")))
			return true;
		else
			return false;
	}
    
    private String getMimeType(String file) {
        String mimetype = "";
      //  if (file.exists()) {
//            URLConnection uc = new URL("file://" + file.getAbsolutePath()).openConnection();
//            String mimetype = uc.getContentType();
//            MimetypesFIleTypeMap gives PNG as application/octet-stream, but it seems so does URLConnection
//            have to make dirty workaround
            if (getSuffix(file).equalsIgnoreCase("png")) {
                mimetype = "image/png";
            } else {
                javax.activation.MimetypesFileTypeMap mtMap = new javax.activation.MimetypesFileTypeMap();
                mimetype  = mtMap.getContentType(file);
            }
       // }
        System.out.println("mimetype: " + mimetype);
        return mimetype;
    }



    private String getSuffix(String filename) {
        String suffix = "";
        int pos = filename.lastIndexOf('.');
        if (pos > 0 && pos < filename.length() - 1) {
            suffix = filename.substring(pos + 1);
        }
        System.out.println("suffix: " + suffix);
        return suffix;
    }
}