package com.video.web;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class SearchServlet
 */
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String fileUploadPath = "/home/xiaojian/";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchServlet() {
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

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		double picLight = 0.0;
		ServletFileUpload uploadHandler = new ServletFileUpload(
				new DiskFileItemFactory());
		try {
			List<FileItem> items = uploadHandler.parseRequest(request);
			for (FileItem item : items) {
				if (!item.isFormField()) {
					File file = new File(fileUploadPath, item.getName());
					item.write(file);
					picLight = averageLight(fileUploadPath + item.getName());
					System.out.println("picLight: " + picLight);

				}
			}
			// String infoPath = "/home/xiaojian/lightofframe.txt";
			LightOfFrame topThree[] = new LightOfFrame[3];

			for (int i = 0; i < 3; i++) {
				topThree[i] = new LightOfFrame();
			}

			DataInputStream br = new DataInputStream(
					MovieUploader.getLightStream());
			String temp = "";
			for (int i = 0; i < 3; i++) {
				temp = br.readLine();
				if ((temp != null) && (temp.length()) > 0) {
					String key = temp.substring(0, temp.lastIndexOf(":"));
					String value = temp.substring(temp.lastIndexOf(":") + 1);
					topThree[i].setPath(key);
					topThree[i].setLight(Double.parseDouble(value));
					topThree[i].setLikehood(Math.abs(picLight
							- topThree[i].getLight()));
				}
			}
			// 对topThree排序
			for (int i = 0; i < 3; i++) {
				for (int j = i + 1; j < 3; j++) {
					if (topThree[i].getLikehood() > topThree[j].getLikehood()) {
						String temppath = topThree[j].getPath();
						double templh = topThree[j].getLikehood();
						topThree[j].setPath(topThree[i].getPath());
						topThree[j].setLikehood(topThree[i].getLikehood());
						topThree[i].setPath(temppath);
						topThree[i].setLikehood(templh);
					}
				}
			}
			while (((temp = br.readLine()) != null) && (temp.length()) > 0) {
//				System.out.println(temp);
				String path = temp.substring(0, temp.lastIndexOf(":"));
				String light = temp.substring(temp.lastIndexOf(":") + 1);

				double likehood = Math
						.abs(picLight - Double.parseDouble(light));
				int location = -1;
				for (int i = 0; i < 3; i++) {
					if (topThree[i].getLikehood() > likehood) {
						location = i;
						break;
					}
				}
				if (location == 0) {
					topThree[2].setPath(topThree[1].getPath());
					topThree[2].setLikehood(topThree[1].getLikehood());
					topThree[2].setLight( topThree[1].getLight());
					topThree[1].setPath(topThree[0].getPath());
					topThree[1].setLikehood(topThree[0].getLikehood());
					topThree[1].setLight( topThree[0].getLight());
					topThree[0].setPath(path);
					topThree[0].setLight( Double.parseDouble(light));
					topThree[0].setLikehood(likehood);
				}
				if (location == 1) {
					topThree[2].setPath(topThree[1].getPath());
					topThree[2].setLikehood(topThree[1].getLikehood());
					topThree[2].setLight( topThree[1].getLight());
					topThree[1].setPath(path);
					topThree[1].setLikehood(likehood);
					topThree[1].setLight( Double.parseDouble(light));
				}
				if (location == 2) {
					topThree[2].setPath(path);
					topThree[2].setLikehood(likehood);
					topThree[2].setLight( Double.parseDouble(light));
				}
			}
			
			PrintWriter writer = response.getWriter();
			response.setContentType("application/json");
			JSONArray json = new JSONArray();
			for (LightOfFrame item : topThree) {
				System.out.println(item.getPath()+":"+item.getLight()+":" +item.getLikehood());
				JSONObject jsono = new JSONObject();
				try {
					jsono.put(
							"name",
							item.getPath().substring(
									item.getPath().lastIndexOf("/") + 1));
					jsono.put("light", "Light: " + item.getLight());
					jsono.put("likehood", "Likehood: " + item.getLikehood());
					String[] tmp = item.getPath().split("Frames");
					jsono.put("url", "frame?download=" + tmp[1]);
					json.put(jsono);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			writer.write(json.toString());
			writer.close();
			if (br != null)
				br.close();

		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public double averageLight(String name) {
		try {
			BufferedImage bi = ImageIO.read(new File(name));
			int height = bi.getHeight();
			int width = bi.getWidth();
			int[] allrgbs = new int[height * width];
			bi.getRGB(0, 0, width, height, allrgbs, 0, width);
			double light = 0.0;
			for (int pixel : allrgbs) {
				int[] rgbs = getSplitRGB(pixel);
				light += (0.299 * rgbs[0]) + (0.587 * rgbs[1])
						+ (0.114 * rgbs[2]);
			}
			double averageLight = light / (height * width);
			return averageLight;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0.0;
		}
	}

	private int[] getSplitRGB(int pixel) {
		int[] rgbs = new int[3];
		rgbs[0] = (pixel & 0xff0000) >> 16;
		rgbs[1] = (pixel & 0xff00) >> 8;
		rgbs[2] = (pixel & 0xff);
		// System.out.println("R:"+rgbs[0]+"  G:"+rgbs[1]+"  B:"+rgbs[2]);
		return rgbs;
	}

}
