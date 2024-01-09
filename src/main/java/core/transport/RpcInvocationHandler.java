package core.transport;

import annotation.RpcReference;
import common.entity.RpcReferenceWrapper;
import common.entity.RpcRequest;
import common.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetClassAware;
import util.RpcMessageChecker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RpcInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcInvocationHandler.class);
    private final RpcClient client;
    private final RpcReferenceWrapper<?> rpcReferenceWrapper;
    public RpcInvocationHandler(RpcClient client,RpcReferenceWrapper rpcReferenceWrapper){
        this.client = client;
        this.rpcReferenceWrapper = rpcReferenceWrapper;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)  {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        String group = rpcReferenceWrapper.getGroup();
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .heartBeat(false)
                .group(group)
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
