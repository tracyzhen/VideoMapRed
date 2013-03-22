package com.video.rindex;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

@SuppressWarnings("deprecation")
public class HbaseTest {

	static HBaseConfiguration cfg = null;
	static {
		Configuration HBASE_CONFIG = new Configuration();
		HBASE_CONFIG.set("hbase.zookeeper.quorum", "localhost");
		cfg = new HBaseConfiguration(HBASE_CONFIG);
	}

	public static void creatTable(String tablename) throws Exception {
		HBaseAdmin admin = new HBaseAdmin(cfg);
		if (admin.tableExists(tablename)) {
			System.out.println("table   Exists!!!");
		} else {
			HTableDescriptor tableDesc = new HTableDescriptor(tablename);
			tableDesc.addFamily(new HColumnDescriptor("name"));
			admin.createTable(tableDesc);
			System.out.println("create table ok .");
		}

	}

	public static void addData(String tablename) throws Exception {
		HTable table = new HTable(cfg, tablename);
		Put put=new Put(Bytes.toBytes("huangyi"));
		put.add(Bytes.toBytes("name"), Bytes.toBytes("java2"), "http://www.javabloger.com".getBytes());
		table.put(put);
		System.out.println("add data ok .");
	}

	public static void getAllData(String tablename) throws Exception {
		HTable table = new HTable(cfg, tablename);
		Scan s = new Scan();
		ResultScanner ss = table.getScanner(s);
		for (Result r : ss) {
			for (KeyValue kv : r.raw()) {
				System.out.print(new String(kv.getKey()));
				System.out.println(new String(kv.getValue()));
			}

		}
	}

	
	
	public static void main(String[] args) {
		try {
			String tablename = "hbasetest";
			HbaseTest.creatTable(tablename);
			HbaseTest.addData(tablename);
			HbaseTest.getAllData(tablename);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
