package com.zp.zk.lock;

import com.zp.zk.utils.ZkUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.awt.windows.ThemeReader;

/**
 * @author zhaopeng
 * @create 2020-07-10 16:09
 */
public class TestLock {

    ZooKeeper zk;

    @Before
    public void conn(){
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
    public void lock(){
        //启动10个线程
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                WacthCallBack wacthCallBack = new WacthCallBack();
                wacthCallBack.setZk(zk);
                String threadName = Thread.currentThread().getName();
                //设置线程名字
                wacthCallBack.setThreaName(threadName);
                //拿锁
                wacthCallBack.tryLock();
                  //玩
                System.out.println(threadName+"->do something.....");
               /* try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                //释放锁
                wacthCallBack.unLock();
            },"client:"+i).start();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
