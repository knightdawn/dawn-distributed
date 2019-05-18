package zookeeper.demo;

import static zookeeper.demo.ZookeeperDemoUtils.exists;
import static zookeeper.demo.ZookeeperDemoUtils.startZK;
import static zookeeper.demo.ZookeeperDemoUtils.syncCreateNode;
/**
 *
 * Date: 2019年5月18日<br/>
 * 
 * @author sugengbin
 */
public class ZkTest {

	public static void main(String[] args) throws Exception {
		String host = "127.0.0.1:2181";
		// 1、 同步 create
//		startZK();
//	    syncCreateNode();
		
		// 2、 异步 create
//		startZK();
//		asnycCreateNode();
		
		// 3、getChildren 【监控】子节点列表变化情况  None (-1), NodeCreated (1), NodeDeleted (2), NodeDataChanged (3), NodeChildrenChanged (4);
//		startZK();
//		getChildren();
//		syncCreateNode();
		/**
		 *  getChildren----------------------
			childList [cluster, controller_epoch, controller, brokers, zookeeper, admin, isr_change_notification, consumers, latest_producer_id_block, config]
			syncCreateNode----------------------
			getChildren WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/
			path1 = /test1
			path2 = /test2
		 */
		
		// 4、getData 【监控】节点内容变化情况
//		startZK();
//		syncCreateNode();
//		getData(); // 监控
//		setData(); // 修改内容触发监控
		/**
		 *  syncCreateNode----------------------
			path1 = /test1
			path2 = /test2
			getData----------------------
			znode /test1 content is znode1
			znode /test2 content is znode2
			setData----------------------
			getData WatchedEvent state:SyncConnected type:NodeDataChanged path:/test2
			setData to zNode22
		 */
		
		// 5、exists 【监控】节点的增删改
		startZK(host);
		exists();
		syncCreateNode();
		/**
		 *  exists----------------------
			stat is null
			syncCreateNode----------------------
			exists WatchedEvent state:SyncConnected type:NodeCreated path:/test2
			path1 = /test1
			path2 = /test2
		 */
		
		Thread.sleep(100000);
	}
}
