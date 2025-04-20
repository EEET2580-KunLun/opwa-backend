package eeet2580.kunlun.opwa.backend.line.service.impl;

import eeet2580.kunlun.opwa.backend.line.model.LineEntity;
import eeet2580.kunlun.opwa.backend.line.repository.LineRepository;
import eeet2580.kunlun.opwa.backend.line.service.LineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LineServiceImpl implements LineService {

    private final LineRepository lineRepository;

    @Override
    public List<LineEntity> getAllLines() {
        return lineRepository.findAll();
    }

    @Override
    public Optional<LineEntity> getLineById(String id) {
        return lineRepository.findById(id);
    }

    @Override
    public LineEntity createLine(LineEntity line) {
        return lineRepository.save(line);
    }

    @Override
    public LineEntity updateLine(String id, LineEntity updatedLine) {
        if (lineRepository.existsById(id)) {
            updatedLine.setId(id);
            return lineRepository.save(updatedLine);
        }
        throw new RuntimeException("Line not found with id: " + id);
    }

    @Override
    public void deleteLine(String id) {
        lineRepository.deleteById(id);
    }
}