package com.video.ffmpeg;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
				// System.out.println(line);
				String[] timeStr = duration.split(":");

				int time = (int) (Integer.parseInt(timeStr[0]) * 3600
						+ Integer.parseInt(timeStr[1]) * 60 + Double
						.parseDouble(timeStr[2]));
				String stime = initime;
				String etime;
				// System.out.println(time);
				results = new String[time];
				for (int i = 0; i < time; i++) {
					// stime = "00:00:" + i;
					// etime = "00:00:" + (i + 1);
					
					etime="1";
//                    System.out.println(stime);
					outputfile = videoName.substring(0,
							videoName.lastIndexOf("."))
							+ "_"
							+ (i + 1)
							+ videoName.substring(videoName.lastIndexOf("."));
					pcs = Runtime.getRuntime().exec(
							new String[] { "/usr/VideoMapRed/split_.sh",
									videoName, outputfile, stime, etime });
					results[i] = outputfile;
					stime = getNextTime(stime);
					pcs.waitFor();
				}
			}
			if (null != input) {
				input.close();
			}
			if (null != ir) {
				ir.close();
			}

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

	public static void main(String[] args) {
		new VideoFFmpeg().split("/usr/VideoMapRed/tmp/input.webm");
	}

}
