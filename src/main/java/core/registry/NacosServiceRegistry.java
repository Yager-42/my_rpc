package core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import common.enumerate.RpcError;
import exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.NacosUtil;

import java.net.InetSocketAddress;

public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    public final NamingService namingService;

    public NacosServiceRegistry() {
        this.namingService = NacosUtil.getNacosNamingService();
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress,String group) {
        try{
            NacosUtil.registerService(serviceName, inetSocketAddress,group);
        }catch (NacosException e){
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
}
