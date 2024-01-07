package core.transport;

import core.serializer.CommonSerializer;

public interface RpcServer {
    void start();

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    <T> void publishService(T service, String serviceName);

}
