package com.lzy.mr.wordcount;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * KEYIN：默认情况下，是mr框架所读到的一行文本的起始偏移量，Long,但是
 * 在hadoop中有更精简的序列化接口，所以不直接用Long，而是LongWritable
 * VALUEIN：默认情况下，是mr框架所读到的一行文本的内容，String，同上，用Text
 * 
 * KEYOUT：是用户自定义逻辑处理完成之后输出数据中的key，此处是单词，String
 * KEYOUT：是用户自定义逻辑处理完成之后输出数据中的value，此处是单词次数，int、long
 * @author Administrator
 *
 */
public class WordcountMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
	
	/**
	 * 这里写map阶段的业务逻辑
	 * maptask会对每一行输入的数据调用一次该方法 
	 */
	@Override
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		//根据空格切割单词
		String[] words = line.split(" ");
		//将单词输出为<单词，1>
		for (String word : words) {
			context.write(new Text(word), new IntWritable(1));
		}
		
	}

}
