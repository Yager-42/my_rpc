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
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RpcClientProxy {
    private final RpcClient client;
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    public RpcClientProxy(RpcClient client){
        this.client = client;
    }

    public <T> T getProxy(RpcReferenceWrapper<T> rpcReferenceWrapper){
        T res = (T)Proxy.newProxyInstance(rpcReferenceWrapper.getAimClass().getClassLoader(),new Class[]{rpcReferenceWrapper.getAimClass()},new RpcInvocationHandler(client,rpcReferenceWrapper));
        return res;
    }
}
