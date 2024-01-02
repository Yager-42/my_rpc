package client;

import api.HelloObject;
import api.HelloService;
import core.serializer.ProtobufSerializer;
import core.transport.RpcClient;
import core.netty.client.NettyClient;
import core.transport.RpcClientProxy;

public class TestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        client.setSerializer(new ProtobufSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.Hello(object);
        System.out.println(res);
    }
}

