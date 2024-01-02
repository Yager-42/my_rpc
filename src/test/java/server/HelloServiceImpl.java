package server;

import api.HelloObject;
import api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceImpl implements HelloService {

    private  static  final Logger log = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String Hello(HelloObject helloObject) {
        log.info("接收到：{}",helloObject.getMessage());
        return "这是掉用的返回值，id=" + helloObject.getId();
    }
}
