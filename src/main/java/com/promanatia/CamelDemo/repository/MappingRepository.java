package com.promanatia.CamelDemo.repository;

import com.promanatia.CamelDemo.DTO.MappingRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MappingRepository
        extends JpaRepository<MappingRecordEntity, Long> {

}
