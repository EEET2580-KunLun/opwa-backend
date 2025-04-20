package eeet2580.kunlun.opwa.backend.line.service.impl;

import eeet2580.kunlun.opwa.backend.line.dto.mapper.LineMapper;
import eeet2580.kunlun.opwa.backend.line.dto.req.LineReq;
import eeet2580.kunlun.opwa.backend.line.dto.resp.LineRes;
import eeet2580.kunlun.opwa.backend.line.model.LineEntity;
import eeet2580.kunlun.opwa.backend.line.repository.LineRepository;
import eeet2580.kunlun.opwa.backend.line.service.LineService;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LineServiceImpl implements LineService {
    private final LineRepository lineRepository;
    private final LineMapper lineMapper;
    private final StaffRepository staffRepository;

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
}