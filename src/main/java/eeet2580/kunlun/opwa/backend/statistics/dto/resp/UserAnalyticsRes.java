package eeet2580.kunlun.opwa.backend.statistics.dto.resp;

import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnalyticsRes {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserTypeCount {
        private StaffEntity.Role userType;
        private Integer count;
    }

    private List<UserTypeCount> userTypeCounts;
    private Integer totalUsers;
}