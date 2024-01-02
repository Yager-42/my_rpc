package handler;

import api.HelloService;
import core.netty.server.NettyServer;
import core.serializer.ProtobufSerializer;
import core.transport.RpcServer;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer server = new NettyServer("127.0.0.1", 9999);
        server.setSerializer(new ProtobufSerializer());
        server.publishService(helloService, HelloService.class);
        server.start();
    }
}


