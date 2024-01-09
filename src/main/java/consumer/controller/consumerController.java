package consumer.controller;


import annotation.RpcReference;
import api.ByeService;
import api.HelloObject;
import api.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class consumerController {
    @RpcReference
    private HelloService helloService;
    @RpcReference
    private ByeService byeService;

    @GetMapping(value = "/test")
    public void test(){
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.Hello(object);
        System.out.println(res);
        res = byeService.bye("Netty");
        System.out.println(res);
    }
}
