package eeet2580.kunlun.opwa.backend.line.dto.req;

import eeet2580.kunlun.opwa.backend.line.model.LineEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LineSuspensionReq {
    // If empty, the entire line is considered suspended
    private List<String> affectedStationIds;

    @NotBlank(message = "Suspension reason is required")
    private String reason;

    private LineEntity.Status suspensionType = LineEntity.Status.EMERGENCY;

    // If not provided, the suspension is considered indefinite
    private LocalDateTime expectedRestorationTime;
}
