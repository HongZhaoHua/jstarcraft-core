JStarCraft Core
==========

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

*****

**JStarCraft Core是一个面向对象的轻量级框架,遵循Apache 2.0协议.**

|作者|洪钊桦|
|---|---
|E-mail|110399057@qq.com, jstarcraft@gmail.com

*****

## JStarCraft Core架构

JStarCraft Core框架各个模块之间的关系:
![core](https://github.com/HongZhaoHua/jstarcraft-reference/blob/master/core/JStarCraft%E6%A0%B8%E5%BF%83%E6%A1%86%E6%9E%B6%E7%BB%84%E4%BB%B6%E5%9B%BE.png "JStarCraft Core架构")

*****

## JStarCraft Core特性
* [1.缓存(cache)](https://github.com/HongZhaoHua/jstarcraft-core-1.0/wiki/%E7%BC%93%E5%AD%98)
    * Cache Aside
    * Cache as Record
* 2.编解码(codec)
    * CSV
    * JSON
    * Kryo
    * ProtocolBufferX
* 3.通用(common)
    * 编译(compilation)
    * 转换(conversion)
    * 识别(identification)
    * 日期与时间(instant)
        * 间隔
        * 阳历
        * 阴历
        * 节气
    * 锁(lockable)
        * 链锁
        * 哈希锁
    * 日志(log)
        * Log4j 1
        * Log4j 2
    * 反射(reflection)
    * 安全(security)
* 4.通讯(communication)
    * TCP
    * UDP
* [5.分布式(distribution)](https://github.com/HongZhaoHua/jstarcraft-core-1.0/wiki/%E5%88%86%E5%B8%83%E5%BC%8F)
    * 数据路由
    * 一致性哈希
    * 标识管理
    * 分布式锁
* [6.对象关系映射(orm)](https://github.com/HongZhaoHua/jstarcraft-core-1.0/wiki/%E5%AF%B9%E8%B1%A1%E5%85%B3%E7%B3%BB%E6%98%A0%E5%B0%84)
    * 键值数据库(Berkeley DB)
    * 关系型数据库(Hibernate/MyBatis)
    * 文档型数据库(Mongo DB)
* 7.脚本(script)
    * Groovy
    * JS
    * Lua
    * MVEL
* 8.配置仓储(storage)
    * 格式
        * CSV
        * JSON
        * Properties
        * XLSX
        * YAML
    * 路径与流
        * Disk
        * FTP
        * Git
        * HDFS
        * HTTP
        * Memory
        * SVN
        * ZooKeeper
