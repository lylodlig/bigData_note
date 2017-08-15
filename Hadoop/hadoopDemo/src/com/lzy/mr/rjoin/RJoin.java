package com.lzy.mr.rjoin;

import java.io.IOException;
import java.util.ArrayList;

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
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class RJoin {

	static class RJoinMapper extends Mapper<LongWritable, Text, Text, InfoBean> {

		InfoBean bean = new InfoBean();
		Text key = new Text();

		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			
			String line = value.toString();
			
			FileSplit inputSplit = (FileSplit) context.getInputSplit();
			String name = inputSplit.getPath().getName();
			// 通过文件名判断是哪一种
			String pid = "";
			if (name.startsWith("order")) {
				String[] split = line.split(",");
				pid = split[2];
				bean.set(Integer.parseInt(split[0]), split[1], pid,
						Integer.parseInt(split[3]), "", "", 0, "0");
			} else {
				String[] split = line.split(",");
				pid = split[0];
				bean.set(0, "", pid, 0, split[1], split[2],
						Integer.parseInt(split[3]), "1");
			}
			this.key.set(pid);
			context.write(this.key, bean);
		}
	}

	static class RJoinReducer extends
			Reducer<Text, InfoBean, InfoBean, NullWritable> {
		@Override
		protected void reduce(Text pid, Iterable<InfoBean> beans,
				Context context) throws IOException, InterruptedException {
			InfoBean b = new InfoBean();
			ArrayList<InfoBean> orderBeans = new ArrayList<>();
			for (InfoBean bean : beans) {
				if (bean.getFlag().equals("1")) {
					try {
						BeanUtils.copyProperties(b, bean);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					InfoBean orderBean = new InfoBean();
					try {
						BeanUtils.copyProperties(orderBean, bean);
					} catch (Exception e) {
						e.printStackTrace();
					}
					orderBeans.add(orderBean);
				}
			}
			//拼接
			for (InfoBean infoBean : orderBeans) {
				infoBean.setPname(b.getPname());
				infoBean.setCategory_id(b.getCategory_id());
				infoBean.setPrice(b.getPrice());
				context.write(infoBean, NullWritable.get());
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		/*conf.set("mapreduce.framework.name", "yarn");
		conf.set("yarn.resoucemanager.hostname", "mini1");*/
		Job job = Job.getInstance(conf);
		
		/*job.setJar("/home/hadoop/wc.jar");*/
		//指定本程序的jar包所在的本地路径
		job.setJarByClass(RJoin.class);
		
		//指定本业务job要使用的mapper/Reducer业务类
		job.setMapperClass(RJoinMapper.class);
		job.setReducerClass(RJoinReducer.class);
		
		//指定mapper输出数据的kv类型
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(InfoBean.class);
		
		//指定最终输出的数据的kv类型
		job.setOutputKeyClass(InfoBean.class);
		job.setOutputValueClass(NullWritable.class);
		
		//指定job的输入原始文件所在目录
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		//指定job的输出结果所在目录
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		//将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
		/*job.submit();*/
		boolean res = job.waitForCompletion(true);
		System.exit(res?0:1);
		
	}
}
