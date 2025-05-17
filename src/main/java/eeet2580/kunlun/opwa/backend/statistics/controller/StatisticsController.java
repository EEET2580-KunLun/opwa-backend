package eeet2580.kunlun.opwa.backend.statistics.controller;

import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import eeet2580.kunlun.opwa.backend.statistics.dto.resp.TicketAnalyticsRes;
import eeet2580.kunlun.opwa.backend.statistics.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticService statisticService;

    @GetMapping("/tickets")
    public ResponseEntity<BaseRes<TicketAnalyticsRes>> getTicketAnalytics() {
        TicketAnalyticsRes analytics = statisticService.getTicketAnalytics();

        BaseRes<TicketAnalyticsRes> response = new BaseRes<>(
                HttpStatus.OK.value(), "Successfully get tickets analytics", analytics);
        return ResponseEntity.ok(response);
    }
}