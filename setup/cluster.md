
The project includes these modules:

* __Hadoop Common__: The common utilities that support the other Hadoop modules.
* __Hadoop Distributed File System (HDFS™)__: A distributed file system that provides high-throughput access to application data.
* __Hadoop YARN__: A framework for job scheduling and cluster resource management.
* __Hadoop MapReduce__: A YARN-based system for parallel processing of large data sets.


### Cluster Design

```

| *Hostname* | *IP*      | *Software* 	  		   | *Process* |
| hadoop1    | 10.0.0.31 | jdk, hadoop 		   | NameNode, DFSZKFailoverController
| hadoop2    | 10.0.0.32 | jdk, hadoop 		   | NameNode, DFSZKFailoverController
| hadoop3    | 10.0.0.33 | jdk, hadoop    		   | ResourceManager
| hadoop4    | 10.0.0.34 | jdk, hadoop, zookeeper | DataNode、NodeManager、JournalNode、QuorumPeerMain
| hadoop5    | 10.0.0.35 | jdk, hadoop, zookeeper | DataNode、NodeManager、JournalNode、QuorumPeerMain
| hadoop6    | 10.0.0.36 | jdk, hadoop, zookeeper | DataNode、NodeManager、JournalNode、QuorumPeerMain
```



### Linux Environment
对于6台机器，都要这样处理, 然后hostname, IP, macAddress 都要修改

1. Linux Environment
	* Hostname
		
		```
		vim /etc/sysconfig/network
		
		NETWORKING=yes 
		HOSTNAME=hadoop    ###
		```
		
	* IP
		
		```
		vim /etc/sysconfig/network-scripts/ifcfg-eth0
		
		DEVICE="eth0"
		BOOTPROTO="static"           ###
		HWADDR="00:0C:29:3C:BF:E7"
		IPV6INIT="yes"
		NM_CONTROLLED="yes"
		ONBOOT="yes"
		TYPE="Ethernet"
		UUID="ce22eeca-ecde-4536-8cc2-ef0dc36d4a8c"  
		IPADDR=10.0.0.11       ###
		NETMASK=255.255.255.0      ###
		GATEWAY=10.0.0.1        ###
		
		```
	* host-ip mapping
	
		```
		vim /etc/hosts
		
		10.0.0.11	hadoop1
		10.0.0.12	hadoop2
		10.0.0.13	hadoop3
		10.0.0.14	hadoop4
		10.0.0.15	hadoop5
		10.0.0.16	hadoop6
		```
	这里要注意，localhost 一定还是对应127.0.0.1， 不要把hostname对应上去	
		
	* Firewall
		
		CentOS 6.5
	
		```
		#查看防火墙状态
		service iptables status
		#关闭防火墙
		service iptables stop
		#查看防火墙开机启动状态
		chkconfig iptables --list
		#关闭防火墙开机启动
		chkconfig iptables off
		```
		
		CentOS 7
	
		```
		#查看防火墙状态
		systemctl status firewalld
		#关闭防火墙
		systemctl stop firewalld
		#关闭防火墙开机启动
		systemctl disable firewalld
		```
	* Reboot
	


2. JDK & Hadoop
	* Install JDK
		
		```
		mkdir /usr/java
		tar -zxvf jdk-7u55-linux-i586.tar.gz -C /usr/java/
		```
	* Install Hadoop

		```
		mkdir /cloud
		tar -zxvf hadoop-2.2.0.tar.gz -C /cloud/
		```
	* Environment
		
		```
		vim /etc/profile
		export JAVA_HOME=/usr/java/jdk1.7.0_55
		export HADOOP_HOME=/cloud/hadoop-2.2.0
		export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin
		source /etc/profile
		```

3. Zookeeper

	* Install Zookeeper
		
		```
		tar -zxvf zookeeper-3.4.5.tar.gz -C /cloud/
		```
	* Configuration
	
		```
		cd /cloud/zookeeper-3.4.5/conf/
		cp zoo_sample.cfg zoo.cfg
		vim zoo.cfg
		修改：dataDir=/cloud/zookeeper-3.4.5/tmp
		在最后添加：
		server.1=hadoop4:2888:3888
		server.2=hadoop5:2888:3888
		server.3=hadoop6:2888:3888
		

		创建一个tmp文件夹
		mkdir /cloud/zookeeper-3.4.5/tmp
		再创建一个空文件
		touch /cloud/zookeeper-3.4.5/tmp/myid
		最后向该文件写入ID
		echo 1 > /cloud/zookeeper-3.4.5/tmp/myid
		
		```
	* Copy
		
		```
		scp -r /cloud/zookeeper-3.4.5/ hadoop5:/cloud/
		scp -r /cloud/zookeeper-3.4.5/ hadoop6:/cloud/
		
		注意：修改hodoop5、hadoop6对应/cloud/zookeeper-3.4.5/tmp/myid内容
		hadoop5：echo 2 > /cloud/zookeeper-3.4.5/tmp/myid
		hadoop6：echo 3 > /cloud/zookeeper-3.4.5/tmp/myid
		```


4. Hadoop
	
	* Configuration
		
		```
		1: hadoop-env.sh
		export JAVA_HOME=/usr/java/jdk1.7.0_55
		
		2: core-site.xml
		<configuration>
			<!-- 指定hdfs的nameservice为ns1 -->
			<property>
				<name>fs.defaultFS</name>
				<value>hdfs://ns1</value>
			</property>
			<!-- 指定hadoop运行时产生文件的存储路径 -->
			<property>
				<name>hadoop.tmp.dir</name>
				<value>/cloud/hadoop-2.2.0/tmp</value>
			</property>
			
			<!-- 指定zookeeper地址 -->
			<property>
				<name>ha.zookeeper.quorum</name>
				<value>hadoop4:2181,hadoop5:2181,hadoop6:2181</value>
			</property>
		</configuration>
		
		3: hdfs-site.xml
		<configuration>
			<!--指定hdfs的nameservice为ns1，需要和core-site.xml中的保持一致 -->
			<property>
				<name>dfs.nameservices</name>
				<value>ns1</value>
			</property>
			
			<!-- ns1下面有两个NameNode，分别是nn1，nn2 -->
			<property>
				<name>dfs.ha.namenodes.ns1</name>
				<value>nn1,nn2</value>
			</property>
			
			<!-- nn1的RPC通信地址 -->
			<property>
				<name>dfs.namenode.rpc-address.ns1.nn1</name>
				<value>hadoop1:9000</value>
			</property>
			
			<!-- nn1的http通信地址 -->
			<property>
				<name>dfs.namenode.http-address.ns1.nn1</name>
				<value>hadoop1:50070</value>
			</property>
			
			<!-- nn2的RPC通信地址 -->
			<property>
				<name>dfs.namenode.rpc-address.ns1.nn2</name>
				<value>hadoop2:9000</value>
			</property>
			
			<!-- nn2的http通信地址 -->
			<property>
				<name>dfs.namenode.http-address.ns1.nn2</name>
				<value>hadoop2:50070</value>
			</property>
			
			<!-- 指定NameNode的元数据在JournalNode上的存放位置 -->
			<property>
				<name>dfs.namenode.shared.edits.dir</name>
				<value>qjournal://hadoop4:8485;hadoop5:8485;hadoop6:8485/ns1</value>
			</property>
			
			<!-- 指定JournalNode在本地磁盘存放数据的位置 -->
			<property>
				<name>dfs.journalnode.edits.dir</name>
				<value>/cloud/hadoop-2.2.0/journal</value>
			</property>
			
			<!-- 开启NameNode失败自动切换 -->
			<property>
				<name>dfs.ha.automatic-failover.enabled</name>
				<value>true</value>
			</property>
			
			<!-- 配置失败自动切换实现方式 -->
			<property>
				<name>dfs.client.failover.proxy.provider.ns1</name>
				<value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
			</property>
			
			<!-- 配置隔离机制方法，多个机制用换行分割，即每个机制暂用一行-->
			<property>
				<name>dfs.ha.fencing.methods</name>
				<value>
					sshfence
					shell(/bin/true)
				</value>
			</property>
			
			<!-- 使用sshfence隔离机制时需要ssh免登陆 -->
			<property>
				<name>dfs.ha.fencing.ssh.private-key-files</name>
				<value>/root/.ssh/id_rsa</value>
			</property>
			
			<!-- 配置sshfence隔离机制超时时间 -->
			<property>
				<name>dfs.ha.fencing.ssh.connect-timeout</name>
				<value>30000</value>
			</property>
		</configuration>
		
		4: mapred-site.xml 需要重命名
		<configuration>
			<!-- 通知框架MR使用YARN -->
			<property>
				<name>mapreduce.framework.name</name>
				<value>yarn</value>
			</property>
		</configuration>
		
		5: yarn-site.xml
		<configuration>
			<!-- 指定resourcemanager地址 -->
			<property>
				<name>yarn.resourcemanager.hostname</name>
				<value>hadoop3</value>
			</property>
			
			<!-- 指定nodemanager启动时加载server的方式为shuffle server -->
			<property>
				<name>yarn.nodemanager.aux-services</name>
				<value>mapreduce_shuffle</value>
			</property>
		</configuration>

		6. slaves
		(slaves是指定子节点的位置，因为要在hadoop1上启动HDFS、在hadoop3启动yarn，所以hadoop1上的slaves文件指定的是datanode的位置，hadoop3上的slaves文件指定的是nodemanager的位置)
		hadoop4
		hadoop5
		hadoop6
	
		
		```
	* SSH 

		```
		#首先要配置hadoop1到hadoop2、hadoop3、hadoop4、hadoop5、hadoop6的免密码登陆
		#在hadoop1上生产一对钥匙
		ssh-keygen -t rsa
		#将公钥拷贝到其他节点，包括自己
		ssh-coyp-id hadoop1
		ssh-coyp-id hadoop2
		ssh-coyp-id hadoop3
		ssh-coyp-id hadoop4
		ssh-coyp-id hadoop5
		ssh-coyp-id hadoop6
		#配置hadoop3到hadoop4、hadoop5、hadoop6的免密码登陆
		#在hadoop3上生产一对钥匙
		ssh-keygen -t rsa
		#将公钥拷贝到其他节点
		ssh-coyp-id hadoop4
		ssh-coyp-id hadoop5
		ssh-coyp-id hadoop6
		#注意：两个namenode之间要配置ssh免密码登陆，别忘了配置hadoop2到hadoop1的免登陆
		在hadoop2上生产一对钥匙
		ssh-keygen -t rsa
		ssh-coyp-id -i hadoop1		
	
		```
	* 配置好的机器发送给其他机器
	
		```
		scp -r /cloud/ hadoop2:/
		scp -r /cloud/ hadoop3:/
		scp -r /cloud/hadoop-2.2.0/ root@hadoop4:/cloud/
		scp -r /cloud/hadoop-2.2.0/ root@hadoop5:/cloud/
		scp -r /cloud/hadoop-2.2.0/ root@hadoop6:/cloud/
		
		```
5. Start 	
		
	* Start Zookeeper(分别在hadoop4, hadoop5, hadoop6 上启动zookeeper)
		
		```
		cd /cloud/zookeeper-3.4.5/bin/
		./zkServer.sh start
		#查看状态：一个leader，两个follower
		./zkServer.sh status
		```
		这里status一定要确定出现一个leader, 两个follower. jps进程也要OK
		
	* Namenode format， hadoop1 上运行(hadoop2也OK，但是我们这里只配置了hadoop1到其他机器的ssh)
		
		```
		hadoop namenode -format
		
		scp -r tmp/ hadoop2:/cloud/hadoop-2.2.0/   # hadoop1生成的tmp要copy到hadoop2上
		```
		
	* Zookeeper format (hadoop1上运行)
		
		```
		hdfs zkfc -formatZK
		```
	
	* Start Hadoop	(hadoop1运行)
		
		```
		#start HDFS
		sbin/start-dfs.sh

		```
	* Start Yarn
		
		```
		#start YARN
		sbin/start-yarn.sh
		```	
	
	* Verification
		Web 端. 
		
		```
		http://hadoop1:50070
		NameNode 'hadoop1:9000' (active)
		http://hadoop2:50070
		NameNode 'hadoop2:9000' (standby)
		http://hadoop3:8088
		NameNode 'hadoop3:8088'   ## 查看中间node是否正确
		
		```
	
		HDFS HA	
		
		```
		首先向hdfs上传一个文件
		hadoop fs -put /etc/profile /profile
		hadoop fs -ls /
		
		然后再kill掉active的NameNode
		kill -9 <pid of NN>
		通过浏览器访问：http://hadoop2:50070
		NameNode 'hadoop2:9000' (active)
		这个时候hadoop2上的NameNode变成了active
		
		在执行命令：
		hadoop fs -ls /
		-rw-r--r--   3 root supergroup       1926 2014-02-06 15:36 /profile
		HDFS依然在运行
		
		手动启动那个挂掉的NameNode
		sbin/hadoop-daemon.sh start namenode
		通过浏览器访问：http://hadoop1:50070
		NameNode 'hadoop1:9000' (standby)
		```
		
		YARN - 跑一个example的例子
		
		```
		hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.2.0.jar wordcount /profile /out
		```
		
		jps
		
		根据最上面cluster design来验证
	











