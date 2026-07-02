package com.promanatia.CamelDemo.DTO;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;


@Entity
@Table(name = "integration_mapping")
@Data
@Builder
public class MappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flowType;

    private String externalInstance;

    private String openbravoTable;

    private String openbravoRecordId;

    private String externalEntity;

    private String externalRecordId;

    private String status;

}