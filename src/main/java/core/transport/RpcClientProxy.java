package core.transport;

import common.entity.RpcRequest;
import common.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.RpcMessageChecker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RpcClientProxy implements InvocationHandler {
    private final RpcClient client;
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    public RpcClientProxy(RpcClient client){
        this.client = client;
    }

    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)  {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .heartBeat(false)
                .build();
        RpcResponse rpcResponse = null;
        CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest);
        try {
            rpcResponse = completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("方法调用请求发送失败", e);
            return null;
        }
        RpcMessageChecker.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }
}
