package eeet2580.kunlun.opwa.backend.trip.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "metro_trip_schedule")
public class TripScheduleEntity {
    @Id
    private String id;
    private String lineId;
    private String lineName;
    private String tripCode;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private boolean suspended; // Affected by line suspension
    private List<StationStop> stationStops = new ArrayList<>();

    @Data
    public static class StationStop {
        private String stationId;
        private String stationName;
        private int sequence;
        private LocalDateTime arrivalTime;
        private LocalDateTime departureTime;
        private double[] location; // [longitude, latitude]
    }
}
