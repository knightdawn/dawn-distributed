
### 1、概念
分布式协调服务
集群的管理者，监视着集群中各个节点的状态根据节点提交的反馈进行下一步合理操作

- 客户端的读请求可以被集群中的任意一台机器处理
- 客户端的写请求，这些请求会同时发给其他zookeeper机器并且达成一致后，请求才会返回成功
- 集群机器增多，读请求的吞吐会提高但是写请求的吞吐会下降
- 有序性是zookeeper中非常重要的一个特性，所有的更新都是全局有序的，每个更新都有一个唯一的时间戳，这个时间戳称为zxid（Zookeeper Transaction Id）

### 2、提供
文件系统
- 多层级的节点命名空间（节点称为znode）。与文件系统不同的是，这些节点都可以设置关联的数据
- 不能用于存放大量的数据，每个节点的存放数据上限为1M

通知机制


### node 类型
1、PERSISTENT-持久化目录节点 
客户端与zookeeper断开连接后，该节点依旧存在 
2、PERSISTENT_SEQUENTIAL-持久化顺序编号目录节点
客户端与zookeeper断开连接后，该节点依旧存在，只是Zookeeper给该节点名称进行顺序编号 
3、EPHEMERAL-临时目录节点
客户端与zookeeper断开连接后，该节点被删除 
4、EPHEMERAL_SEQUENTIAL-临时顺序编号目录节点
客户端与zookeeper断开连接后，该节点被删除，只是Zookeeper给该节点名称进行顺序编号

### 分布式锁
锁服务可以分为两类，一个是保持独占，另一个是控制时序。 
对于第一类，我们将zookeeper上的一个znode看作是一把锁，通过createznode的方式来实现。所有客户端都去创建 /distribute_lock 节点，最终成功创建的那个客户端也即拥有了这把锁。用完删除掉自己创建的distribute_lock 节点就释放出锁


### 数据复制
- 提供最终一致性服务，所有机器间做数据复制
1、容错
2、提高系统的扩展能力 
3、提高性能，就近访问

- 写任意(Write Any)：对数据的修改可提交给任意的节点
通过增加机器，它的读吞吐能力和响应能力扩展性非常好，而写，随着机器的增多吞吐能力肯定下降（这也是它建立observer的原因），而响应能力则取决于具体实现方式，是延迟复制保持最终一致性，还是立即复制快速响应

### Zab协议
原子广播，这个机制保证了各个Server之间的同步。实现这个机制的协议叫做Zab协议。Zab协议有两种模式，它们分别是恢复模式（选主）和广播模式（同步）


### 保证事务的顺序一致性
递增的事务Id来标识，所有的proposal（提议）都在被提出的时候加上了zxid

### Server工作状态
LOOKING：当前Server不知道leader是谁，正在搜寻
LEADING：当前Server即为选举出来的leader
FOLLOWING：leader已经选举出来，当前Server与之同步

### 选leader 
当leader崩溃或者leader失去大多数的follower，这时zk进入恢复模式，恢复模式需要重新选举出一个新的leader，让所有的Server都恢复到一个正确的状态。Zk的选举算法有两种：一种是基于basic paxos实现的，另外一种是基于fast paxos算法实现的。系统默认的选举算法为fast paxos

### 为什么会有leader
在分布式环境中，有些业务逻辑只需要集群中的某一台机器进行执行，其他的机器可以共享这个结果，这样可以大大减少重复计算，提高性能，于是就需要进行leader选举

### ACL
ACL（Access Control List）访问控制列表
包括三个方面：

- 权限模式（Scheme）
IP：从IP地址粒度进行权限控制
Digest：最常用，用类似于 username:password 的权限标识来进行权限配置，便于区分不同应用来进行权限控制
World：最开放的权限控制方式，是一种特殊的digest模式，只有一个权限标识“world:anyone”
Super：超级用户
- 授权对象
授权对象指的是权限赋予的用户或一个指定实体，例如IP地址或是机器灯。

- 权限 Permission
CREATE：数据节点创建权限，允许授权对象在该Znode下创建子节点
DELETE：子节点删除权限，允许授权对象删除该数据节点的子节点
READ：数据节点的读取权限，允许授权对象访问该数据节点并读取其数据内容或子节点列表等
WRITE：数据节点更新权限，允许授权对象对该数据节点进行更新操作
ADMIN：数据节点管理权限，允许授权对象对该数据节点进行ACL相关设置操作

### 客户端注册Watcher实现
调用getData()/getChildren()/exist()三个API，传入Watcher对象
标记请求request，封装Watcher到WatchRegistration
封装成Packet对象，发服务端发送request
收到服务端响应后，将Watcher注册到ZKWatcherManager中进行管理
请求返回，完成注册。

### 服务端处理Watcher实现
接收到客户端请求，处理请求判断是否需要注册Watcher，需要的话将数据节点的节点路径和ServerCnxn（ServerCnxn代表一个客户端和服务端的连接，实现了Watcher的process接口，此时可以看成一个Watcher对象）存储在WatcherManager的WatchTable和watch2Paths中去

### 服务器角色
- Leader
事务请求的唯一调度和处理者，保证集群事务处理的顺序性
集群内部各服务的调度者
- Follower
处理客户端的非事务请求，转发事务请求给Leader服务器
参与事务请求Proposal的投票
参与Leader选举投票
- Observer
3.3.0版本以后引入的一个服务器角色，在不影响集群事务处理能力的基础上提升集群的非事务处理能力

处理客户端的非事务请求，转发事务请求给Leader服务器
不参与任何形式的投票


### zk自带的zkclient及Apache开源的Curator






