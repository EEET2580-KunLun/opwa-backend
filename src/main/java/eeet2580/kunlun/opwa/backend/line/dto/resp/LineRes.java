package eeet2580.kunlun.opwa.backend.line.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import eeet2580.kunlun.opwa.backend.line.model.LineEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LineRes {
    private String id;
    private String staffId;
    private String name;
    private OffsetDateTime firstDepartureTime;
    private Integer frequency;
    private LineEntity.Status status;
    private List<StationInLineRes> stations;
}