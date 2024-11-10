package com.ata.java_springboot.services;

import com.ata.java_springboot.entities.Salary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@RequiredArgsConstructor
@Service
public class SalaryService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final ObjectMapper objectMapper;

    public List<ObjectNode> getSalaryByCriteria(MultiValueMap<String, String> filters, List<String> selectedFields,
                                                List<String> sortFields, String sortDirection, int page, int size) {
        String sqlQuery = buildQuery(filters, selectedFields, sortFields, sortDirection);
        Query query = createQueryWithParameters(sqlQuery, filters, page, size);
        List<Object[]> resultList = query.getResultList();
        return mapObjectsToJson((selectedFields == null || selectedFields.isEmpty()) ? getAllFieldNames() : selectedFields, resultList);
    }

    private String buildQuery(MultiValueMap<String, String> filters, List<String> selectedFields,
                              List<String> sortFields, String sortDirection) {
        StringJoiner selectFields = new StringJoiner(", ");
        if (selectedFields == null || selectedFields.isEmpty()) {
            selectFields.add("*");
        } else {
            selectedFields.forEach(selectFields::add);
        }

        StringBuilder queryBuilder = new StringBuilder("SELECT ")
                .append(selectFields)
                .append(" FROM SALARY WHERE 1=1");

        addFilterConditions(queryBuilder, filters);
        addSorting(queryBuilder, sortFields, sortDirection);

        queryBuilder.append(" LIMIT :limit OFFSET :offset");
        return queryBuilder.toString();
    }

    private void addFilterConditions(StringBuilder queryBuilder, MultiValueMap<String, String> filters) {
        if (filters.containsKey("jobTitle")) {
            queryBuilder.append(" AND jobTitle = :jobTitle");
        }
        if (filters.containsKey("gender")) {
            queryBuilder.append(" AND gender = :gender");
        }
        if (filters.containsKey("salary[gte]")) {
            queryBuilder.append(" AND salary REGEXP '^[0-9,]+$' AND CAST(REPLACE(salary, ',', '') AS DOUBLE) >= :salaryGte");
        }
        if (filters.containsKey("salary[lte]")) {
            queryBuilder.append(" AND salary REGEXP '^[0-9,]+$' AND CAST(REPLACE(salary, ',', '') AS DOUBLE) <= :salaryLte");
        }
    }

    private void addSorting(StringBuilder queryBuilder, List<String> sortFields, String sortDirection) {
        if (sortFields != null && !sortFields.isEmpty()) {
            queryBuilder.append(" ORDER BY ");
            String sortClause = String.join(", ", sortFields);
            queryBuilder.append(sortClause)
                    .append(" ")
                    .append("DESC".equalsIgnoreCase(sortDirection) ? "DESC" : "ASC");
        }
    }

    private Query createQueryWithParameters(String sqlQuery, MultiValueMap<String, String> filters, int page, int size) {
        Query query = entityManager.createNativeQuery(sqlQuery);
        setQueryParameters(query, filters, page, size);
        return query;
    }

    private void setQueryParameters(Query query, MultiValueMap<String, String> filters, int page, int size) {
        if (filters.containsKey("jobTitle")) {
            query.setParameter("jobTitle", filters.getFirst("jobTitle"));
        }
        if (filters.containsKey("gender")) {
            query.setParameter("gender", filters.getFirst("gender"));
        }
        if (filters.containsKey("salary[gte]")) {
            query.setParameter("salaryGte", parseToDouble(filters.getFirst("salary[gte]")));
        }
        if (filters.containsKey("salary[lte]")) {
            query.setParameter("salaryLte", parseToDouble(filters.getFirst("salary[lte]")));
        }

        query.setParameter("limit", size);
        query.setParameter("offset", page * size);
    }

    private List<ObjectNode> mapObjectsToJson(List<String> fields, List<Object[]> objects) {
        List<ObjectNode> jsonObjects = new ArrayList<>();
        for (Object[] row : objects) {
            ObjectNode jsonNode = objectMapper.createObjectNode();
            for (int i = 0; i < fields.size(); i++) {
                jsonNode.putPOJO(fields.get(i), row[i]);
            }
            jsonObjects.add(jsonNode);
        }
        return jsonObjects;
    }

    private List<String> getAllFieldNames() {
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = Salary.class.getDeclaredFields();

        for (Field field : fields) {
            fieldNames.add(field.getName());
        }

        return fieldNames;
    }

    private Double parseToDouble(String value) {
        try {
            String numericValue = value.replaceAll("[^\\d.]", "");
            return Double.valueOf(numericValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
