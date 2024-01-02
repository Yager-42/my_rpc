package core.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import common.entity.RpcRequest;
import common.entity.RpcResponse;
import common.enumerate.SerializerCode;
import exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.esotericsoftware.kryo.util.Pool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements CommonSerializer{
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    private static Pool<Kryo> kryoPool;

    static {
        kryoPool = new Pool<Kryo>(true, false, 8) {
            @Override
            protected Kryo create() {
                Kryo kryo = new Kryo();
                kryo.register(RpcResponse.class);
                kryo.register(RpcRequest.class);
                kryo.setReferences(true);
                kryo.setRegistrationRequired(false);
                return kryo;
            }
        };
    }

    @Override
    public byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream);
            Kryo kryo = kryoPool.obtain();
            kryo.writeObject(output, obj);
            kryoPool.free(kryo);
            return output.toBytes();
        } catch (Exception e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try{
            ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayOutputStream);
            Kryo kryo = kryoPool.obtain();
            Object o = kryo.readObject(input,clazz);
            input.close();
            kryoPool.free(kryo);
            return o;
        }catch (Exception e){
            logger.error("反序列化时有错误发生:", e);
            throw new SerializeException("反序列化时有错误发生");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
