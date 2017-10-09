package com.lzy.main;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class WeblogBean implements Writable{
	public String remote_ip;// ��¼�ͻ��˵�ip��ַ
	public String remote_user;// ��¼�ͻ����û�����,��������"-"
	public String time_local;// ��¼����ʱ����ʱ��
	public String request;// ��¼�����url��httpЭ��
	public String status;// ��¼����״̬���ɹ���200
	public String body_bytes_sent;// ��¼���͸��ͻ����ļ��������ݴ�С
	public String http_referer;// ������¼���Ǹ�ҳ�����ӷ��ʹ�����
	public String http_user_agent;// ��¼�ͻ�������������Ϣ

	public boolean valid = true;// �ж������Ƿ�Ϸ�

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(valid);
		sb.append("\001").append(remote_ip);
		sb.append("\001").append(remote_user);
		sb.append("\001").append(time_local);
		sb.append("\001").append(request);
		sb.append("\001").append(status);
		sb.append("\001").append(body_bytes_sent);
		sb.append("\001").append(http_referer);
		sb.append("\001").append(http_user_agent);
		return sb.toString();
	}

	@Override
	public void readFields(DataInput input) throws IOException {
		remote_ip=input.readUTF();
		remote_user=input.readUTF();
		time_local=input.readUTF();
		request=input.readUTF();
		status=input.readUTF();
		body_bytes_sent=input.readUTF();
		http_referer=input.readUTF();
		http_user_agent=input.readUTF();
		valid=input.readBoolean();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(remote_ip==null?"":remote_ip);
		out.writeUTF(remote_user==null?"":remote_user);
		out.writeUTF(time_local==null?"":time_local);
		out.writeUTF(request==null?"":request);
		out.writeUTF(status==null?"":status);
		out.writeUTF(body_bytes_sent==null?"":body_bytes_sent);
		out.writeUTF(http_referer==null?"":http_referer);
		out.writeUTF(http_user_agent==null?"":http_user_agent);
		out.writeBoolean(valid);
	}
}
