package com.video.ffmpeg;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public class VideoFFmpeg {

	private String initime = "00:00:00";

	public String[] split(String videoName) {

		Process pcs;
		String duration;
		// String filename="/home/xiaojian/input.webm";
		String outputfile;
		String[] results = null;

		try {
			// pcs = Runtime.getRuntime().exec(new
			// String[]{"/home/xiaojian/split.sh",filename,outputfile});
			pcs = Runtime.getRuntime().exec(
					new String[] { "/usr/VideoMapRed/Duration.sh", videoName });
			InputStreamReader ir = new InputStreamReader(pcs.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line = null;
			if ((line = input.readLine()) != null) {
				duration = line;
				System.out.println(line);
				String[] timeStr = duration.split(":");

				int time = (int) (Integer.parseInt(timeStr[0]) * 3600
						+ Integer.parseInt(timeStr[1]) * 60 + Double
						.parseDouble(timeStr[2]));
				String stime = initime;
				String etime;
				System.out.println(time);
				results = new String[time];
				for (int i = 0; i < time; i++) {
					// stime = "00:00:" + i;
					// etime = "00:00:" + (i + 1);
					etime = getNextTime(stime);

					// System.out.println(stime+"--"+etime);

					outputfile = videoName.substring(0,
							videoName.lastIndexOf("."))
							+ "_"
							+ (i + 1)
							+ videoName.substring(videoName.lastIndexOf("."));
					pcs = Runtime.getRuntime().exec(
							new String[] { "/usr/VideoMapRed/split.sh",
									videoName, outputfile, stime, etime });
					results[i] = outputfile;
					stime = etime;
				}
			}
			if (null != input) {
				input.close();
			}
			if (null != ir) {
				ir.close();
			}
			pcs.waitFor();
			return results;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getNextTime(String stime) {
		DateFormat format = new SimpleDateFormat("HH:mm:ss");
		Date date = null;

		try {
			date = format.parse(stime);
			date.setSeconds(date.getSeconds() + 1);
			stime = format.format(date);
			return stime;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getKeyFrame(String videoName) {
		// System.out.println(videoName);
		String keyframe = videoName.substring(0, videoName.lastIndexOf("."))
				+ "_frame";
		// System.out.println(keyframe);
		double duration = 0.0;
		Process pcs;
		try {

			pcs = Runtime.getRuntime().exec(
					new String[] { "/usr/VideoMapRed/Duration.sh", videoName });
			InputStreamReader ir = new InputStreamReader(pcs.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line = null;
			String tmp;
			if ((line = input.readLine()) != null) {
				tmp = line;
				// System.out.println(line);
				String[] timeStr = tmp.split(":");

				duration = (Double.parseDouble(timeStr[0]) * 3600
						+ Double.parseDouble(timeStr[1]) * 60 + Double
						.parseDouble(timeStr[2]));
				String half = (duration / 2.0) + "";
				pcs = Runtime.getRuntime().exec(
						new String[] { "/usr/VideoMapRed/keyframe.sh",
								videoName, keyframe, half });
			}
			int res = pcs.waitFor();
			if (res == 0)
				return keyframe;
			else
				return null;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public double averageLight(String name) {
		try {
			BufferedImage bi = ImageIO.read(new File(name));
			int height = bi.getHeight();
			int width = bi.getWidth();
			int[] allrgbs = new int[height * width];
			bi.getRGB(0, 0, width, height, allrgbs, 0, width);
			double light=0.0;
			for(int pixel:allrgbs){
				int[] rgbs=getSplitRGB(pixel);
				light+=(0.299*rgbs[0])+(0.587*rgbs[1])+(0.114*rgbs[2]);
			}
			double averageLight=light/(height * width);
			return averageLight;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0.0;
		}
	}

	private  int[] getSplitRGB(int pixel) {
		int[] rgbs = new int[3];
		rgbs[0] = (pixel & 0xff0000) >> 16;
		rgbs[1] = (pixel & 0xff00) >> 8;
		rgbs[2] = (pixel & 0xff);
		//System.out.println("R:"+rgbs[0]+"  G:"+rgbs[1]+"  B:"+rgbs[2]);
		return rgbs;
	}

	public static void main(String[] args) {
//		System.out.println(new VideoFFmpeg()
//				.getKeyFrame("/usr/VideoMapRed/tmp/test_01_0_1_frame"));
		System.out.println(new VideoFFmpeg().averageLight("/usr/VideoMapRed/tmp/test_01_0_23_frame"));
	}
}
