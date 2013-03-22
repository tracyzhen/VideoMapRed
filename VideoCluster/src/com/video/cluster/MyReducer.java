package com.video.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MyReducer extends Reducer<Text, Text, Text, Text> {

	private ArrayList<Cluster> centerClusters = new ArrayList<Cluster>();
	private ArrayList<Cluster> groupClusters = new ArrayList<Cluster>();
	public static boolean isStable = false;
	private String outVal = "";

	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		groupClusters.clear();
		outVal = "";
		for (Text val : values) {
			outVal += val.toString() + "|";
			Cluster tmp = new Cluster(val.toString());
			groupClusters.add(tmp);
		}
		System.out.println("from reduce class reduce method size:"
				+ groupClusters.size());
		centerClusters.add(getMidPointInOneGroup(groupClusters));
		context.write(key, new Text(outVal));
		System.out.println("from reducer class key :" + key.toString()
				+ " value:" + outVal.toString());

	}

	private Cluster getMidPointInOneGroup(List<Cluster> group) {
		double delta = -1;
		int index = 0;
		for (int i = 0; i < group.size(); i++) {
			double tmpDeltaBrightness = 0;
			for (int k = 0; k < group.size(); k++) {
				tmpDeltaBrightness += Math.abs(group.get(i).getLight()
						- group.get(k).getLight());
			}
			if (delta == -1) {
				delta = tmpDeltaBrightness;
				index = i;
			} else {
				if (tmpDeltaBrightness < delta) {
					delta = tmpDeltaBrightness;
					index = i;
				}
			}
		}
		return group.get(index);

	}

	@Override
	protected void cleanup(org.apache.hadoop.mapreduce.Reducer.Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		super.cleanup(context);
		Configuration conf = context.getConfiguration();
		// Path outPath = new Path(conf.get(KMEANS_CENTER_KEY));
		Path outPath = new Path(ClusterJob.KMEANS_CENTER_PATH);
		FileSystem fs = outPath.getFileSystem(conf);
		new KMeansCluster().writeKMeansCenter(fs, outPath, conf, centerClusters);

	}

}
