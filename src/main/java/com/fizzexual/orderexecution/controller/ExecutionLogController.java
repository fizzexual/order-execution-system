package com.fizzexual.orderexecution.controller;

import com.fizzexual.orderexecution.dto.ExecutionLogResponse;
import com.fizzexual.orderexecution.mapper.ExecutionLogMapper;
import com.fizzexual.orderexecution.repository.ExecutionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/execution-logs")
@RequiredArgsConstructor
public class ExecutionLogController {

    private final ExecutionLogRepository executionLogRepository;
    private final ExecutionLogMapper executionLogMapper;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ExecutionLogResponse>> getExecutionLogsByOrderId(@PathVariable Long orderId) {
        List<ExecutionLogResponse> logs = executionLogRepository.findByOrderIdOrderByExecutedAtDesc(orderId)
                .stream()
                .map(executionLogMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }

    @GetMapping
    public ResponseEntity<List<ExecutionLogResponse>> getAllExecutionLogs() {
        List<ExecutionLogResponse> logs = executionLogRepository.findAll()
                .stream()
                .map(executionLogMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }
}
