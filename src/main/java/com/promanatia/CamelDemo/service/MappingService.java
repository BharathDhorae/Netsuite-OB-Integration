package com.promanatia.CamelDemo.service;

import com.promanatia.CamelDemo.DTO.MappingRecord;
import com.promanatia.CamelDemo.repository.MappingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MappingService {

    private final MappingRepository repository;

    public MappingService(
            MappingRepository repository
    ) {
        this.repository = repository;
    }

    public List<MappingRecord> getAll() {

        return repository.findAll();
    }

    public void save(MappingRecord record) {

        repository.save(record);
    }

    public MappingRecord getById(Long id) {

        return repository.findById(id)
                .orElseThrow();
    }

    public void delete(Long id) {

        repository.deleteById(id);
    }
}