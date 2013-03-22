package com.video.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

public class KMeansCluster {

	public static ArrayList<Cluster> kcenter = new ArrayList<Cluster>();
	private static final int K = 5;
	private static SequenceFile.Reader reader;
	private static SequenceFile.Writer writer;

	public Cluster calCenter(String keyframe) {
		Cluster cl = new Cluster(keyframe);
		for (Cluster center : kcenter) {
			double dist = Math.abs(center.getLight() - cl.getLight());
			if (dist < cl.getDistance()) {
				this.updateDistance(cl, center, dist);
			}

		}
		return cl;
	}

	public void updateDistance(Cluster current, Cluster center, double distance) {
		current.setDistance(distance);
		current.setCenter(center);
	}

	public void loadKMeansCenter(FileSystem fs, Path path, Configuration conf,
			ArrayList<Cluster> KMeansCenter) {
		try {
//			System.out.println("kCenter size: "+ KMeansCenter.size());
			KMeansCenter.clear();
			reader = new SequenceFile.Reader(fs, path, conf);
			IntWritable key = new IntWritable();
			Text value = new Text();
			while (reader.next(key, value)) {
				// System.out.println(key + "\t" + value);
				Cluster cl = new Cluster(value.toString());
				cl.setDistance(0.0);
				cl.setCenter(cl);
				KMeansCenter.add(cl);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void writeKMeansCenter(FileSystem fs, Path path, Configuration conf,
			ArrayList<Cluster> tmpKMeansCenter) {

		try {
			if (fs.exists(path)) {
				ArrayList<Cluster> kMeansCenter = new ArrayList<Cluster>();
				loadKMeansCenter(fs, path, conf, kMeansCenter);
				if (unchanged(kMeansCenter, tmpKMeansCenter) == false)
					fs.delete(path, true);
				Path center = new Path(ClusterJob.KMEANS_CENTER_PATH);
				IntWritable key = new IntWritable();
				writer = new SequenceFile.Writer(fs, conf, center,
						key.getClass(), Text.class);
				for (int i = 0; i < tmpKMeansCenter.size(); i++) {
					String newCenter = tmpKMeansCenter.get(i).getKeyframe()
							+ ":" + tmpKMeansCenter.get(i).getLight();
					key.set(i);
					writer.append(key, new Text(newCenter));
					System.out.println("write center--->" + newCenter);

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private boolean unchanged(ArrayList<Cluster> middlePoints,
			ArrayList<Cluster> tmpMidPoints) {
		int total = middlePoints.size();
		int counter = 0;
		for (int i = 0; i < middlePoints.size(); i++) {
			for (int k = 0; k < tmpMidPoints.size(); k++) {
				if (middlePoints.get(i).getKeyframe()
						.equals(tmpMidPoints.get(k).getKeyframe())) {
					counter++;
					continue;
				}
			}
		}
		if (total == counter)
			MyReducer.isStable = true;
		else
			MyReducer.isStable = false;
		return MyReducer.isStable;
	}

	public static void main(String[] args) throws IOException {
		Configuration conf = new Configuration();
		Path path = new Path(
				"hdfs://localhost:9000/user/xiaojian/Cluster/centroid.txt");
		FileSystem fs = path.getFileSystem(conf);
		// new KMeansCluster().loadKMeansCenter(fs, path, conf);

	}

}
