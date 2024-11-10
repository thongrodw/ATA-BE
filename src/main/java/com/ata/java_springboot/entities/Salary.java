package com.ata.java_springboot.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "SALARY")
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("Additional Comments")
    @Column(name = "ADDITIONAL_COMMENTS", columnDefinition = "TEXT", length = 1000)
    private String additionalComments;

    @JsonProperty("Annual Bonus")
    @Column(name = "ANNUAL_BONUS")
    private String annualBonus;

    @JsonProperty("Annual Stock Value/Bonus")
    @Column(name = "ANNUAL_STOCK_VALUE_BONUS")
    private String annualStockValueBonus;

    @JsonProperty("Employer")
    @Column(name = "EMPLOYER")
    private String employer;

    @JsonProperty("Gender")
    @Column(name = "GENDER")
    private String gender;

    @JsonProperty("Job Title")
    @Column(name = "JOB_TITLE")
    private String jobTitle;

    @JsonProperty("Location")
    @Column(name = "LOCATION")
    private String location;

    @JsonProperty("Salary")
    @Column(name = "SALARY")
    private String salary;

    @JsonProperty("Signing Bonus")
    @Column(name = "SIGNING_BONUS")
    private String signingBonus;

    @JsonProperty("Timestamp")
    @Column(name = "TIMESTAMP")
    private String timestamp;

    @JsonProperty("Years at Employer")
    @Column(name = "YEARS_AT_EMPLOYER")
    private String yearsAtEmployer;

    @JsonProperty("Years of Experience")
    @Column(name = "YEARS_OF_EXPERIENCE")
    private String yearsOfExperience;
}
