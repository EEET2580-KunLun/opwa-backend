package eeet2580.kunlun.opwa.backend.line.controller;

import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import eeet2580.kunlun.opwa.backend.line.dto.mapper.LineMapper;
import eeet2580.kunlun.opwa.backend.line.dto.req.LineReq;
import eeet2580.kunlun.opwa.backend.line.dto.resp.LineRes;
import eeet2580.kunlun.opwa.backend.line.model.LineEntity;
import eeet2580.kunlun.opwa.backend.line.service.LineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/line")
@RequiredArgsConstructor
public class LineController {
    private final LineService lineService;
    private final LineMapper lineMapper;

    @GetMapping
    public ResponseEntity<BaseRes<List<LineRes>>> getAllLines() {
        List<LineEntity> lines = lineService.getAllLines();
        List<LineRes> lineDtoList = lineMapper.toDtoList(lines);
        BaseRes<List<LineRes>> response = new BaseRes<>(HttpStatus.OK.value(), "Line list retrieved successfully", lineDtoList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseRes<LineRes>> getLineById(@PathVariable String id) {
        return lineService.getLineById(id)
                .map(line -> {
                    LineRes lineDto = lineMapper.toDto(line);
                    BaseRes<LineRes> response = new BaseRes<>(HttpStatus.OK.value(), "Line retrieved successfully", lineDto);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    BaseRes<LineRes> response = new BaseRes<>(HttpStatus.NOT_FOUND.value(), "Line not found", null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<BaseRes<LineRes>> createLine(@Valid @RequestBody LineReq lineReq) {
        LineEntity line = lineMapper.toEntity(lineReq);
        LineEntity createdLine = lineService.createLine(line);
        LineRes lineDto = lineMapper.toDto(createdLine);
        BaseRes<LineRes> response = new BaseRes<>(HttpStatus.CREATED.value(), "Line created successfully", lineDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseRes<LineRes>> updateLine(@PathVariable String id, @Valid @RequestBody LineReq lineReq) {
        try {
            LineEntity line = lineMapper.toEntity(lineReq);
            LineEntity updatedLine = lineService.updateLine(id, line);
            LineRes lineDto = lineMapper.toDto(updatedLine);
            BaseRes<LineRes> response = new BaseRes<>(HttpStatus.OK.value(), "Line updated successfully", lineDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseRes<LineRes> response = new BaseRes<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseRes<Void>> deleteLine(@PathVariable String id) {
        if (lineService.getLineById(id).isEmpty()) {
            BaseRes<Void> response = new BaseRes<>(HttpStatus.NOT_FOUND.value(), "Line not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        lineService.deleteLine(id);
        BaseRes<Void> response = new BaseRes<>(HttpStatus.OK.value(), "Line deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}