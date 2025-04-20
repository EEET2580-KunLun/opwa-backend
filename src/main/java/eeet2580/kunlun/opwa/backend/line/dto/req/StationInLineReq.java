package eeet2580.kunlun.opwa.backend.line.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Duration;

@Data
public class StationInLineReq {
    @NotBlank(message = "Station ID is required")
    private String stationId;

    @NotBlank(message = "Station name is required")
    private String stationName;

    @NotNull(message = "Sequence is required")
    @Min(value = 0, message = "Sequence must be a positive number")
    private int sequence;

    private Duration timeFromPreviousStation;

    @NotNull(message = "Location coordinates are required")
    private double[] location; // [longitude, latitude]
}