package eeet2580.kunlun.opwa.backend.trip.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleOverviewRes {
    private String lineId;
    private String lineName;
    private List<TripScheduleRes> firstTwoTrips;
    private List<TripScheduleRes> lastTwoTrips;
    private long totalTripCount;
}