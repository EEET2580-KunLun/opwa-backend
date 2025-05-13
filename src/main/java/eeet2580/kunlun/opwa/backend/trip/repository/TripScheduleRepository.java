package eeet2580.kunlun.opwa.backend.trip.repository;

import eeet2580.kunlun.opwa.backend.trip.model.TripScheduleEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripScheduleRepository extends MongoRepository<TripScheduleEntity, String> {

    List<TripScheduleEntity> findByLineId(String lineId);

    List<TripScheduleEntity> findByLineId(String lineId, Sort sort);

    @Query(value = "{'lineId': ?0}", sort = "{'departureTime': 1}")
    List<TripScheduleEntity> findFirstTwoByLineId(String lineId, Pageable pageable);

    @Query(value = "{'lineId': ?0}", sort = "{'departureTime': -1}")
    List<TripScheduleEntity> findLastTwoByLineId(String lineId, Pageable pageable);

    @Query(value = "{'departureTime': {$gte: ?0}, 'lineId': ?1}", sort = "{'departureTime': 1}")
    List<TripScheduleEntity> findNextThreeUpcomingTrips(LocalDateTime fromTime, String lineId, Pageable pageable);

    @Query(value = "{'stationStops.stationId': ?0, 'stationStops.stationId': ?1}")
    List<TripScheduleEntity> findTripsBetweenStations(String departureStationId, String arrivalStationId);

    @Query(value = "{'stationStops.stationId': ?0}", sort = "{'departureTime': 1}")
    List<TripScheduleEntity> findTripsByStationId(String stationId, Pageable pageable);
}
