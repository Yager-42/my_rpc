package core.transport;

import common.entity.RpcRequest;
import core.serializer.CommonSerializer;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;
}
