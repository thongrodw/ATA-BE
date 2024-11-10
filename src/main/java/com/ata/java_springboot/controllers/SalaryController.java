package com.ata.java_springboot.controllers;

import com.ata.java_springboot.services.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/job_data")
public class SalaryController {

    private final SalaryService salaryService;

    @GetMapping
    public List<Object[]> getSalary(
            @RequestParam(required = false) Map<String, String> filterAttributes,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "ASC") String sort_type) {

        List<String> fieldList = fields != null ? Arrays.asList(fields.split(",")) : null;
        List<String> sortList = sort != null ? Arrays.asList(sort.split(",")) : null;

        return salaryService.getSalaryByCriteria(filterAttributes, fieldList, sortList, sort_type);
    }
}
