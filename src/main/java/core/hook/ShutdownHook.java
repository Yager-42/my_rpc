package core.hook;

import factory.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.NacosUtil;

import java.util.concurrent.ExecutorService;

public class ShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    //其中shutdownHook是一个已初始化但并没有启动的线程，
    // 当jvm关闭的时候，会执行系统中已经设置的所有通过方法addShutdownHook添加的钩子，
    // 当系统执行完这些钩子后，jvm才会关闭。
    public void addClearAllHook() {
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
