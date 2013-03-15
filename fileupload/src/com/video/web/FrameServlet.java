package com.video.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class FrameServlet
 */
public class FrameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FrameServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (request.getParameter("getfile") != null
				&& !request.getParameter("getfile").isEmpty()) {
			String file = request.getParameter("getfile");
			System.out.println(file);
			if (MovieUploader.getKeyFrame(file)) {
				PrintWriter writer = response.getWriter();
				writer.write("Success");
			} else {
				PrintWriter writer = response.getWriter();
				writer.write("Error");
			}
		} else if (request.getParameter("download") != null
				&& !request.getParameter("download").isEmpty()) {

			String file = request.getParameter("download");
			InputStream in = MovieUploader.downloadFrame(file);

			if (in != null) {
				int bytes = 0;
				ServletOutputStream op = response.getOutputStream();

				byte[] bbuf = new byte[1024];
				int len = 0;
				while ((in != null) && ((bytes = in.read(bbuf)) != -1)) {
					op.write(bbuf, 0, bytes);
					len += bytes;
				}

				response.setContentType("image/png");
				// response.setContentLength((int) file.length());
				response.setContentLength(len);
				response.setHeader("Content-Disposition", "inline; filename=\""
						+ file + "\"");
				in.close();
				op.flush();
				op.close();
			}
		} else {
			List<FileInfo> files = MovieUploader.listFrameFiles();
			PrintWriter writer = response.getWriter();
			response.setContentType("application/json");
			JSONArray json = new JSONArray();
			for (FileInfo item : files) {

				JSONObject jsono = new JSONObject();
				try {
					jsono.put("name", item.getName());
					jsono.put("size", item.getSize());
					jsono.put("light", "light:" + item.getLight());
					jsono.put("url", "frame?download=" + item.getUri());
					json.put(jsono);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			writer.write(json.toString());
			writer.close();

		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
