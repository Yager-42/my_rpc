package consumer;

import annotation.RpcReference;
import common.entity.RpcReferenceWrapper;
import core.netty.client.NettyClient;
import core.serializer.CommonSerializer;
import core.transport.RpcClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;

@Configuration
public class RpcClientAutoConfiguration implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(RpcClientAutoConfiguration.class);
    private static NettyClient client;
    private static RpcClientProxy rpcClientProxy;
    private volatile boolean hasInitClient = false;
    private volatile boolean hasInitProxy = false;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for(Field field:fields){
            if(field.isAnnotationPresent(RpcReference.class)){
                if(!hasInitClient){
                    client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
                }
                hasInitClient = true;
                if(!hasInitProxy){
                    rpcClientProxy = new RpcClientProxy(client);
                }
                hasInitProxy = true;
                field.setAccessible(true);
                Class<?> clazz = field.getType();
                String group = field.getAnnotation(RpcReference.class).group();
                RpcReferenceWrapper rpcReferenceWrapper = new RpcReferenceWrapper();
                rpcReferenceWrapper.setAimClass(clazz);
                rpcReferenceWrapper.setGroup(group);
                Object ref = rpcClientProxy.getProxy(rpcReferenceWrapper);
                try {
                    field.set(bean,ref);
                } catch (IllegalAccessException e) {
                    log.info("赋值出现问题：{}",e);
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
