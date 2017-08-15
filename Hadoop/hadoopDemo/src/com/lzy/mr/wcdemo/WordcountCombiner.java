package com.lzy.mr.wcdemo;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
  * 输如为map的输出
  * @author: 张政
  * @date: 2016年4月11日 下午7:08:18
  * @package_name: day07.sample
 */
public class WordcountCombiner extends Reducer<Text, IntWritable, Text, IntWritable>{

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

		int count=0;
		for(IntWritable v: values){
			
			count += v.get();
		}
		
		context.write(key, new IntWritable(count));
		
		
	}
	
	
}
