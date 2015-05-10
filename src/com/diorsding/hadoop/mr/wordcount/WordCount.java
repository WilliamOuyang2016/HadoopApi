package com.diorsding.hadoop.mr.wordcount;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.setInt("mapreduce.client.submit.file.replication", 20);
		Job job = Job.getInstance();
		
		//notice
		job.setJarByClass(WordCount.class);
		
		// set mapper's property
		job.setMapperClass(WordCountMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		
		// set reducer's property
		job.setReducerClass(WordCountReducer.class);
		job.setOutputKeyClass(Text.class);
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		// setCombinerClass
		job.setCombinerClass(WordCountReducer.class);
		
		job.waitForCompletion(true);
	}
}
