package com.zp.zk.utils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import java.util.concurrent.CountDownLatch;
/**
 * @author zhaopeng
 * @create 2020-07-07 16:46
 */
public class DefaultWatch implements Watcher {

    CountDownLatch cdl;

    public void setCdl(CountDownLatch cdl) {
        this.cdl = cdl;
    }

    @Override
    public void process(WatchedEvent event) {
        System.err.println(event.toString());
        switch (event.getState()) {
            case Unknown:
                break;
            case Disconnected:
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
                //监听连接是否成功，成功就countdown
                cdl.countDown();
                System.out.println("connected success......");
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
    }
}
