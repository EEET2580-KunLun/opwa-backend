package eeet2580.kunlun.opwa.backend.station.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "metro_station")
public class StationEntity {
    @Id
    private String id;
    private String name;
    private String address;
    private boolean isActive;

    @GeoSpatialIndexed
    private double[] location; // [longitude, latitude]
}