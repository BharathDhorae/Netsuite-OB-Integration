package com.promanatia.CamelDemo.repository;

import com.promanatia.CamelDemo.DTO.MappingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MappingRepository
        extends JpaRepository<MappingRecord, Long> {
}