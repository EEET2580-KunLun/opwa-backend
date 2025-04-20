package eeet2580.kunlun.opwa.backend.line.service;

import eeet2580.kunlun.opwa.backend.line.dto.req.LineReq;
import eeet2580.kunlun.opwa.backend.line.dto.resp.LineRes;
import eeet2580.kunlun.opwa.backend.line.model.LineEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface LineService {
    List<LineRes> getAllLines();

    Optional<LineRes> getLineById(String id);

    LineRes createLine(LineEntity line);

    LineRes createLine(LineReq lineReq, Authentication authentication);

    LineRes updateLine(String id, LineEntity updatedLine);

    LineRes updateLine(String id, LineReq lineReq, Authentication authentication);

    void deleteLine(String id);
}