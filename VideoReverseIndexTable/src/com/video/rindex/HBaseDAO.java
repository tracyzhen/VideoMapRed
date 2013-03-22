package com.video.rindex;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseDAO {

	static HBaseConfiguration cfg = null;
	static {
		Configuration HBASE_CONFIG = new Configuration();
		HBASE_CONFIG.set("hbase.zookeeper.quorum", "localhost");
		cfg = new HBaseConfiguration(HBASE_CONFIG);
	}

	/**
	 * @param args
	 */

	public void storeToHbase(FileSystem fs, Path path, Configuration conf) {
		try {
			this.creatTable("videoRindex");
			FSDataInputStream in = fs.open(path);
			String line = null;
			while ((line = in.readLine()) != null) {
				this.addData("videoRindex", line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void creatTable(String tablename) throws Exception {
		HBaseAdmin admin = new HBaseAdmin(cfg);
		if (admin.tableExists(tablename)) {
			System.out.println("table   Exists!!!");
		} else {
			HTableDescriptor tableDesc = new HTableDescriptor(tablename);
			tableDesc.addFamily(new HColumnDescriptor("video"));
			tableDesc.addFamily(new HColumnDescriptor("frame"));
			tableDesc.addFamily(new HColumnDescriptor("light"));
			admin.createTable(tableDesc);
			System.out.println("create table ok .");
		}

	}

	public void addData(String tablename, String record) throws Exception {
		String[] tmp = record.split("\t");
		HTable table = new HTable(cfg, tablename);
		Put put = new Put(Bytes.toBytes(tmp[0]));
		String video = this.getVideoName(tmp[1]);
		String frame = this.getFrameName(tmp[1]);
		String light = this.getLight(tmp[1]);
//		long explicitTimeInMs = System.currentTimeMillis();
		put.add(Bytes.toBytes("video"), Bytes.toBytes(video),video.getBytes());
		put.add(Bytes.toBytes("frame"), Bytes.toBytes(frame),frame.getBytes());
		put.add(Bytes.toBytes("light"), Bytes.toBytes(light),light.getBytes());
		table.put(put);
//		System.out.println("add data ok .");
	}

	private String getVideoName(String name) {
		// hdfs://localhost:9000/user/xiaojian/Frames/A/A_01_16_frame:127.643
		String videoname = name.split("Frames")[1];
		videoname = videoname.substring(0, videoname.lastIndexOf("_"));
		return videoname + ".webm";
	}

	private String getFrameName(String name) {
		String framename = name.split("Frames")[1];
		return framename.substring(0, framename.lastIndexOf(":"));
	}

	private String getLight(String name){
		return name.substring(name.lastIndexOf(":")+1);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
