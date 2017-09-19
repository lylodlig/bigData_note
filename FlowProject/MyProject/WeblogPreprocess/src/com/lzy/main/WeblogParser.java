package com.lzy.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeblogParser {

	static SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.US);
	static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static WeblogBean parse(String line) {
		WeblogBean bean = new WeblogBean();

		String[] split = line.split(" ");
		if (split.length > 11) {
			bean.remote_ip = split[0];
			bean.remote_user = split[1];
			bean.time_local = formatTime(split[3].substring(1));
			bean.request = split[6];
			bean.status = split[8];
			bean.body_bytes_sent = split[9];
			bean.http_referer = split[10];
			bean.http_user_agent = split[11];
			bean.valid = true;
			if (split.length > 12)
				bean.http_user_agent = split[11] + " " + split[12];
			if (Integer.valueOf(bean.status) >= 400)
				bean.valid = false;
		} else
			bean.valid = false;

		return bean;
	}

	static String formatTime(String time) {
		String timeString = "";
		try {
			Date parse = sdf1.parse(time);
			timeString = sdf2.format(parse);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeString;
	}
}
