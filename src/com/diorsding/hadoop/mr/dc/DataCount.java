package com.diorsding.hadoop.mr.dc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Partition example
 * 
 * @author jiashan
 *
 */

public class DataCount {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(DataCount.class);
		
		job.setMapperClass(DCMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DataInfo.class);
		
		job.setReducerClass(DCReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DataInfo.class);
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		
		// set partition information
		job.setPartitionerClass(DCPartitioner.class);
		job.setNumReduceTasks(Integer.parseInt(args[2]));
		
		job.waitForCompletion(true);

	}

	public static class DCMapper extends Mapper<LongWritable, Text, Text, DataInfo>{	
		private Text k = new Text();
		
		@Override
		protected void map(LongWritable key, Text value,
				Mapper<LongWritable, Text, Text, DataInfo>.Context context)
				throws IOException, InterruptedException {
			
			String line = value.toString();
			String[] fields = line.split("\t");
			
			String tel = fields[1];	
			long up = Long.parseLong(fields[8]);
			long down = Long.parseLong(fields[9]);
			
			DataInfo dataInfo = new DataInfo(tel,up,down);
			k.set(tel);
			
			context.write(k, dataInfo);
		}
	}
	
	
	public static class DCReducer extends Reducer<Text, DataInfo, Text, DataInfo>{
		
		@Override
		protected void reduce(Text key, Iterable<DataInfo> values,
				Reducer<Text, DataInfo, Text, DataInfo>.Context context)
				throws IOException, InterruptedException {
			
			long up_sum = 0;
			long down_sum = 0;
			
			for (DataInfo d : values){
				up_sum += d.getUpPayLoad();
				down_sum += d.getDownPayLoad();
			}
			
			DataInfo dataInfo = new DataInfo("",up_sum,down_sum);
			context.write(key, dataInfo);
		}
	}

	public static class DCPartitioner extends Partitioner<Text, DataInfo>{
		private static Map<String,Integer> provider = new HashMap<String,Integer>();
		
		static{
			provider.put("138", 1);
			provider.put("139", 1);
			provider.put("150", 2);
			provider.put("159", 2);
			provider.put("182", 3);
			provider.put("183", 3);
		}
		@Override
		public int getPartition(Text key, DataInfo value, int numPartitions) {
			String tel_sub = key.toString().substring(0,3);
			Integer count = provider.get(tel_sub);
			
			if(count == null){
				count = 0;
			}
			
			return count;
		}
	}

}
