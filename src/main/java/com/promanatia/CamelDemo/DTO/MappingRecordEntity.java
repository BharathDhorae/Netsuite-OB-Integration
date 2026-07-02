package com.promanatia.CamelDemo.DTO;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "integration_mapping")
@Data
public class MappingRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flowType;

    private String externalInstance;

    private String openbravoTable;

    private String openbravoRecordId;

    private String externalEntity;

    private String externalRecordId;

}