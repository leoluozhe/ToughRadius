ToughRadius
===========

### ToughRadius 功能特性

ToughRadius 是基于开源TinyRadius开发的一个支持高并发的Radius服务器。TinyRadius提供了Radius协议实现基础，ToughRadius增加了多线程的支持，可以支持高并发的认证计费请求。

ToughRadius 使用java开发，支持跨平台的部署。

ToughRadius增加了数据库存储支持，通过jdbc驱动支持多种数据库，比如Mysql,Hsqldb,Oracle等，当前ToughRadius内置Hsqldb的支持，Hsqldb是一个高性能的java嵌入式数据库，强烈建议你使用它来存储您的用户数据。

ToughRadius实现了一个Web管理界面，提供用户管理，用户组管理，客户端管理等功能，支持灵活的属性扩展。

### 版本构建

二进制版本发布请移步这里：[https://github.com/jamiesun/ToughRadius-Release][]

如果您对java环境下的构建比较了解，可以选择从源码构建：

1. 设置java环境变量。

2. 设置ANT环境变量。

3. 从github克隆或下载ToughRadius最新代码。

4. 执行ant构建自己的ToughRadius发布包。

### 数据库安装

当前ToughRadius内置Hsqldb的支持，ToughRadius提供了一个快捷的初始化脚本来帮助您初始化您的数据库。

linux环境下请执行：setupDB.sh 

windows环境请执行：setupDB.bat

### 运行说明

linux环境下请执行：startup.sh 

windows环境请执行：startup.bat

### 系统运行参数设置

修改conf目录下的system.xml配置文件：
```
<?xml version="1.0" encoding="UTF-8" ?>
<config>
	<radius>
		<version>ToughRadius V1.0</version>
		<authPort>1812</authPort>
		<acctPort>1813</acctPort>
		<adminPort>1815</adminPort>
		<maxSessionTimeout>86400</maxSessionTimeout>
	</radius>
	<webapp>
		<port>8080</port>
		<maxConn>1024</maxConn>
	</webapp>
	<database>
		<driver>org.hsqldb.jdbc.JDBCDriver</driver>
		<url>jdbc:hsqldb:./data/radius</url>
		<username>sa</username>
		<password></password>
	</database>	
</config>
```
修改conf目录下的log4j.xml配置文件,在实际生产环境中应该只开启error级别日志。

### 开发者和贡献者

@jamiesun  欢迎更多的开发者加入。


### 技术支持

ToughRadius是一个开源和免费的产品，使用ToughRadius，您不需要为此付出购买费用，我们提供长期的版本维护，同时根据使用者的实际需求，我们提供有偿的技术服务。
