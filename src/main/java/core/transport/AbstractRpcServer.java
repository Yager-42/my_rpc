package core.transport;

import annotation.Service;
import annotation.ServiceScan;
import common.enumerate.RpcError;
import core.registry.ServiceRegistry;
import exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import provider.ServiceProvider;
import util.ReflectUtil;

import java.net.InetSocketAddress;
import java.util.Set;

public abstract class AbstractRpcServer implements RpcServer{
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanService(){
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try{
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)){
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        }catch (ClassNotFoundException e){
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        String basePacketge = startClass.getAnnotation(ServiceScan.class).value();
        if(basePacketge.equals("")){
            basePacketge = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePacketge);
        for(Class<?> clazz:classSet){
            if(clazz.isAnnotationPresent(Service.class)){
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if(serviceName.equals("")){
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> a_interface:interfaces){
                        publishService(obj,a_interface.getCanonicalName());
                    }
                }else{
                    publishService(obj,serviceName);
                }
            }
        }
    }

    @Override
    public <T> void publishService(T service, String serviceName,String group) {
        this.serviceProvider.addServiceProvider(service, serviceName);
        this.serviceRegistry.register(serviceName, new InetSocketAddress(host, port),group);
    }

    @Override
    public <T> void publishService(T service, String serviceName) {
        this.serviceProvider.addServiceProvider(service, serviceName);
        this.serviceRegistry.register(serviceName, new InetSocketAddress(host, port),"");
    }
}
