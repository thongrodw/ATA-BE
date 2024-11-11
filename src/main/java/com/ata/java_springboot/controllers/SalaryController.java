package com.ata.java_springboot.controllers;

import com.ata.java_springboot.services.SalaryService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/job_data")
public class SalaryController {

    private final SalaryService salaryService;

    @GetMapping
    public ResponseEntity<ArrayNode> getSalary(
        @RequestParam(required = false) MultiValueMap<String, String> filterAttributes,
        @RequestParam(required = false) String fields,
        @RequestParam(required = false) String sort,
        @RequestParam(required = false, defaultValue = "ASC") String sort_type,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        try {
            List<String> fieldList = fields != null ? Arrays.asList(fields.split("\\s*,\\s*")) : null;
            List<String> sortList = sort != null ? Arrays.asList(sort.split("\\s*,\\s*")) : null;

            ArrayNode result = salaryService.getSalaryByCriteria(filterAttributes, fieldList, sortList, sort_type, page, size);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error occurred while fetching salary data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
