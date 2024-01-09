package fuse;

import io.netty.channel.Channel;
import lombok.Data;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Data
public class FuseProtector {

    public ConcurrentHashMap<String, ServiceState> serviceStateCache = new ConcurrentHashMap<>();


    private float k = 1.5f;


    /**
     * 检查当前熔断状态，许可发送请求返回true，拦截返回false
     * @param serviceName
     * @return
     */
    public boolean checkService(String serviceName){
        ServiceState serviceState = serviceStateCache.get(serviceName);
        switch (serviceState.getFuseState()) {
            case CLOSE:
                return true;
            case FALL_OPEN:
                return false;
            case HALF_OPEN:
                return Math.random() > serviceState.getInterceptRate();
        }
        return true;
    }


    public void initCache(Map<String, Channel> channels){
        serviceStateCache.clear();
        for (String serviceName : channels.keySet()) {
            ServiceState serviceState = new ServiceState(serviceName,k);
            serviceStateCache.put(serviceName,serviceState);
        }
    }
    @Async
    public void increaseRequest(String serviceName){
        ServiceState serviceState = serviceStateCache.get(serviceName);
        serviceState.incrRequest();
    }

    public void increaseExcepts(String serviceName){
        ServiceState serviceState = serviceStateCache.get(serviceName);
        serviceState.incrExcepts();
    }
}
