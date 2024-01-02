package handler;

import api.HelloService;
import core.netty.server.NettyServer;
import core.serializer.CommonSerializer;
import core.serializer.ProtobufSerializer;
import core.transport.RpcServer;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        NettyServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.PROTOBUF_SERIALIZER);
        server.publishService(helloService, HelloService.class);
        server.start();
    }
}


