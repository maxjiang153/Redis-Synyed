##Redis跨机房同步系统

注：该项目目前为起步阶段，从0-1的过程    
本人正在全力开发中，如果您有好的想法或者建议欢迎与我联系qq 805005024    
也欢迎给本项目提PR

####最新进展：     
	目前已经实现了单Agent 单源Redis 单目标Redis的数据同步操作    
	[Synyed支持同步的Redis命令列表](https://github.com/wmz7year/Redis-Synyed/wiki/Synyed%E6%94%AF%E6%8C%81%E5%90%8C%E6%AD%A5%E7%9A%84Redis%E5%91%BD%E4%BB%A4%E5%88%97%E8%A1%A8)    
	
	打包方法 mvn clean package     
	解压target包下的Synyed-Agent.zip修改conf/application.properties文件为对应的Redis服务器地址后
	使用bin/Synyed-Agent start脚本即可启动同步操作
	
####当前版本遗留的问题：

	RDB文件解析还原成命令时RedisHashObject、RedisHashZipMap、RedisListObject、RedisSetIntSet、RedisZSetObject    
	五种数据结构暂时无法还原成Redis命令

	本人正在努力视线中
	
####目标：

	1、跨机房Redis缓存数据同步 
	2、弱侵入性部署
	3、数据一致性保障
	4、系统高可用性保障

####阶段实现方案：     
	* 实现单Agent的源Redis到目标Redis数据单向同步
	* 实现单Agent的源Redis到多个目标Redis的数据单向同步
	* 实现单Agent的多个源Redis到单个目标Redis的数据单向同步
	* 实现单Agent的多个源Redis到多个目标Redis的数据单向同步
	* 实现双Agent之间的源Redis到目标Redis的数据单向同步
	* 管理后台统一配置Agent的同步策略

####目前进度：
	- 完成Redis网络协议解析
	- 完成Redis rdb文件内容解析
	- 完成Redis网络协议数据包转换为Redis命令语义分析
	- 目前支持单Agent 单源Redis 单目标Redis的数据同步

####模块：

	Synyed-agent   
		用于模拟redis的slave    
		数据的同步处理
		
	Synyed-manager
		agent的管理
		agent的配置
		
	Synyed-common
		公共的一些组件模块
		
	Synyed-util
		工具了模块
		
