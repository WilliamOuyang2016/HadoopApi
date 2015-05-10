package com.diorsding.hadoop.hdfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Hdfs;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class HDFSDemo {

	private FileSystem fs = null;
	private static final String HDFS_ADDR = "hdfs://hadoop:9000";
	private static final String HOME_DIR = "/Users/jiashan/Downloads/";
	
	@Before
	public void init() throws IOException, InterruptedException, URISyntaxException {
		fs = FileSystem.get(new URI(HDFS_ADDR), new Configuration(), "root");
	}
	
	@Test
	public void testDelete() throws IllegalArgumentException, IOException {
		boolean flag = fs.delete(new Path("/hadoop-2.2.0-64bit.tar.gz"), true);
		System.out.println(flag);
	}
	
	@Test
	public void testMkDir() throws IllegalArgumentException, IOException {
		boolean flag = fs.mkdirs(new Path("/testDir"));
		System.out.println(flag);
	}
	
	@Test
	public void testReadFile() throws IOException, URISyntaxException {
		FileSystem fs = FileSystem.get(new URI(HDFS_ADDR), new Configuration());
		InputStream in = fs.open(new Path("/hadoop-2.2.0-64bit.tar.gz"));
		FileOutputStream out = new FileOutputStream(new File(HOME_DIR + "testHadoopFromHdfs"));
		IOUtils.copyBytes(in, out, 2048, true);
	}
	
	@Test
	public void testUpload() throws IllegalArgumentException, IOException {
		FSDataOutputStream out = fs.create(new Path("/hadoop-2.2.0-64bit.tar.gz"));
		FileInputStream in = new FileInputStream(new File(HOME_DIR + "hadoop-2.2.0-64bit.tar.gz"));
		IOUtils.copyBytes(in, out, 2048, true);
	}
}
