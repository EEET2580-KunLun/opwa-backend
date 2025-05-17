package eeet2580.kunlun.opwa.backend.line.service.impl;

import eeet2580.kunlun.opwa.backend.line.dto.mapper.LineMapper;
import eeet2580.kunlun.opwa.backend.line.dto.req.LineReq;
import eeet2580.kunlun.opwa.backend.line.dto.req.LineSuspensionReq;
import eeet2580.kunlun.opwa.backend.line.dto.resp.LineRes;
import eeet2580.kunlun.opwa.backend.line.model.LineEntity;
import eeet2580.kunlun.opwa.backend.line.repository.LineRepository;
import eeet2580.kunlun.opwa.backend.line.service.LineService;
import eeet2580.kunlun.opwa.backend.notification.service.SuspensionNotificationService;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.repository.StaffRepository;
import eeet2580.kunlun.opwa.backend.trip.dto.mapper.TripScheduleMapper;
import eeet2580.kunlun.opwa.backend.trip.dto.resp.ScheduleOverviewRes;
import eeet2580.kunlun.opwa.backend.trip.dto.resp.TripScheduleRes;
import eeet2580.kunlun.opwa.backend.trip.model.TripScheduleEntity;
import eeet2580.kunlun.opwa.backend.trip.repository.TripScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LineServiceImpl implements LineService {
    private final LineRepository lineRepository;
    private final LineMapper lineMapper;
    private final StaffRepository staffRepository;
    private final TripScheduleRepository tripScheduleRepository;

    //TODO: Create the classes: TripScheduleMapper, SuspensionNotificationService later
    private final TripScheduleMapper tripScheduleMapper;
    private final SuspensionNotificationService suspensionNotificationService;

    @Override
    public List<LineRes> getAllLines() {
        List<LineEntity> entities = lineRepository.findAll();
        return lineMapper.toDtoList(entities);
    }

    @Override
    public Optional<LineRes> getLineById(String id) {
        return lineRepository.findById(id).map(lineMapper::toDto);
    }

    @Override
    public LineRes createLine(LineEntity line) {
        if (lineRepository.existsByName(line.getName())) {
            throw new IllegalArgumentException("Line name '" + line.getName() + "' already exists");
        }
        LineEntity savedEntity = lineRepository.save(line);
        return lineMapper.toDto(savedEntity);
    }

    @Override
    public LineRes createLine(LineReq lineReq, Authentication authentication) {
        LineEntity line = lineMapper.toEntity(lineReq);

        String email = authentication.getName();
        StaffEntity staff = staffRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        line.setStaffId(staff.getId());

        return createLine(line);
    }

    @Override
    public LineRes updateLine(String id, LineEntity updatedLine) {
        if (lineRepository.existsById(id)) {
            Optional<LineEntity> existingWithSameName = lineRepository.findByName(updatedLine.getName());
            if (existingWithSameName.isPresent() && !existingWithSameName.get().getId().equals(id)) {
                throw new IllegalArgumentException("Line name '" + updatedLine.getName() + "' already exists");
            }

            updatedLine.setId(id);
            LineEntity savedEntity = lineRepository.save(updatedLine);
            return lineMapper.toDto(savedEntity);
        }
        throw new RuntimeException("Line not found with id: " + id);
    }

    @Override
    public LineRes updateLine(String id, LineReq lineReq, Authentication authentication) {
        LineEntity line = lineMapper.toEntity(lineReq);

        String email = authentication.getName();
        StaffEntity staff = staffRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        line.setStaffId(staff.getId());

        return updateLine(id, line);
    }

    @Override
    public void deleteLine(String id) {
        lineRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ScheduleOverviewRes generateLineSchedule(String lineId) {
        LineEntity line = lineRepository.findById(lineId)
                .orElseThrow(() -> new RuntimeException("Line not found with id: " + lineId));

        // Delete existing schedules for this line
        List<TripScheduleEntity> existingSchedules = tripScheduleRepository.findByLineId(lineId);
        tripScheduleRepository.deleteAll(existingSchedules);

        // Sort stations by sequence
        List<LineEntity.StationInLine> sortedStations = line.getStations().stream()
                .sorted(Comparator.comparingInt(LineEntity.StationInLine::getSequence))
                .collect(Collectors.toList());

        if (sortedStations.size() < 2) {
            throw new IllegalArgumentException("A line must have at least two stations");
        }

        // Generate schedules from first departure until 10:00 PM (22:00)
        List<TripScheduleEntity> generatedSchedules = new ArrayList<>();

        // Get first departure time in milliseconds since midnight
        long firstDeparture = line.getFirstDepartureTime();

        // Convert milliseconds since midnight to hours and minutes
        int hours = (int) (firstDeparture / (60 * 60 * 1000));
        int minutes = (int) ((firstDeparture % (60 * 60 * 1000)) / (60 * 1000));
        int seconds = (int) ((firstDeparture % (60 * 1000)) / 1000);

        // Create today's date with the specified time
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime firstDepartureTime = LocalDateTime.of(
                today.getYear(), today.getMonth(), today.getDayOfMonth(),
                hours, minutes, seconds);

        // First trip is at first departure time
        LocalDateTime departureTime = firstDepartureTime;

        // End time is 10:00 PM on the same day
        LocalDateTime endTime = firstDepartureTime.toLocalDate().atTime(22, 0);

        // Generate trips until end time
        int tripCounter = 0;
        while (departureTime.isBefore(endTime)) {
            String tripCode = line.getName().replaceAll("\\s+", "") + "-" +
                    String.format("%04d", tripCounter);

            TripScheduleEntity tripSchedule = createTripSchedule(line, sortedStations, departureTime, tripCode);
            generatedSchedules.add(tripSchedule);

            // Next departure is after the frequency interval
            departureTime = departureTime.plusMinutes(line.getFrequency());
            tripCounter++;
        }

        // Save all generated schedules
        tripScheduleRepository.saveAll(generatedSchedules);

        // Return the schedule overview
        return getScheduleOverview(lineId);
    }

    private TripScheduleEntity createTripSchedule(LineEntity line, List<LineEntity.StationInLine> sortedStations,
                                                  LocalDateTime departureTime, String tripCode) {
        TripScheduleEntity tripSchedule = new TripScheduleEntity();
        tripSchedule.setLineId(line.getId());
        tripSchedule.setLineName(line.getName());
        tripSchedule.setTripCode(tripCode);
        tripSchedule.setDepartureTime(departureTime);

        // Calculate cumulative time from start and create station stops
        List<TripScheduleEntity.StationStop> stationStops = new ArrayList<>();

        LocalDateTime currentTime = departureTime;
        for (int i = 0; i < sortedStations.size(); i++) {
            LineEntity.StationInLine station = sortedStations.get(i);

            TripScheduleEntity.StationStop stationStop = new TripScheduleEntity.StationStop();
            stationStop.setStationId(station.getStationId());
            stationStop.setStationName(station.getStationName());
            stationStop.setSequence(station.getSequence());
            stationStop.setLocation(station.getLocation());

            if (i == 0) {
                // First station - departure only
                stationStop.setArrivalTime(currentTime);
                stationStop.setDepartureTime(currentTime);
            } else {
                // Add time from previous station
                currentTime = currentTime.plus(sortedStations.get(i - 1).getTimeFromPreviousStation());
                stationStop.setArrivalTime(currentTime);

                // Add a 30-second stop at each intermediate station
                if (i < sortedStations.size() - 1) {
                    currentTime = currentTime.plusSeconds(30);
                }
                stationStop.setDepartureTime(currentTime);
            }

            stationStops.add(stationStop);
        }

        tripSchedule.setStationStops(stationStops);

        // Set arrival time as the arrival time at the last station
        tripSchedule.setArrivalTime(stationStops.get(stationStops.size() - 1).getArrivalTime());

        return tripSchedule;
    }

    //TODO: Not yet understand this method
    @Override
    public ScheduleOverviewRes getScheduleOverview(String lineId) {
        LineEntity line = lineRepository.findById(lineId)
                .orElseThrow(() -> new RuntimeException("Line not found with id: " + lineId));

        // Get first two trips
        List<TripScheduleEntity> firstTwoTrips = tripScheduleRepository.findFirstTwoByLineId(
                lineId, PageRequest.of(0, 2));

        // Get last two trips
        List<TripScheduleEntity> lastTwoTrips = tripScheduleRepository.findLastTwoByLineId(
                lineId, PageRequest.of(0, 2));

        // Get total trip count
        long totalTripCount = tripScheduleRepository.count();

        ScheduleOverviewRes overview = new ScheduleOverviewRes();
        overview.setLineId(lineId);
        overview.setLineName(line.getName());
        overview.setFirstTwoTrips(tripScheduleMapper.toDtoList(firstTwoTrips));
        overview.setLastTwoTrips(tripScheduleMapper.toDtoList(lastTwoTrips));
        overview.setTotalTripCount(totalTripCount);

        return overview;
    }

    @Override
    public List<TripScheduleRes> getLineTrips(String lineId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<TripScheduleEntity> trips = tripScheduleRepository.findByLineId(lineId, pageRequest);
        return tripScheduleMapper.toDtoList(trips);
    }

    @Override
    @Transactional
    public LineRes suspendLine(String lineId, LineSuspensionReq suspensionReq, Authentication authentication) {
        LineEntity line = lineRepository.findById(lineId)
                .orElseThrow(() -> new RuntimeException("Line not found with id: " + lineId));

        // Update line status
        line.setStatus(suspensionReq.getSuspensionType());

        // Save the updated line
        LineEntity savedLine = lineRepository.save(line);

        // Mark affected trips as suspended
        List<TripScheduleEntity> affectedTrips;
        if (suspensionReq.getAffectedStationIds() == null || suspensionReq.getAffectedStationIds().isEmpty()) {
            // Entire line is suspended
            affectedTrips = tripScheduleRepository.findByLineId(lineId);

            // Mark all trips as suspended
            for (TripScheduleEntity trip : affectedTrips) {
                trip.setSuspended(true);
            }
        } else {
            // Only specific stations are affected
            affectedTrips = new ArrayList<>();

            // Find trips passing through the affected stations
            for (String stationId : suspensionReq.getAffectedStationIds()) {
                List<TripScheduleEntity> tripsWithStation = tripScheduleRepository.findTripsByStationId(stationId, PageRequest.of(0, Integer.MAX_VALUE));

                // Filter trips for the current line only
                tripsWithStation = tripsWithStation.stream()
                        .filter(trip -> trip.getLineId().equals(lineId))
                        .toList();

                affectedTrips.addAll(tripsWithStation);
            }

            // Mark affected trips as suspended
            for (TripScheduleEntity trip : affectedTrips) {
                trip.setSuspended(true);
            }
        }

        // Save the updated trips
        tripScheduleRepository.saveAll(affectedTrips);

        // Send suspension notification to PAWA
        suspensionNotificationService.notifySuspension(
                lineId,
                line.getName(),
                suspensionReq.getAffectedStationIds(),
                suspensionReq.getReason(),
                suspensionReq.getExpectedRestorationTime());

        return lineMapper.toDto(savedLine);
    }

    @Override
    @Transactional
    public LineRes resumeLine(String lineId, Authentication authentication) {
        LineEntity line = lineRepository.findById(lineId)
                .orElseThrow(() -> new RuntimeException("Line not found with id: " + lineId));

        // Update line status to ACTIVE
        line.setStatus(LineEntity.Status.ACTIVE);

        // Save the updated line
        LineEntity savedLine = lineRepository.save(line);

        // Mark all trips as not suspended
        List<TripScheduleEntity> trips = tripScheduleRepository.findByLineId(lineId);
        for (TripScheduleEntity trip : trips) {
            trip.setSuspended(false);
        }

        // Save the updated trips
        tripScheduleRepository.saveAll(trips);

        // Send resume notification to PAWA
        suspensionNotificationService.notifyResumption(lineId, line.getName());

        return lineMapper.toDto(savedLine);
    }

    @Override
    public List<TripScheduleRes> findTripsBetweenStations(String departureStationId, String arrivalStationId, LocalDateTime departureTime) {
        // If departureTime is null, use current time
        final LocalDateTime finalDepartureTime = (departureTime == null) ? LocalDateTime.now() : departureTime;

        // Find trips that pass through both stations
        List<TripScheduleEntity> trips = tripScheduleRepository.findTripsBetweenStations(
                departureStationId, arrivalStationId);

        // Filter trips:
        // 1. Trip must not be suspended
        // 2. Trip must have departure station before arrival station
        // 3. Trip must depart after requested time
        List<TripScheduleEntity> validTrips = trips.stream()
                .filter(trip -> !trip.isSuspended())
                .filter(trip -> {
                    // Get station stops for departure and arrival
                    Optional<TripScheduleEntity.StationStop> departureStop = trip.getStationStops().stream()
                            .filter(stop -> stop.getStationId().equals(departureStationId))
                            .findFirst();

                    Optional<TripScheduleEntity.StationStop> arrivalStop = trip.getStationStops().stream()
                            .filter(stop -> stop.getStationId().equals(arrivalStationId))
                            .findFirst();

                    // Both stops must exist
                    if (departureStop.isEmpty() || arrivalStop.isEmpty()) {
                        return false;
                    }

                    // Departure must be before arrival
                    return departureStop.get().getSequence() < arrivalStop.get().getSequence();
                })
                .filter(trip -> {
                    // Get departure time for the departure station
                    LocalDateTime tripDepartureTime = trip.getStationStops().stream()
                            .filter(stop -> stop.getStationId().equals(departureStationId))
                            .findFirst()
                            .get() // We've already filtered out nulls
                            .getDepartureTime();

                    // Trip must depart after requested time
                    return tripDepartureTime.isAfter(finalDepartureTime) || tripDepartureTime.equals(finalDepartureTime);
                }).sorted(Comparator.comparing(trip ->
                        trip.getStationStops().stream()
                                .filter(stop -> stop.getStationId().equals(departureStationId))
                                .findFirst()
                                .get()
                                .getDepartureTime())).toList();

        // Sort by departure time

        // Take the next three trips
        List<TripScheduleEntity> nextThreeTrips = validTrips.stream()
                .limit(3)
                .collect(Collectors.toList());

        return tripScheduleMapper.toDtoList(nextThreeTrips);
    }

    @Override
    public List<TripScheduleRes> findNextThreeUpcomingTrips(String stationId, LocalDateTime fromTime) {
        // If fromTime is null, use current time
        if (fromTime == null) {
            fromTime = LocalDateTime.now();
        }

        // Find the next three upcoming trips for this station
        List<TripScheduleEntity> trips = tripScheduleRepository.findNextThreeUpcomingTrips(
                fromTime, stationId, PageRequest.of(0, 3));

        return tripScheduleMapper.toDtoList(trips);
    }
}