package com.lzy.main;

public class WeblogBean {
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
}
