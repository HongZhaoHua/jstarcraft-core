# JStarCraft Core

****

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Total lines](https://tokei.rs/b1/github/HongZhaoHua/jstarcraft-core?category=lines)](https://tokei.rs/b1/github/HongZhaoHua/jstarcraft-core?category=lines)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8e39a24e1be740c58b83fb81763ba317)](https://www.codacy.com/project/HongZhaoHua/jstarcraft-core/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=HongZhaoHua/jstarcraft-core&amp;utm_campaign=Badge_Grade_Dashboard)


希望路过的同学,顺手给JStarCraft框架点个Star,算是对作者的一种鼓励吧!

****

## 目录

* [介绍](#介绍)
* [架构](#架构)
* [模块](#模块)
* [安装](#安装)
* [使用](#使用)
* [概念](#概念)
* [特性](#特性)
* [示例](#示例)
* [对比](#对比)
* [版本](#版本)
* [参考](#参考)
* [协议](#协议)
* [作者](#作者)
* [致谢](#致谢)
* [捐赠](#捐赠)

****

## 介绍

**JStarCraft Core是一个面向对象的轻量级框架,遵循Apache 2.0协议.**

JStarCraft Core是一个基于Java语言的核心编程工具包,涵盖了缓存,编解码,通讯,事件,输入/输出,监控,存储,配置,脚本和事务10个方面.

目标是作为搭建其它框架或者项目的基础.

****

## 架构

JStarCraft Core框架各个模块之间的关系:
![core](https://github.com/HongZhaoHua/jstarcraft-tutorial/blob/master/core/JStarCraft%E6%A0%B8%E5%BF%83%E6%A1%86%E6%9E%B6%E7%BB%84%E4%BB%B6%E5%9B%BE.png "JStarCraft Core架构")

| 模块 | 功能 | 依赖 |
| :----: | :----: | :----: |
| core-cache | 提供各种缓存机制 | core-storage |
| core-codec | 提供各种编解码机制 | core-common |
| core-common | 提供各种通用工具 | |
| core-communication | 提供各种通讯机制 | core-codec |
| core-event | 提供各种事件机制 | core-codec |
| core-io | 提供各种输入/输出机制 | core-common |
| core-monitor | 提供各种监控机制 | core-common |
| core-resource | 提供各种资源机制 | core-codec |
| core-script | 提供各种脚本机制 | core-common |
| core-storage | 提供各种存储机制 | core-codec |
| core-transaction | 提供各种事务机制 | core-common |

****

## 特性

* [1.缓存(cache)](https://github.com/HongZhaoHua/jstarcraft-core/wiki/%E7%BC%93%E5%AD%98)
    * Cache Aside
    * Cache as Record
* 2.编解码(codec)
    * 字节
        * Avro
        * CBOR
        * Hessian
        * Ion
        * Kryo
        * MessagePack
        * Standard
        * Thrift
    * 字符
        * CSV
        * JSON
        * XML
        * YAML
* 3.通用(common)
    * 编译(compilation)
    * 转换(conversion)
    * 标识(identification)
    * 日期与时间(instant)
        * 间隔
        * 阳历
        * 阴历(伊斯兰历)
        * 阴阳历(农历)
        * 节气
    * 锁(lockable)
        * 链锁
        * 哈希锁
    * 反射(reflection)
    * 安全(security)
    * 选择(selection)
        * CSS
            HTML
        * JSONPath
            JSON
        * Regular
            Text
        * XPath
            HTML
            Swing
            XML
* 4.通讯(communication)
    * TCP
    * UDP
* 5.事件(event)
    * 模式
       * 队列(queue)
       * 主题(topic)
    * 组件/协议
        * AMQP
        * JMS
        * Kafka
        * Memory
        * MQTT
        * QMQ
        * RabbitMQ
        * Redis
        * RocketMQ
        * STOMP
        * Vert.x
* 6.输入/输出(io)
    * 流
        * Disk
        * FTP
        * Git
        * HDFS
        * SVN
        * ZooKeeper
* 7.监控(monitor)
    * 链路(link)
        * 日志(log)
            * Commons Logging
            * Console
            * JBoss Logging
            * JDK Logging
            * Log4j 1.x
            * Log4j 2.x
            * SLF4J
            * tinylog
    * 度量(measure)
    * 路由(route)
        * 数据路由
        * 一致性哈希
    * 节流(throttle)
* 8.资源(resource)
    * 格式
        * CSV
        * Excel
        * JSON
        * Properties
        * XLSX
        * XML
        * YAML
    * 路径
        * ClassPath
        * Cloud
        * HTTP
        * IO
        * ZIP
* 9.脚本(script)
    * BeanShell
    * Groovy
    * JS
    * Kotlin
    * Lua
    * MVEL
    * PHP
    * Python
    * Ruby
* [10.存储(storage)](https://github.com/HongZhaoHua/jstarcraft-core/wiki/%E5%AF%B9%E8%B1%A1%E5%85%B3%E7%B3%BB%E6%98%A0%E5%B0%84)
    * 标识管理
    * 键值数据库(Berkeley DB)
    * 文档数据库(Elasticsearch/Lucene/Mongo DB)
    * 关系数据库(Hibernate/MyBatis)
    * 图数据库(Neo4j)
* 11.事务(transaction)
    * 分布式锁
        * Cassandra
        * Consul
        * Elasticsearch
        * etcd
        * Hazelcast
        * Hibernate
        * Mongo
        * Redis
        * ZooKeeper

****

## 安装

****

## 使用

****

## 概念

****

## 示例

****

## 对比

****

## 版本

****

## 参考

****

## 协议

****

## 作者

| 作者 | 洪钊桦 |
| :----: | :----: |
| E-mail | 110399057@qq.com, jstarcraft@gmail.com |

****

## 致谢

****

## 捐赠

****
