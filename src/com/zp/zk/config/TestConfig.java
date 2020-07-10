package com.zp.zk.config;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * @author zhaopeng
 * @create 2020-07-07 16:42
 */
public class TestConfig {

    ZooKeeper zk;

    @Before
    public void conn() {
        zk=ZkUtils.getZk();
    }

    @After
    public void close(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getConf(){
        WatchCallBack watchCallBack=new WatchCallBack();
        watchCallBack.setZk(zk);
        MyConf conf=new MyConf();
        watchCallBack.setConf(conf);
        //等待着
        watchCallBack.await();
        //1.节点不存在
        //2.节点存在
        while(true){
            if(conf.getConf().equals("")){
                System.err.println("conf delete...");
                watchCallBack.await();
            }else{
                System.err.println(conf.getConf());
            }
        }
    }
}
