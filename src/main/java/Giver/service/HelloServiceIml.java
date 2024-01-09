package Giver.service;

import annotation.RpcService;
import api.HelloObject;
import api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RpcService
public class HelloServiceIml implements HelloService {
    private  static  final Logger log = LoggerFactory.getLogger(HelloServiceIml.class);
    @Override
    public String Hello(HelloObject helloObject) {
        log.info("接收到：{}",helloObject.getMessage());
        return "这是掉用的返回值，id=" + helloObject.getId();
    }
}
