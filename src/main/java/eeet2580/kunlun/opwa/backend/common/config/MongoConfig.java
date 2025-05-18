package eeet2580.kunlun.opwa.backend.common.config;

import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.model.StaffInviteTokenEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@Configuration
@RequiredArgsConstructor
public class MongoConfig {
    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void setupConfig() {
        // sharding
        mongoTemplate.indexOps(StaffEntity.class)
                .ensureIndex(new Index()
                        .on("role", Sort.Direction.ASC)
                        .on("email", Sort.Direction.ASC)
                        .on("username", Sort.Direction.ASC));

        mongoTemplate.indexOps(StaffEntity.class)
                .ensureIndex(new Index()
                        .on("username", Sort.Direction.ASC)
                        .unique());

        // staff invite token TTL
        mongoTemplate.indexOps(StaffInviteTokenEntity.class)
                .ensureIndex(new Index().on("expiresAt", Sort.Direction.ASC).expire(0));
    }
}
