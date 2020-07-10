package com.zp.zk.config;
import org.apache.zookeeper.ZooKeeper;
import java.util.concurrent.CountDownLatch;
/**
 * @author zhaopeng
 * @create 2020-07-07 16:42
 */
public class ZkUtils {
    private static final String address="192.168.150.61:2181,192.168.150.62:2181,192.168.150.63:2181/testConf";
    //默认的watch,是session级别的，不是path级别的
    private static DefaultWatch watch=new DefaultWatch();
    private static ZooKeeper zk;
    private static CountDownLatch cdl=new CountDownLatch(1);
    public static ZooKeeper getZk(){
        try {
            //watch是session级别的
            zk=new ZooKeeper(address,1000,watch);
            //将cdl传递给defaultwatch
            watch.setCdl(cdl);

            //一直等待着，知道监听器连接成功就返=继续执行
            cdl.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zk;
    }
}
