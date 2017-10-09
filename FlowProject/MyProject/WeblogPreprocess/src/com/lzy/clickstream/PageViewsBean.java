package com.lzy.clickstream;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class PageViewsBean implements Writable {

	public String session;
	public String remote_ip;
	public String timestr;
	public String request;
	public int step;
	public String staylong;
	public String referal;

	public void set(String session, String remote_ip,  String timestr, String request, int step, String staylong, String referal) {
		this.session = session;
		this.remote_ip = remote_ip;
		this.timestr = timestr;
		this.request = request;
		this.step = step;
		this.staylong = staylong;
		this.referal = referal;
	}


	@Override
	public void readFields(DataInput in) throws IOException {
		this.session = in.readUTF();
		this.remote_ip = in.readUTF();
		this.timestr = in.readUTF();
		this.request = in.readUTF();
		this.step = in.readInt();
		this.staylong = in.readUTF();
		this.referal = in.readUTF();

	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(session);
		out.writeUTF(remote_ip);
		out.writeUTF(timestr);
		out.writeUTF(request);
		out.writeInt(step);
		out.writeUTF(staylong);
		out.writeUTF(referal);

	}

}
