package eeet2580.kunlun.opwa.backend.statistics.service;

import eeet2580.kunlun.opwa.backend.statistics.dto.resp.TicketAnalyticsRes;
import eeet2580.kunlun.opwa.backend.statistics.dto.resp.UserAnalyticsRes;

public interface StatisticService {
    TicketAnalyticsRes getTicketAnalytics();

    UserAnalyticsRes getUserAnalytics();
}
