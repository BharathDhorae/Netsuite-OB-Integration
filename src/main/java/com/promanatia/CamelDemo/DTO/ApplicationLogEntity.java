package com.promanatia.CamelDemo.DTO;

import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "application_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Timestamp logTime;
    private String product;
    private String flowType;
    private String documentId;
    private String logLevel;
    private String message;
    private String errorMessage;

}