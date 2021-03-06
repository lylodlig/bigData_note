package com.lzy.clickstream;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

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
	
	
	public static void main(String[] args) throws Exception{
		
		Configuration configuration=new Configuration();
		Job job=Job.getInstance(configuration);
		
		job.setJarByClass(ClickStreamViews.class);
		
		job.setMapperClass(ClickStreamViewsMapper.class);
		job.setReducerClass(ClickStreamViewsReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(WeblogBean.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.waitForCompletion(true);
		
	}
	
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

	static class ClickStreamViewsReducer extends Reducer<Text, WeblogBean, NullWritable, Text> {

		Text v = new Text();

		@Override
		protected void reduce(Text key, Iterable<WeblogBean> values,
				Reducer<Text, WeblogBean, NullWritable, Text>.Context context)
				throws IOException, InterruptedException {
			// 把结果放到集合中
			ArrayList<WeblogBean> beans = new ArrayList<>();
			for (WeblogBean weblogBean : values) {
				WeblogBean bean = new WeblogBean();
				try {
					BeanUtils.copyProperties(bean, weblogBean);
					beans.add(bean);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// 按照时间顺序排序
			Collections.sort(beans, new Comparator<WeblogBean>() {

				@Override
				public int compare(WeblogBean b1, WeblogBean b2) {
					if (b1.time_local == null || b2.time_local == null) {
						return 0;
					}
					try {
						Date date1 = toDate(b1.time_local);
						Date date2 = toDate(b1.time_local);
						return date1.compareTo(date2);
					} catch (ParseException e) {
						e.printStackTrace();
						return 0;
					}
				}
			});
			try {
				int step = 1;
				String session = UUID.randomUUID().toString();
				for (int i = 0; i < beans.size(); i++) {
					WeblogBean bean = beans.get(i);
					// 只有一条数据直接输出,输出格式为：session user date url 停留时间 第几步 来源url
					if (beans.size() == 1) {
						v.set(session + "\001" + bean.remote_ip + "\001" + bean.time_local + "\001" + bean.request
								+ "\001" + "60" + "\001" + step+"\001"+bean.http_referer);
						context.write(NullWritable.get(), v);
						session = UUID.randomUUID().toString();
						break;
					}
					// 不止一条时跳过，第二条再输出
					if (i == 0)
						continue;
					// 计算两次时间差
					long timeDiff = timeDiff(bean.time_local, beans.get(i - 1).time_local);
					WeblogBean lastBaen = beans.get(i - 1);
					if (timeDiff < 30 * 60 * 1000) {
						v.set(session + "\001" + lastBaen.remote_ip + "\001" + lastBaen.time_local + "\001"
								+ lastBaen.request + "\001" + timeDiff + "\001" + step+"\001"+bean.http_referer);
						step++;
						context.write(NullWritable.get(), v);
					} else {
						v.set(session + "\001" + lastBaen.remote_ip + "\001" + lastBaen.time_local + "\001"
								+ lastBaen.request + "\001" + timeDiff + "\001" + step+"\001"+bean.http_referer);
						context.write(NullWritable.get(), v);
						// 输出完上一条重置step
						step = 1;
						session = UUID.randomUUID().toString();
					}

					if (i == beans.size() - 1) {// 最后一条，直接输出
						v.set(session + "\001" + bean.remote_ip + "\001" + bean.time_local + "\001"
								+ bean.request + "\001" + timeDiff + "\001" + step+"\001"+bean.http_referer);
						context.write(NullWritable.get(), v);
					}	

				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		private Date toDate(String timeStr) throws ParseException {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return df.parse(timeStr);
		}

		private long timeDiff(String time1, String time2) throws ParseException {
			Date d1 = toDate(time1);
			Date d2 = toDate(time2);
			return d1.getTime() - d2.getTime();

		}

	}

}
