package eeet2580.kunlun.opwa.backend.common.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseRes<T> {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {
        private Integer status;
        private String message;
    }

    private Meta meta;
    private T data;

    public BaseRes(Integer status, String message, T data) {
        this.meta = new Meta(status, message);
        this.data = data;
    }
}