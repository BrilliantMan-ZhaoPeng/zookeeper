package com.zp.zk.lock;

import com.sun.org.apache.xml.internal.security.utils.JDKXPathAPI;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhaopeng
 * @create 2020-07-10 16:15
 */

public class WacthCallBack implements Watcher,AsyncCallback.StringCallback,AsyncCallback.Children2Callback,AsyncCallback.StatCallback {

    ZooKeeper zk;

    String threaName;

    String pathName;

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    //阻塞
    CountDownLatch cdl=new CountDownLatch(1);

    public void setThreaName(String threaName) {
        this.threaName = threaName;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }


    //session 级别的
    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                //重点关注对象
                zk.getChildren("/",false,this,"asda");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    //尝试锁
    public void tryLock(){
        try {
            zk.create("/lock",threaName.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL,this,"create node");
            //给我等着
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //释放锁
    public void unLock(){
        //直接删除该节点就完事了
        try {
            zk.delete(pathName,-1);
            System.out.println(threaName+" unlock...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }


    //zk.create callBack
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        //如果创建成功
       if(name != null){
           System.err.println(threaName+"->create node path:"+name);
           pathName=name;
           //查看当前我的节点的是不是最小的，是最小的话就说明我就拿到锁了
           zk.getChildren("/",false,this,"asda");
       }
    }


    //zk.getChildren callBack
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        System.out.println(threaName+"->see all locks...");
        //一定要看到自己前面的
      //  for (String child : children) {
            //System.out.println(child);
            //因为直接取出来的是乱序的，所以，需要进行简单的排序操作
        //}
        //先排序操作一波
        Collections.sort(children);
        //查看当前的节点是不是第一个
        int i = children.indexOf(pathName.substring(1));
        if(i==0){//是第一个节点直接拿到锁
            System.out.println(threaName+"->I am first...");
            try {
                zk.setData("/",threaName.getBytes(),-1);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            cdl.countDown();
        }else{//不是的话，去监听前面节点是否存在就完犊子了
            String prePathName = "/";
            prePathName+=children.get(i - 1);
            zk.exists(prePathName,this,this,"asd");
        }
    }


    //zk.exists callBack
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        //判断
    }
}
