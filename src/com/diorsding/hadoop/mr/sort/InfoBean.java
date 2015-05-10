package com.diorsding.hadoop.mr.sort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class InfoBean implements WritableComparable<InfoBean> {
	
	private String account;
	private double income;
	private double expense;
	private double earning;
	
	public void set(String account, double income, double expense) {
		this.account = account;
		this.income = income;
		this.expense = expense;
		this.earning = income - expense;
	}
	
	@Override
	public void readFields(DataInput input) throws IOException {
		this.account = input.readUTF();
		this.income = input.readDouble();
		this.expense = input.readDouble();
		this.earning = input.readDouble();
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeUTF(account);
		output.writeDouble(income);
		output.writeDouble(expense);
		output.writeDouble(earning);
	}

	@Override
	public int compareTo(InfoBean o) {
		if (this.income == o.getIncome()) {
			return this.expense > o.getExpense() ? 1: -1; 
		}
		
		return this.income > o.getIncome() ? -1 : 1;
	}

	@Override
	public String toString() {
		return income + "\t" + expense + "\t" + earning;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public double getIncome() {
		return income;
	}

	public void setIncome(double income) {
		this.income = income;
	}

	public double getExpense() {
		return expense;
	}

	public void setExpense(double expense) {
		this.expense = expense;
	}

	public double getSurplus() {
		return earning;
	}

	public void setSurplus(double surplus) {
		this.earning = surplus;
	}
	

}
