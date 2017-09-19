package com.lzy.clickstream;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.lzy.main.WeblogBean;

/**
 * 
 * 将清洗之后的日志梳理出点击流pageviews模型数据
 * 
 * 输入数据是清洗过后的结果数据
 * 
 * 区分出每一次会话，给每一次visit（session）增加了session-id（随机uuid）
 * 梳理出每一次会话中所访问的每个页面（请求时间，url，停留时长，以及该页面在这次session中的序号）
 * 保留referral_url，body_bytes_send，useragent
 * 
 */
public class ClickStreamViews {

	static class ClickStreamViewsMapper extends Mapper<LongWritable, Text, Text, WeblogBean> {
		Text k = new Text();

		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, WeblogBean>.Context context)
				throws IOException, InterruptedException {
			String[] splite = value.toString().split("\001");
			WeblogBean bean = new WeblogBean();
			bean.valid = Boolean.valueOf(splite[0]);
			bean.remote_ip = splite[1];
			bean.remote_user = splite[2];
			bean.time_local = splite[3];
			bean.request = splite[4];
			bean.status = splite[5];
			bean.body_bytes_sent = splite[6];
			bean.http_referer = splite[7];
			bean.http_user_agent = splite[8];
			k.set(bean.remote_ip);
			if (bean.valid)
				context.write(k, bean);

		}
	}

}
