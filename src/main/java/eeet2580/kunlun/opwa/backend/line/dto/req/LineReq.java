package eeet2580.kunlun.opwa.backend.line.dto.req;

import eeet2580.kunlun.opwa.backend.line.model.LineEntity.Status;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class LineReq {
    @NotBlank(message = "Line name is required")
    private String name;

    @NotNull(message = "First departure time is required")
    private long firstDepartureTime;

    @NotNull(message = "Frequency is required")
    @Min(value = 1, message = "Frequency must be at least 1 minute")
    private Integer frequency;

    @NotNull(message = "Status is required")
    private Status status;

    @Valid
    private List<StationInLineReq> stations;
}