package com.ata.java_springboot;

import com.ata.java_springboot.entities.Salary;
import com.ata.java_springboot.repositories.SalaryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.InputStream;
import java.util.List;

@SpringBootApplication
public class JavaSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaSpringbootApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(SalaryRepository repository){
		return args -> {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<Salary>> typeReference = new TypeReference<>() {};
			try (InputStream inputStream = new ClassPathResource("salary_survey-3.json").getInputStream()) {
				List<Salary> salaryDataList = mapper.readValue(inputStream, typeReference);
				repository.saveAll(salaryDataList);
				System.out.println("Job data loaded successfully.");
			} catch (Exception e) {
				System.err.println("Failed to load job data: " + e.getMessage());
			}
		};
	}
}
