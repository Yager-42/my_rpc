package handler;

import annotation.ServiceScan;
import api.HelloService;
import core.netty.server.NettyServer;
import core.serializer.CommonSerializer;
import core.serializer.ProtobufSerializer;
import core.transport.RpcServer;

@ServiceScan
public class TestServer {
    public static void main(String[] args) {
        RpcServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }
}


