package eeet2580.kunlun.opwa.backend.trip.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripScheduleRes {
    private String id;
    private String lineId;
    private String lineName;
    private String tripCode;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private boolean suspended;
    private List<StationStopRes> stationStops;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StationStopRes {
        private String stationId;
        private String stationName;
        private int sequence;
        private LocalDateTime arrivalTime;
        private LocalDateTime departureTime;
        private double[] location;
    }
}
