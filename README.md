# Guava-Event-Bus 学习笔记
Guava的EventBus源码学习
> 分析者：[Allen](https://github.com/qq291462491)
		
###1. 背景简介
EventBus是google的一个Java工具包其中的一个工具类，类似的有多个版本其中包括移植到Android端的[greenrobot-EventBus](https://github.com/greenrobot/EventBus)
和改良的[square-otto](https://github.com/square/otto)，功能基本都是一样的。此处主要是对Guava版的EventBus分析，关于[guava](https://github.com/google/guava)。

###2. 功能简介     
####2.1 EventBus介绍
####2.2 关键词
**事件(Event)：**又可称为消息，本文中统一用事件表示。其实就是一个对象，可以是网络请求返回的字符串，也可以是某个开关状态等等。`事件类型(EventType)`指事件所属的 Class。  
事件分为一般事件和 Sticky 事件，相对于一般事件，Sticky 事件不同之处在于，当事件发布后，再有订阅者开始订阅该类型事件，依然能收到该类型事件最近一个 Sticky 事件。  

**订阅者(Subscriber)：**订阅某种事件类型的对象。当有发布者发布这类事件后，EventBus 会执行订阅者的 onEvent 函数，这个函数叫`事件响应函数`。订阅者通过 register 接口订阅某个事件类型，unregister 接口退订。订阅者存在优先级，优先级高的订阅者可以取消事件继续向优先级低的订阅者分发，默认所有订阅者优先级都为 0。  

**发布者(Publisher)：**发布某事件的对象，通过 post 接口发布事件。 




```xml 
<dependency>
	<groupId>com.google.guava</groupId>
	<artifactId>guava</artifactId>
	<version>18.0</version>
</dependency>
```	