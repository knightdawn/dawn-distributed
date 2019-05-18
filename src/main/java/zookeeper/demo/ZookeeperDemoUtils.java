package zookeeper.demo;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 *
 * Date: 2019年5月18日<br/>
 * 
 * @author sugengbin
 */
public class ZookeeperDemoUtils {

	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	private static ZooKeeper zk;
	private static final String ZOOKEEPER_ZNODE_NAME = "zookeeper";

	/**
	 * zk 启动
	 * 
	 * @throws Exception
	 */
	public static void startZK(String host) throws Exception {
		System.out.println("startZK----------------------start");
		// 确保server确实已经开启了，这里是创建client到server的session
		zk = new ZooKeeper(host, 20000, new Watcher() {
			@Override
			public void process(WatchedEvent watchedEvent) {
				System.out.println("process " + watchedEvent);
				if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
					countDownLatch.countDown();
				}
			}
		});
		try {
			countDownLatch.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("state is " + zk.getState());
		System.out.println("zk session begin");
		System.out.println("startZK----------------------end");
	}

	/**
	 * （同步）创建临时节点： create 方式
	 */
	public static void syncCreateNode() {
		System.out.println("syncCreateNode----------------------start");
		try {
			/*
			 * /test1 path "znode1".getBytes() 内容 OPEN_ACL_UNSAFE fixme
			 * EPHEMERAL 客户端断开时移除节点 & PERSISTENT（持久化）
			 */
			String path1 = zk.create("/test1", "znode1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			String path2 = zk.create("/test2", "znode2".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			System.out.println("path1 = " + path1);
			System.out.println("path2 = " + path2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("syncCreateNode----------------------end");
	}

	/**
	 * （异步）创建临时节点： create 方式
	 * 
	 * @throws Exception
	 */
	public static void asnycCreateNode() throws Exception {
		System.out.println("asnycCreateNode----------------------start");
		zk.create("/test3", "znode3".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
				new IStringCallBack(), "context");
		System.out.println("asnycCreateNode----------------------end");
	}

	static class IStringCallBack implements AsyncCallback.StringCallback {
		@Override
		public void processResult(int rc, String path, Object ctx, String name) {
			System.out.println("Create path result " + rc + " " + path + " " + ctx + " " + name);
		}
	}

	/**
	 *  删除节点
	 * @throws Exception
	 */
	public static void clear() throws Exception {
		List<String> childList = zk.getChildren("/", false);
		for (String s : childList) {
			if (s.equals(ZOOKEEPER_ZNODE_NAME))
				continue;
			zk.delete("/" + s, -1);
		}
	}

	/**
	 * 获取子节点列表，在子节点列表变更时触发
	 * 
	 * @throws Exception
	 */
	public static void getChildren() throws Exception {
		System.out.println("getChildren----------------------");
		List<String> childList = zk.getChildren("/", new Watcher() {
			@Override
			// 这个znode的子节点变化的时候会收到通知
			public void process(WatchedEvent watchedEvent) {
				System.out.println("getChildren " + watchedEvent);
			}
		});
		System.out.println("childList " + childList);
	}

	/**
	 * 获取数据，注册监听器，在znode内容被改变时触发
	 * 
	 * @throws Exception
	 */
	public static void getData() throws Exception {
		System.out.println("getData----------------------");
		String ans1 = new String(zk.getData("/test1", false, null));
		String ans2 = new String(zk.getData("/test2", new Watcher() {
			@Override
			public void process(WatchedEvent watchedEvent) {
				System.out.println("getData " + watchedEvent);
			}
		}, null));
		System.out.println("znode /test1 content is " + ans1);
		System.out.println("znode /test2 content is " + ans2);
	}

	/**
	 * 更新内容，会触发对应znode的watch事件
	 * 
	 * @throws Exception
	 */
	public static void setData() throws Exception {
		System.out.println("setData----------------------");
		String data = "zNode22";
		zk.setData("/test2", data.getBytes(), -1);
		String ans2 = new String(zk.getData("/test2", false, null));
		System.out.println("setData to " + ans2);
	}

	/**
	 * 节点是否存在，watch监听节点的创建，删除以及更新
	 * 
	 * @throws Exception
	 */
	public static void exists() throws Exception {
		System.out.println("exists----------------------");
		Stat stat = zk.exists("/test2", new Watcher() {
			@Override
			public void process(WatchedEvent watchedEvent) {
				System.out.println("exists " + watchedEvent);
			}
		});
		System.out.println("stat is " + stat);
	}

	public static void delete() throws Exception {
		System.out.println("delete----------------------");
		zk.delete("/test2", -1);
		zk.delete("/test1", -1);
	}
}
