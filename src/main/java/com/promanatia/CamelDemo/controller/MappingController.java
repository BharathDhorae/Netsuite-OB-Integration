package com.promanatia.CamelDemo.controller;

import com.promanatia.CamelDemo.DTO.MappingRecordEntity;
import com.promanatia.CamelDemo.service.MappingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MappingController {

    private final MappingService mappingService;

    public MappingController(
            MappingService mappingService
    ) {
        this.mappingService = mappingService;
    }

    @GetMapping("/mappings")
    public String mappings(Model model) {

        model.addAttribute(
                "records",
                mappingService.getAll()
        );

        return "mapping-dashboard";
    }

    @PostMapping("/mappings/create")
    public String create(

            @RequestParam String flowType,

            @RequestParam String externalInstance,

            @RequestParam String openbravoTable,

            @RequestParam String openbravoRecordId,

            @RequestParam String externalEntity,

            @RequestParam String externalRecordId
    ) {

        MappingRecordEntity record = new MappingRecordEntity();

        record.setFlowType(flowType);

        record.setExternalInstance(
                externalInstance
        );

        record.setOpenbravoTable(
                openbravoTable
        );

        record.setOpenbravoRecordId(
                openbravoRecordId
        );

        record.setExternalEntity(
                externalEntity
        );

        record.setExternalRecordId(
                externalRecordId
        );

        mappingService.save(record);

        return "redirect:/mappings";
    }

    @PostMapping("/mappings/update")
    public String update(

            @RequestParam Long id,

            @RequestParam String flowType,

            @RequestParam String externalInstance,

            @RequestParam String openbravoTable,

            @RequestParam String openbravoRecordId,

            @RequestParam String externalEntity,

            @RequestParam String externalRecordId
    ) {

        MappingRecordEntity record =
                mappingService.getById(id);

        record.setFlowType(flowType);

        record.setExternalInstance(
                externalInstance
        );

        record.setOpenbravoTable(
                openbravoTable
        );

        record.setOpenbravoRecordId(
                openbravoRecordId
        );

        record.setExternalEntity(
                externalEntity
        );

        record.setExternalRecordId(
                externalRecordId
        );

        mappingService.save(record);

        return "redirect:/mappings";
    }

    @PostMapping("/mappings/delete")
    public String delete(
            @RequestParam Long id
    ) {

        mappingService.delete(id);

        return "redirect:/mappings";
    }
}