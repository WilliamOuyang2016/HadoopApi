package com.diorsding.hadoop.mr.dc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class DataInfo implements Writable {
	
	private String tel;
	private long upPayLoad;
	private long downPayLoad;
	private long totalPayLoad;
	
	public DataInfo(){}
	
	public DataInfo(String tel, long upPayLoad, long downPayLoad) {
		this.tel = tel;
		this.upPayLoad = upPayLoad;
		this.downPayLoad = downPayLoad;
		this.totalPayLoad = upPayLoad + downPayLoad;
	}

	@Override
	public void readFields(DataInput input) throws IOException {
		this.tel = input.readUTF();
		this.upPayLoad = input.readLong();
		this.downPayLoad = input.readLong();
		this.totalPayLoad = input.readLong();
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeUTF(tel);
		output.writeLong(upPayLoad);
		output.writeLong(downPayLoad);
		output.writeLong(totalPayLoad);
	}
	
	
	@Override
	public String toString() {
		return upPayLoad + "\t" + downPayLoad + "\t" + totalPayLoad;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public long getUpPayLoad() {
		return upPayLoad;
	}

	public void setUpPayLoad(long upPayLoad) {
		this.upPayLoad = upPayLoad;
	}

	public long getDownPayLoad() {
		return downPayLoad;
	}

	public void setDownPayLoad(long downPayLoad) {
		this.downPayLoad = downPayLoad;
	}

	public long getTotalPayLoad() {
		return totalPayLoad;
	}

	public void setTotalPayLoad(long totalPayLoad) {
		this.totalPayLoad = totalPayLoad;
	}
	
}
