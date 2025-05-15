package eeet2580.kunlun.opwa.backend.station.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StationReq {
    @NotBlank(message = "Station name is required")
    private String name;

    private String address;

    @NotNull(message = "Active status is required")
    @JsonProperty("is_active")
    private boolean isActive;

    @NotNull(message = "Location coordinates are required")
    private double[] location; // [longitude, latitude]
}