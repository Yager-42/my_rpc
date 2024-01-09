package fuse;

import common.enumerate.FuseState;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class ServiceState {
    /**
     * 服务名
     */
    private String serviceName;
    /**
     * 请求数量
     */
    private AtomicInteger request;
    /**
     * 异常的请求数量
     */
    private AtomicInteger excepts;
    /**
     * 当前服务熔断状态
     */
    private FuseState fuseState;

    /**
     * 当前服务拦截lv
     * @param serviceName
     */
    private float interceptRate;

    private float k;

    private static Logger log = LoggerFactory.getLogger(ServiceState.class);

    public ServiceState(String serviceName,float k) {
        this.serviceName = serviceName;
        this.request = new AtomicInteger(0);
        this.excepts = new AtomicInteger(0);
        this.interceptRate = 0;
        this.fuseState = FuseState.CLOSE;
        this.k = k;
    }

    public void incrRequest(){
        request.incrementAndGet();
        int requestNum = request.incrementAndGet();
        int exceptsNum = excepts.incrementAndGet();
        if( this.fuseState != FuseState.CLOSE){
            float rate = (float) ((requestNum - this.k*(requestNum - exceptsNum))/(requestNum + 1));
            this.interceptRate = rate;
            if(rate < 0.1f) {
                this.fuseState = FuseState.CLOSE;
            }
        }

    }

    public void incrExcepts(){
        int requestNum = request.incrementAndGet();
        int exceptsNum = excepts.incrementAndGet();
//        此外拦截率，当拦截率超过0.3时进入半开模式，同时开启拦截模式，即请求会被一定概率拦截，概率为拦截率。
//        k值越大，则越不容易开启拦截，k值越小，则越容易开启拦截
        float rate = (float) ((requestNum - this.k*(requestNum - exceptsNum))/(requestNum + 1));

        log.info("rate="+rate);
        this.interceptRate = rate;
        if(rate < 0.1f){
            this.fuseState = FuseState.CLOSE;
        }else if(rate > 0.3f){
            this.fuseState = FuseState.HALF_OPEN;
        }else if(rate > 0.9f){
            this.fuseState = FuseState.FALL_OPEN;
        }


    }
}
