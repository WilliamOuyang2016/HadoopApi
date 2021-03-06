package com.diorsding.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;

public class HBaseDemo {
	
	private Configuration conf = null;
	
	@Before
	public void init() {
		conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "hadoop1,hadoop2,hadoop3");
	}
	
	@Test
	public void testDrop() throws Exception {
		HBaseAdmin admin = new HBaseAdmin(conf);
		admin.disableTable("account");
		admin.deleteTable("account");
		admin.close();
	}
	
	@Test
	public void testPut() throws Exception {
		HTable table = new HTable(conf, "userss");
		Put put = new Put(Bytes.toBytes("rk003"));
		put.add(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes("liuyan"));
	}
	
	@Test
	public void testDel() throws Exception{
		HTable table = new HTable(conf, "user");
		Delete del = new Delete(Bytes.toBytes("rk0001"));
		del.deleteColumn(Bytes.toBytes("data"), Bytes.toBytes("pic"));
		table.delete(del);
		table.close();
	}
	
	@Test
	public void testCreateTable() throws Exception {
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTableDescriptor td = new HTableDescriptor("account");
		HColumnDescriptor cd = new HColumnDescriptor("info");
		cd.setMaxVersions(10);
		td.addFamily(cd);
		admin.createTable(td);
		admin.close();
	}
	
	@Test
	public void testScan() throws Exception{
		HTablePool pool = new HTablePool(conf, 10);
		HTableInterface table = pool.getTable("user");
		Scan scan = new Scan(Bytes.toBytes("rk0001"), Bytes.toBytes("rk0002"));
		scan.addFamily(Bytes.toBytes("info"));
		ResultScanner scanner = table.getScanner(scan);
		for(Result r : scanner){
			/**
			for(KeyValue kv : r.list()){
				String family = new String(kv.getFamily());
				System.out.println(family);
				String qualifier = new String(kv.getQualifier());
				System.out.println(qualifier);
				System.out.println(new String(kv.getValue()));
			}
			*/
			byte[] value = r.getValue(Bytes.toBytes("info"), Bytes.toBytes("name"));
			System.out.println(new String(value));
		}
		pool.close();
	}
	
}
