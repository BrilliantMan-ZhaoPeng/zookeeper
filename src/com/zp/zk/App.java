package com.zp.zk;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import java.util.concurrent.CountDownLatch;
/**
 * @author zhaopeng
 * @create 2020-07-07 15:38
 */
public class App {
    public static void main(String[] args) throws Exception{
        CountDownLatch cdl=new CountDownLatch(1);
        //有两种watcher
           //第一种:new Zookeeper 这是session级别的，跟path,node没得关系
           //第二种:
        ZooKeeper zooKeeper = new ZooKeeper("192.168.150.61:2181,192.168.150.62:2181,192.168.150.63:2181/testConf", 3000, new Watcher() {
            //回调方法
            @Override
            public void process(WatchedEvent event) {
                Event.KeeperState state = event.getState();
                Event.EventType type = event.getType();
                String path = event.getPath();
                System.err.println("new Zookeeper()--->watcher:"+event.toString());
                //查看state的类型
                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("connection success...");
                        //连接成功才放行
                        cdl.countDown();
                        break;
                    case AuthFailed:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        break;
                }
                switch (type) {
                    case None:
                        break;
                    case NodeCreated:
                        break;
                    case NodeDeleted:
                        break;
                    case NodeDataChanged:
                        break;
                    case NodeChildrenChanged:
                        break;
                }
            }
        });

        //等待连接的成功
        cdl.await();

        ZooKeeper.States state = zooKeeper.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("ing.....");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("end......");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        //创建节点
               //两种模式：1.同步的  2.异步的
        //返回创建节点的路径
        String pathName = zooKeeper.create("/ooxx", "oldData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);


        //得到节点,并监听。。。当以后这个节点的数据发生了改变的话，能监听到的
        Stat stat=new Stat();//存放源数据
        //得到数据字节数组
        byte[] data = zooKeeper.getData("/ooxx", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.err.println("node--update:" + event.toString());
                try {
                    //继续注册监听  true调用默认new Zookeeper的监听器
                    byte[] upData = zooKeeper.getData("/ooxx", this, stat);
                    System.err.println("update:"+new String(upData));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, stat);
        System.err.println("getData()-->data:"+new String(data));

        //修改值
        Stat stat1 = zooKeeper.setData("/ooxx", "newData".getBytes(), 0);
        System.err.println("stat1-->version:"+stat1.getVersion());
        Stat stat2 = zooKeeper.setData("/ooxx", "newData1".getBytes(), stat1.getVersion());


        System.out.println("----async start----");
        //异步取值
        zooKeeper.getData("/ooxx", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("----async call back----"+new String(data));
            }
        },"abcdefg");
        System.out.println("----async end----");
        Thread.sleep(5000);
    }
}
