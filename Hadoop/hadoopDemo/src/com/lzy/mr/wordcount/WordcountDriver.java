package com.lzy.mr.wordcount;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 相当于一个yarn集群的客户端
 * 在此封装mr程序运行的参数，指定jar包
 * 最后提交给yarn
 * @author Administrator
 *
 */
public class WordcountDriver {

	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		//指定提交到哪里,打成jar包放到linux中运行，linux以及配置了参数，所以不需要
//		conf.set("mapreduce.framework.name", "yarn");
//		conf.set("yarn.resourcemanager.hostname", "sm1");
		Job job = Job.getInstance(conf);
		
		//设置jar包的路径 
//		job.setJar("/home/hadoop/wc.jar");
		job.setJarByClass(WordcountDriver.class);
		
		job.setMapperClass(WordcountMapper.class);
		job.setReducerClass(WordcountReducer.class);
		//指定mapper的输出类型
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		//指定最终的输出类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		//job的输入原始文件所在目录
		FileInputFormat.setInputPaths(job,new Path(args[0]));
		//指定输出结果目录
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		//将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn运行
//		job.submit();
		//先提交，等到运行完后的返回信息
		boolean b = job.waitForCompletion(true);
		System.exit(b?0:1);
	}

}
