package core.netty.client;

import common.entity.RpcRequest;
import common.entity.RpcResponse;
import common.enumerate.RpcError;
import core.loadbalancer.LoadBalancer;
import core.loadbalancer.RandomLoadBalancer;
import core.serializer.CommonSerializer;
import core.transport.RpcClient;
import core.codec.CommonDecoder;
import core.codec.CommonEncoder;
import exception.RpcException;
import factory.SingletonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.registry.NacosServiceDiscovery;
import core.registry.ServiceDiscovery;
import util.RpcMessageChecker;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);


    private final ServiceDiscovery serviceDiscovery;

    private final CommonSerializer serializer;

    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        this.serviceDiscovery = new NacosServiceDiscovery(new RandomLoadBalancer());
        this.serializer = CommonSerializer.getByCode(DEFAULT_SERIALIZER);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    public NettyClient(Integer serializer){
        this.serviceDiscovery = new NacosServiceDiscovery(new RandomLoadBalancer());
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }



    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if(channel.isActive()) {
                unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
                channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener)future1 -> {
                    if (future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        future1.channel().close();
                        resultFuture.completeExceptionally(future1.cause());
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
            } else {
                return null;
            }
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
