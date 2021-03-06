package com.lzy.clickstream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.beanutils.BeanUtils;
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


public class ClickStreamVisit {
	// 以session作为key，发送数据到reducer
		static class ClickStreamVisitMapper extends Mapper<LongWritable, Text, Text, PageViewsBean> {

			PageViewsBean pvBean = new PageViewsBean();
			Text k = new Text();

			@Override
			protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

				String line = value.toString();
				String[] fields = line.split("\001");
				int step = Integer.parseInt(fields[5]);
				//session user date url 停留时间 第几步 来源url
				pvBean.set(fields[0], fields[1], fields[2], fields[3],step, fields[4], fields[6]);
				k.set(pvBean.session);
				context.write(k, pvBean);
			}
		}

		static class ClickStreamVisitReducer extends Reducer<Text, PageViewsBean, NullWritable, VisitBean> {

			@Override
			protected void reduce(Text session, Iterable<PageViewsBean> pvBeans, Context context) throws IOException, InterruptedException {

				// 将pvBeans按照step排序
				ArrayList<PageViewsBean> pvBeansList = new ArrayList<PageViewsBean>();
				for (PageViewsBean pvBean : pvBeans) {
					PageViewsBean bean = new PageViewsBean();
					try {
						BeanUtils.copyProperties(bean, pvBean);
						pvBeansList.add(bean);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				Collections.sort(pvBeansList, new Comparator<PageViewsBean>() {

					@Override
					public int compare(PageViewsBean o1, PageViewsBean o2) {
						return o1.step > o2.step ? 1 : -1;
					}
				});

				// 取这次visit的首尾pageview记录，将数据放入VisitBean中
				VisitBean visitBean = new VisitBean();
				// 取visit的首记录
				visitBean.inPage=pvBeansList.get(0).request;
				visitBean.inTime=pvBeansList.get(0).timestr;
				// 取visit的尾记录
				visitBean.outPage=pvBeansList.get(pvBeansList.size() - 1).request;
				visitBean.outTime=pvBeansList.get(pvBeansList.size() - 1).timestr;
				// visit访问的页面数
				visitBean.pageVisits=pvBeansList.size();
				// 来访者的ip
				visitBean.remote_addr=pvBeansList.get(0).remote_ip;
				// 本次visit的referal
				visitBean.referal=pvBeansList.get(0).referal;
				visitBean.session=session.toString();

				context.write(NullWritable.get(), visitBean);

			}

		}

		public static void main(String[] args) throws Exception {
			Configuration conf = new Configuration();
			Job job = Job.getInstance(conf);

			job.setJarByClass(ClickStreamVisit.class);

			job.setMapperClass(ClickStreamVisitMapper.class);
			job.setReducerClass(ClickStreamVisitReducer.class);

			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(PageViewsBean.class);

			job.setOutputKeyClass(NullWritable.class);
			job.setOutputValueClass(VisitBean.class);
			
			
			FileInputFormat.setInputPaths(job, new Path(args[0]));
			FileOutputFormat.setOutputPath(job, new Path(args[1]));
//			FileInputFormat.setInputPaths(job, new Path("C:/weblog/pageviews"));
//			FileOutputFormat.setOutputPath(job, new Path("c:/weblog/visitout"));
			
			boolean res = job.waitForCompletion(true);
			System.exit(res?0:1);

		}
}
