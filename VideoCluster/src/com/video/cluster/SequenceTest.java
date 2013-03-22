package com.video.cluster;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;

public class SequenceTest {

	private static final String[] data = {
			"hdfs://localhost:9000/user/xiaojian/Frames/A/A_01_11_frame:115.739",
			"hdfs://localhost:9000/user/xiaojian/Frames/A/A_01_12_frame:120.628",
			"hdfs://localhost:9000/user/xiaojian/Frames/A/A_01_13_frame:125.67",
			"hdfs://localhost:9000/user/xiaojian/Frames/A/A_01_10_frame:136.092",
			"hdfs://localhost:9000/user/xiaojian/Frames/A/A_01_14_frame:129.0" };

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		// Write an Sequrence File

		// Configuration
		Configuration conf = new Configuration();

		// HDFS File Sytem
		FileSystem fs = FileSystem
				.get(URI.create("hdfs://localhost:9000/user/xiaojian/Cluster/centroid.txt"),
						conf);

		// Seq File Path
		Path path = new Path(
				"hdfs://localhost:9000/user/xiaojian/Cluster/centroid.txt");

		// Open Seq Writer and write all key-values
		SequenceFile.Writer writer = null;
		IntWritable key = new IntWritable();
		Text value = new Text();
		try {
			writer = SequenceFile.createWriter(fs, conf, path, key.getClass(),
					value.getClass());
			for (int i = 0; i < 5; i++) {
				key.set(i);
				value.set(SequenceTest.data[i % SequenceTest.data.length]);
				writer.append(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeStream(writer);
		}

		SequenceFile.Reader reader = null;

		try {

			// Get Reader
			reader = new SequenceFile.Reader(fs, path, conf);
			// Get Key/Value Class
			Writable key1 = (Writable) ReflectionUtils.newInstance(
					reader.getKeyClass(), conf);
			Writable value1 = (Writable) ReflectionUtils.newInstance(
					reader.getValueClass(), conf);
			// Read each key/value
			while (reader.next(key1, value1)) {
				System.out.println(key1 + "\t" + value1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeStream(reader);
		}

	}

}
