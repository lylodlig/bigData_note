package com.lzy.mr.rjoin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class InfoBean implements Writable {
	private int id;
	private String date;
	private String pid;
	private int amount;
	private String pname;
	private String category_id;
	private int price;
	//0表示订单，1表示商品
	private String flag;
	
	public InfoBean() {
	}

	

	public void set(int id, String date, String pid, int amount, String pname,
			String category_id, int price, String flag) {
		this.id = id;
		this.date = date;
		this.pid = pid;
		this.amount = amount;
		this.pname = pname;
		this.category_id = category_id;
		this.price = price;
		this.flag = flag;
	}



	@Override
	public String toString() {
		return "InfoBean [id=" + id + ", date=" + date + ", pid=" + pid
				+ ", amount=" + amount + ", pname=" + pname + ", category_id="
				+ category_id + ", price=" + price + ", flag=" + flag + "]";
	}
	
	

	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getDate() {
		return date;
	}



	public void setDate(String date) {
		this.date = date;
	}



	public String getPid() {
		return pid;
	}



	public void setPid(String pid) {
		this.pid = pid;
	}



	public int getAmount() {
		return amount;
	}



	public void setAmount(int amount) {
		this.amount = amount;
	}



	public String getPname() {
		return pname;
	}



	public void setPname(String pname) {
		this.pname = pname;
	}



	public String getCategory_id() {
		return category_id;
	}



	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}



	public int getPrice() {
		return price;
	}



	public void setPrice(int price) {
		this.price = price;
	}



	public String getFlag() {
		return flag;
	}



	public void setFlag(String flag) {
		this.flag = flag;
	}



	@Override
	public void readFields(DataInput in) throws IOException {
		id=in.readInt();
		date = in.readUTF();
		pid = in.readUTF();
		pname = in.readUTF();
		category_id = in.readUTF();
		flag = in.readUTF();
		amount=in.readInt();
		price=in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(id);
		out.writeUTF(date);
		out.writeUTF(pid);
		out.writeUTF(pname);
		out.writeUTF(category_id);
		out.writeUTF(flag);
		out.writeInt(amount);
		out.writeInt(price);
	}
}
