package eeet2580.kunlun.opwa.backend.line.dto.resp;

import lombok.Data;

import java.time.Duration;

@Data
public class StationInLineRes {
    private String stationId;
    private String stationName;
    private int sequence;
    private Duration timeFromPreviousStation;
    private double[] location;
}
