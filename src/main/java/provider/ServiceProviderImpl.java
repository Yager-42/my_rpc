package provider;

import common.enumerate.RpcError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import exception.RpcException;

public class ServiceProviderImpl implements ServiceProvider {
    private static Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private static final Map<String,Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void addServiceProvider(T service) {
        String servicename = service.getClass().getName();
        if(registeredService.contains(servicename)){
            return;
        }else{
            registeredService.add(servicename);
        }
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length==0){
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for(Class<?> i:interfaces){
            serviceMap.put(i.getCanonicalName(),service);
        }
        logger.info("向接口: {} 注册服务: {}", interfaces, servicename);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
