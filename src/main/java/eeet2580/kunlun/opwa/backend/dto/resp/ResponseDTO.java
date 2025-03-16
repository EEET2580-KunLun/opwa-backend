package eeet2580.kunlun.opwa.backend.dtos.resp;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
    private String status;
    private String message;
    private T data;
}
