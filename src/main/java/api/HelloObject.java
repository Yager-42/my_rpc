package api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class HelloObject implements Serializable {
    private Integer id;
    private String message;
    public HelloObject(){}
}
