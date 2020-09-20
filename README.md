# Session控制器

#### 基础环境 

- JDK: 版本1.8
- Maven: 版本3.5及以上
- IDEA集成开发环境

#### 构建项目

项目拉取到本地后，进入到项目根目录，执行下面命令进行项目构建及测试运行

```shell
mvn clean verify
```

> 完成构建后，整个项目的环境就准备完成了，并且会在target/generated-sources/jaxb/com/lee/generate目录下根据定义的schema生成对应的Java对象

#### 启动http服务器

在包 `com.lee` 下执行 `Server.class` 的 `main` 方法，启动服务器，http服务器对所有的请求会回复200，并打印相关的连接信息，启动成功后会打印一下信息

```shell
2020-09-20 22:52:22 [main] INFO  [com.lee.Server] - Server started on port 8081
```

#### 启动Session控制器（Client）

在包 `com.lee` 下执行 `Client.class` 的 `main` 方法，客户端将会启动，启动成功后会打印以下信息

```shell
请输入固定指令指令执行session操作：
查看Session: ls
添加Session: 
1.添加单个session：add sessionId timeout(负数表示不过期） -> add 1000 2000（创建一个id为1000，过期时间为2秒的session）
2.并发添加多个session：add session个数 -> add 10（创建10个Session，默认不过期）
修改Session过期时间: 
modify sessionId timeout -> modify 1000 5000(负数表示不过期)（修改Id为1000的Session过期时间为5秒后）
q退出程序，退出前会将所有Session关闭
```

> Client的操作采用命令行的输入进行操作，动态的改变Session的行为（新建，关闭）

#### Session控制器操作

#### 创建一个Session

创建一个Session可以使用add命令，下面创建了一个SessionId为1234的会话，过期的时间为5秒

```shell
$ add 1234 5000
```

- 客户端日志

  客户端会打印如下信息

```shell
2020-09-20 23:00:30 [session-01-thread-1] INFO  [com.lee.api.RestfulService] - Session1234创建成功！请求开始时间[2020-09-20 23:00:28],请求url[http://127.0.0.1:8081/nbi/deliverysession?id=1234],请求体[{"action":"START","deliverySessionId":1234,"startTime":1600614028318,"stopTime":-1,"version":"1.0.0"}],Session状态[新建]
2020-09-20 23:00:30 [session-01-thread-1] INFO  [com.lee.manager.SessionManager] - 创建Session1234成功，当前存活的Session个数为1个
2020-09-20 23:00:30 [session-01-thread-1] INFO  [com.lee.manager.SessionManager] - Session1234设置的存活时间为5秒，将会在2020-09-20 23:00:35关闭，剩余时间0分5秒
```

​	5秒后，会打印过期的日志

```shell
2020-09-20 23:00:35 [httpSession-stop-timer] INFO  [com.lee.manager.SessionManager] - Session1234存活时间到期，将会被自动删除
2020-09-20 23:00:36 [httpSession-stop-timer] INFO  [com.lee.api.RestfulService] - Session1234停止成功！请求开始时间[2020-09-20 23:00:35],请求url[http://127.0.0.1:8081/nbi/deliverysession?id=1234],请求体[{"action":"STOP","deliverySessionId":1234,"startTime":1600614035588,"stopTime":1600614035587,"version":"1.0.0"}],Session状态[停止]
2020-09-20 23:00:36 [httpSession-stop-timer] INFO  [com.lee.manager.SessionManager] - 删除Session1234成功，当前存活的Session个数为0个
```

- 服务端日志

```shell
2020-09-20 23:00:30 [pool-1-thread-1] INFO  [com.lee.Server] - [Session1234]连接，当前Session[1]个
2020-09-20 23:00:36 [pool-1-thread-3] INFO  [com.lee.Server] - [Session1234]断开，服务器当前Session[0]个
```

#### 批量创建Session

批量创建Session的时候，会使用默认的SessionId进行创建，Id是递增的，创建的过程是非阻塞并发执行的。通过修改add后的数量参数，就可以控制并发数

下面命令创建5个Session，创建的Session默认是不会过期的。

```shell
$ add 5
```

- 客户端日志

```shell
2020-09-20 23:03:41 [session-01-thread-3] INFO  [com.lee.api.RestfulService] - Session100002创建成功！请求开始时间[2020-09-20 23:03:39],请求url[http://127.0.0.1:8081/nbi/deliverysession?id=100002],请求体[{"action":"START","deliverySessionId":100002,"startTime":1600614218903,"stopTime":-1,"version":"1.0.0"}],Session状态[新建]
2020-09-20 23:03:41 [session-01-thread-5] INFO  [com.lee.api.RestfulService] - Session100004创建成功！请求开始时间[2020-09-20 23:03:39],请求url[http://127.0.0.1:8081/nbi/deliverysession?id=100004],请求体[{"action":"START","deliverySessionId":100004,"startTime":1600614218903,"stopTime":-1,"version":"1.0.0"}],Session状态[新建]
2020-09-20 23:03:41 [session-01-thread-4] INFO  [com.lee.api.RestfulService] - Session100003创建成功！请求开始时间[2020-09-20 23:03:39],请求url[http://127.0.0.1:8081/nbi/deliverysession?id=100003],请求体[{"action":"START","deliverySessionId":100003,"startTime":1600614218903,"stopTime":-1,"version":"1.0.0"}],Session状态[新建]
2020-09-20 23:03:41 [session-01-thread-1] INFO  [com.lee.api.RestfulService] - Session100000创建成功！请求开始时间[2020-09-20 23:03:39],请求url[http://127.0.0.1:8081/nbi/deliverysession?id=100000],请求体[{"action":"START","deliverySessionId":100000,"startTime":1600614218903,"stopTime":-1,"version":"1.0.0"}],Session状态[新建]
2020-09-20 23:03:41 [session-01-thread-5] INFO  [com.lee.manager.SessionManager] - 创建Session100004成功，当前存活的Session个数为2个
2020-09-20 23:03:41 [session-01-thread-3] INFO  [com.lee.manager.SessionManager] - 创建Session100002成功，当前存活的Session个数为2个
2020-09-20 23:03:41 [session-01-thread-4] INFO  [com.lee.manager.SessionManager] - 创建Session100003成功，当前存活的Session个数为3个
2020-09-20 23:03:41 [session-01-thread-2] INFO  [com.lee.api.RestfulService] - Session100001创建成功！请求开始时间[2020-09-20 23:03:39],请求url[http://127.0.0.1:8081/nbi/deliverysession?id=100001],请求体[{"action":"START","deliverySessionId":100001,"startTime":1600614218903,"stopTime":-1,"version":"1.0.0"}],Session状态[新建]
2020-09-20 23:03:41 [session-01-thread-1] INFO  [com.lee.manager.SessionManager] - 创建Session100000成功，当前存活的Session个数为4个
2020-09-20 23:03:41 [session-01-thread-2] INFO  [com.lee.manager.SessionManager] - 创建Session100001成功，当前存活的Session个数为5个
```

- 服务端日志

```shell
2020-09-20 23:03:41 [pool-1-thread-6] INFO  [com.lee.Server] - [Session100001]连接，当前Session[1]个
2020-09-20 23:03:41 [pool-1-thread-5] INFO  [com.lee.Server] - [Session100003]连接，当前Session[2]个
2020-09-20 23:03:41 [pool-1-thread-7] INFO  [com.lee.Server] - [Session100000]连接，当前Session[3]个
2020-09-20 23:03:41 [pool-1-thread-9] INFO  [com.lee.Server] - [Session100004]连接，当前Session[4]个
2020-09-20 23:03:41 [pool-1-thread-8] INFO  [com.lee.Server] - [Session100002]连接，当前Session[5]个
```

#### 动态修改Session的过期时间

Session在未过期之前可以重新定义过期时间，下面命令重新设置了id为100001的Session的过期时间，设置完成后，会重置对应Session的过期时间

```shell
$ modify 100001 30000
```

修改后日志

```shell
2020-09-20 23:11:12 [main] INFO  [com.lee.manager.SessionManager] - Session100001重置了过期时间
2020-09-20 23:11:12 [main] INFO  [com.lee.manager.SessionManager] - Session100001设置的存活时间为30秒，将会在2020-09-20 23:11:42关闭，剩余时间0分29秒
```

过期的后日志

```shell
2020-09-20 23:11:42 [httpSession-stop-timer] INFO  [com.lee.manager.SessionManager] - Session100001存活时间到期，将会被自动删除
2020-09-20 23:11:43 [httpSession-stop-timer] INFO  [com.lee.api.RestfulService] - Session100001停止成功！请求开始时间[2020-09-20 23:11:42],请求url[http://127.0.0.1:8081/nbi/deliverysession?id=100001],请求体[{"action":"STOP","deliverySessionId":100001,"startTime":1600614702369,"stopTime":1600614702368,"version":"1.0.0"}],Session状态[停止]
2020-09-20 23:11:43 [httpSession-stop-timer] INFO  [com.lee.manager.SessionManager] - 删除Session100001成功，当前存活的Session个数为4个
```

再次对相同的Session进行操作，会给出错误提示

```shell
$ modify 100001 30000
指令错误: 重置的Session100001不存在
```

#### 查看Session状态

使用 `ls` 命令可以查看所有未过期Session的状态

```shell
$ ls
当前Session个数4个
SessionId: 100000 过期时间: 不过期 剩余时间：不过期
SessionId: 100002 过期时间: 不过期 剩余时间：不过期
SessionId: 100003 过期时间: 不过期 剩余时间：不过期
SessionId: 100004 过期时间: 不过期 剩余时间：不过期
```

#### 退出Session管理器

使用`q` 命令退出Session管理器，并在退出之前关闭所有的Session

```shell
$ q
2020-09-20 23:15:27 [main] INFO  [com.lee.manager.SessionManager] - sessionManager即将关闭
2020-09-20 23:15:28 [session-01-thread-3] INFO  [com.lee.api.RestfulService] - Session100004停止成功！请求开始时间[2020-09-20 23:15:27],请求url[http://127.0.0.1:8081/nbi/deliverysession?id=100004],请求体[{"action":"STOP","deliverySessionId":100004,"startTime":1600614927378,"stopTime":-1,"version":"1.0.0"}],Session状态[停止]
2020-09-20 23:15:28 [session-01-thread-3] INFO  [com.lee.manager.SessionManager] - 删除Session100004成功，当前存活的Session个数为3个
2020-09-20 23:15:28 [session-01-thread-7] INFO  [com.lee.api.RestfulService] - Session100002停止成功！请求开始时间[2020-09-20 23:15:27],请求url[http://127.0.0.1:8081/nbi/deliverysession?id=100002],请求体[{"action":"STOP","deliverySessionId":100002,"startTime":1600614927380,"stopTime":-1,"version":"1.0.0"}],Session状态[停止]
2020-09-20 23:15:28 [session-01-thread-7] INFO  [com.lee.manager.SessionManager] - 删除Session100002成功，当前存活的Session个数为2个
2020-09-20 23:15:28 [session-01-thread-8] INFO  [com.lee.api.RestfulService] - Session100003停止成功！请求开始时间[2020-09-20 23:15:27],请求url[http://127.0.0.1:8081/nbi/deliverysession?id=100003],请求体[{"action":"STOP","deliverySessionId":100003,"startTime":1600614927380,"stopTime":-1,"version":"1.0.0"}],Session状态[停止]
2020-09-20 23:15:28 [session-01-thread-6] INFO  [com.lee.api.RestfulService] - Session100000停止成功！请求开始时间[2020-09-20 23:15:27],请求url[http://127.0.0.1:8081/nbi/deliverysession?id=100000],请求体[{"action":"STOP","deliverySessionId":100000,"startTime":1600614927380,"stopTime":-1,"version":"1.0.0"}],Session状态[停止]
2020-09-20 23:15:28 [session-01-thread-8] INFO  [com.lee.manager.SessionManager] - 删除Session100003成功，当前存活的Session个数为1个
2020-09-20 23:15:28 [session-01-thread-6] INFO  [com.lee.manager.SessionManager] - 删除Session100000成功，当前存活的Session个数为0个
```

