package core.codec;

import common.entity.RpcRequest;
import common.entity.RpcResponse;
import common.enumerate.PackageType;
import core.serializer.CommonSerializer;
import core.serializer.JsonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CommonEncoder extends MessageToByteEncoder {
    //魔数的作用在于，可以轻松的分辨出java class文件和非java class 文件。
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    private final CommonSerializer serializer;


    public CommonEncoder(CommonSerializer commonSerializer) {
        serializer = commonSerializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(MAGIC_NUMBER);
        if(o instanceof RpcRequest){
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
        }else{
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        byteBuf.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(o);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

    }
}
