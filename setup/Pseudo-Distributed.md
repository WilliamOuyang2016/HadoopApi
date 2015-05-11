The project includes these modules:

* __Hadoop Common__: The common utilities that support the other Hadoop modules.
* __Hadoop Distributed File System (HDFS™)__: A distributed file system that provides high-throughput access to application data.
* __Hadoop YARN__: A framework for job scheduling and cluster resource management.
* __Hadoop MapReduce__: A YARN-based system for parallel processing of large data sets.


### Pseudo-Distributed Operation

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
		
		10.0.0.11	hadoop
		
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
	

2. JDK
	* Install JDK
		
		```
		mkdir /usr/java
		tar -zxvf jdk-7u55-linux-i586.tar.gz -C /usr/java/
		```
	* Environment
		
		```
		vim /etc/profile
		export JAVA_HOME=/usr/java/jdk1.7.0_55
		export PATH=$PATH:$JAVA_HOME/bin
		source /etc/profile
		```

3. Hadoop
	* Install Hadoop

		```
		mkdir /cloud
		tar -zxvf hadoop-2.2.0.tar.gz -C /cloud/
		```
	* Configuration
		
		```
		1: hadoop-env.sh
		export JAVA_HOME=/usr/java/jdk1.7.0_55
		
		2: core-site.xml
		<configuration>
			<!-- 指定HDFS namenode的通信地址 -->
			<property>
				<name>fs.defaultFS</name>
				<value>hdfs://itcast01:9000</value>
			</property>
			<!-- 指定hadoop运行时产生文件的存储路径 -->
			<property>
				<name>hadoop.tmp.dir</name>
				<value>/cloud/hadoop-2.2.0/tmp</value>
			</property>
		</configuration>
		
		3: hdfs-site.xml
		<configuration>
			<!-- 设置hdfs副本数量 -->
			<property>
				<name>dfs.replication</name>
				<value>1</value>
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
			<!-- reducer取数据的方式是mapreduce_shuffle -->
			<property>
				<name>yarn.nodemanager.aux-services</name>
				<value>mapreduce_shuffle</value>
			</property>
		</configuration>
	
		
		```
	* Environment
		
		```
		vim /etc/profile
		export JAVA_HOME=/usr/java/jdk1.7.0_55
		export HADOOP_HOME=/cloud/hadoop-2.2.0
		export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin
		```
		
	* Namenode format
		
		```
		hadoop namenode -format
		```
	* Start Hadoop	
		
		```
		#start HDFS
		sbin/start-dfs.sh
		#start YARN
		sbin/start-yarn.sh
		```
	* Verification
		
		```
		jps
		
		27408 NameNode
		28218 Jps
		27643 SecondaryNameNode
		28066 NodeManager
		27803 ResourceManager
		27512 DataNode
		
		http://hadoop:50070  (HDFS管理界面)
		
		http://hadoop:8088 （MR管理界面）
		```
	
	
4. SSH
	* Generate Keys
		
		```
		ssh-keygen -t rsa
		cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
		or 
		ssh-copy-id -i localhost 
		
		```



