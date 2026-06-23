package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.LogRecord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeadLetterService {

    private final List<LogRecord> logs = new ArrayList<>();

    public void save(LogRecord record) {
        logs.add(record);
    }

    public List<LogRecord> getFailedRecords() {
        return logs;
    }
}