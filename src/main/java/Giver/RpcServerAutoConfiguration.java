package Giver;

import annotation.RpcService;
import core.netty.server.NettyServer;
import core.serializer.CommonSerializer;
import core.transport.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RpcServerAutoConfiguration implements InitializingBean, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerAutoConfiguration.class);

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        RpcServer server = null;
        Map<String,Object> beanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if(beanMap.size()==0){
            return;
        }
        server = new NettyServer("127.0.0.1", 9999, CommonSerializer.PROTOBUF_SERIALIZER);
        for(String beanName: beanMap.keySet()){
            Object bean = beanMap.get(beanName);
            String group = bean.getClass().getAnnotation(RpcService.class).group();
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            for(Class<?> a_interface:interfaces){
                server.publishService(bean,a_interface.getCanonicalName(),group);
            }
        }
        server.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
