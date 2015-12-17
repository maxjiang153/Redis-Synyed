##Redis跨机房同步系统

注：该项目目前为起步阶段，从0-1的过程    
本人正在全力开发中，如果您有好的想法或者建议欢迎与我联系qq 805005024    
也欢迎给本项目提PR

####目标：

	1、跨机房Redis缓存数据同步 
	2、弱侵入性部署
	3、数据一致性保障
	4、系统高可用性保障


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
		
	Synyed-test
		测试模块 用于前期的一些功能测试以及相应的测试论证
		后期会删除该模块
