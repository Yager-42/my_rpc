package core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import common.enumerate.RpcError;
import core.loadbalancer.LoadBalancer;
import core.loadbalancer.RandomLoadBalancer;
import exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceDiscovery implements ServiceDiscovery{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if(loadBalancer==null)  this.loadBalancer = new RandomLoadBalancer();
        else this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName, String group) {
        try{
            List<Instance> instances = NacosUtil.getAllInstance(serviceName, group);
            if(instances.size()==0){
                logger.error("找不到对应的服务: " + serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);
            InetSocketAddress resAddress =  new InetSocketAddress(instance.getIp().substring(1),instance.getPort());
            return resAddress;
        }catch (NacosException e){
            logger.error("获取服务时有错误发生:", e);
        }
        return null;
    }
}
