package com.lzy.mr.wordcount;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * KEYIN，VALUEIN对应mapper输出的KEYOUT、VALUEOUT
 * 
 * KEYOUT是单词
 * VALUEOUT是总次数
 * @author Administrator
 *
 */
public class WordcountReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
	
	/**
	 * 入参，key是一组相同单词kv对的key，比如<hello,1><hello,1><hello,1><hello,1><hello,1>
	 * 一组调用一次，比如hello一次，world一次 
	 */
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,Context context)
			throws IOException, InterruptedException {
		int count=0;
		for (IntWritable value : values) {
			count+=value.get();
		}
		//默认写到一个文件里面去 
		context.write(key, new IntWritable(count));
	}
}
