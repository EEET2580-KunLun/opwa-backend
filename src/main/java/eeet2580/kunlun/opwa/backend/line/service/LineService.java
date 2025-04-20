package eeet2580.kunlun.opwa.backend.line.service;

import eeet2580.kunlun.opwa.backend.line.model.LineEntity;

import java.util.List;
import java.util.Optional;

public interface LineService {
    List<LineEntity> getAllLines();

    Optional<LineEntity> getLineById(String id);

    LineEntity createLine(LineEntity line);

    LineEntity updateLine(String id, LineEntity updatedLine);

    void deleteLine(String id);
}