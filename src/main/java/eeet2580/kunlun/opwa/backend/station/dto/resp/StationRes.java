package eeet2580.kunlun.opwa.backend.station.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationRes {
    private String id;
    private String name;
    private String address;
    private boolean isActive;
    private double[] location;
}