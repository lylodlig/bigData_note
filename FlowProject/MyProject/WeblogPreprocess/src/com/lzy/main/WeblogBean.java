package com.lzy.main;

public class WeblogBean {
	public String remote_ip;// 记录客户端的ip地址
	public String remote_user;// 记录客户端用户名称,忽略属性"-"
	public String time_local;// 记录访问时间与时区
	public String request;// 记录请求的url与http协议
	public String status;// 记录请求状态；成功是200
	public String body_bytes_sent;// 记录发送给客户端文件主体内容大小
	public String http_referer;// 用来记录从那个页面链接访问过来的
	public String http_user_agent;// 记录客户浏览器的相关信息

	public boolean valid = true;// 判断数据是否合法

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
