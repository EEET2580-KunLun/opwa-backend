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

    @NotNull(message = "Sequence is required")
    @Min(value = 0, message = "Sequence must be a positive number")
    private int sequence;

//    @Min(value = 60, message = "Time must be at least 1 minute (60 seconds)")
    private Duration timeFromPreviousStation;
}