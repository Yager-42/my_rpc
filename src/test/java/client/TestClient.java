package client;

import api.ByeService;
import api.HelloObject;
import api.HelloService;
import core.serializer.CommonSerializer;
import core.serializer.ProtobufSerializer;
import core.transport.RpcClient;
import core.netty.client.NettyClient;
import core.transport.RpcClientProxy;

public class TestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.Hello(object);
        System.out.println(res);
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Netty"));

    }
}

