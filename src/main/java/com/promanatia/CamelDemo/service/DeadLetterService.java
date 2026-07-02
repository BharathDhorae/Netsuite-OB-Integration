package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.LogRecordEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeadLetterService {

    private final List<LogRecordEntity> logs = new ArrayList<>();

    public void save(LogRecordEntity record) {
        logs.add(record);
    }

    public List<LogRecordEntity> getFailedRecords() {
        return logs;
    }
}