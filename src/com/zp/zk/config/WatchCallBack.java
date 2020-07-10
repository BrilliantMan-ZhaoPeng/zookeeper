package com.zp.zk.config;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;

/**
 * 几个Callback集成到一个类中
 * @author zhaopeng
 * @create 2020-07-07 17:13
 */
public class WatchCallBack implements Watcher,AsyncCallback.StatCallback,AsyncCallback.DataCallback {

    ZooKeeper zk;

    MyConf conf;//存放配置的类

    CountDownLatch cdl=new CountDownLatch(1);

    public MyConf getConf() {
        return conf;
    }

    public void setConf(MyConf conf) {
        this.conf = conf;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }


    //zk.getData
    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if(data!=null){
            String s = new String(data);
            System.out.println("zk.getData():"+s);
            //得到值，设置值
            conf.setConf(s);
            //countDown-1,表示取值成功了
            cdl.countDown();
        }
    }

    //zk.exits
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        //当stat不为空，说明节点路径是存在的
        if(stat != null){
            System.err.println("节点存在....");
            //直接获取值
            zk.getData("/AppConf",this,this,"asdas");
        }else{
            System.err.println("节点不存在....");
        }
    }

    //等待着，去取值就完事了
    public void await(){
            zk.exists("/AppConf", this,this,"ABC");
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }


    //watcher 监听节点被修改
    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                zk.getData("/AppConf",this,this,"asdas");
                break;
            case NodeDeleted:
                //节点删除了咋个搞??
                conf.setConf("");
                cdl=new CountDownLatch(1);
                break;
            case NodeDataChanged:
                //节点数据改变了怎么办？？
                zk.getData("/AppConf",this,this,"asdas");
                break;
            case NodeChildrenChanged:
                break;
        }
    }
}
