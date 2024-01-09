package common.entity;

import lombok.Data;

@Data
public class RpcReferenceWrapper<T> {
    private Class<T> aimClass;
    private String group;
}
