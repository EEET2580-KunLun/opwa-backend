package eeet2580.kunlun.opwa.backend.line.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Data
@Document(collection = "metro_line")
public class LineEntity {
    public enum Status {
        ACTIVE,
        EMERGENCY,
        MAINTENANCE,
    }

    @Id
    private String id;
    private String staffId;
    private String name;
    private Long firstDepartureTime;
    private Integer frequency;
    private Status status;

    private List<StationInLine> stations;

    @Data
    public static class StationInLine {
        private String stationId;
        private String stationName;
        private int sequence;
        private Duration timeFromPreviousStation;
        @GeoSpatialIndexed
        private double[] location; // [longitude, latitude]
    }
}