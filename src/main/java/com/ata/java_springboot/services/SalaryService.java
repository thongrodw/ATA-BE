package com.ata.java_springboot.services;

import com.ata.java_springboot.entities.Salary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class SalaryService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final ObjectMapper objectMapper;

    public List<ObjectNode> getSalaryByCriteria(MultiValueMap<String, String> filters, List<String> selectedFields,
                                                List<String> sortFields, String sortDirection, int page, int size) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Salary> root = query.from(Salary.class);

        // Select Fields
        List<String> fieldsToSelect = (selectedFields == null || selectedFields.isEmpty())
                ? getAllFieldNames(root)
                : selectedFields;

        query.multiselect(getSelectedFields(root, fieldsToSelect));

        // Filters
        if (filters != null && !filters.isEmpty()) {
            query.where(getFilterPredicates(filters, cb, root));
        }

        // Sorting
        if (sortFields != null && !sortFields.isEmpty()) {
            query.orderBy(getSortOrders(sortFields, sortDirection, cb, root));
        }

        List<Object[]> resultList = entityManager.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        return mapObjectsToJson(fieldsToSelect, resultList);
    }

    private List<Selection<?>> getSelectedFields(Root<Salary> root, List<String> fields) {
        List<Selection<?>> selections = new ArrayList<>();
        for (String field : fields) {
            selections.add(root.get(field));
        }
        return selections;
    }

    private Predicate[] getFilterPredicates(MultiValueMap<String, String> filters, CriteriaBuilder cb, Root<Salary> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (filters.containsKey("jobTitle")) {
            predicates.add(cb.equal(root.get("jobTitle"), filters.getFirst("jobTitle")));
        }

        if (filters.containsKey("gender")) {
            predicates.add(cb.equal(root.get("gender"), filters.getFirst("gender")));
        }

        if (filters.containsKey("salary[gte]")) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("salary"), Double.valueOf(Objects.requireNonNull(filters.getFirst("salary[gte]")))));
        }

        if (filters.containsKey("salary[lte]")) {
            predicates.add(cb.lessThanOrEqualTo(root.get("salary"), Double.valueOf(Objects.requireNonNull(filters.getFirst("salary[lte]")))));
        }

        return predicates.toArray(new Predicate[0]);
    }

    private List<Order> getSortOrders(List<String> sortFields, String sortDirection, CriteriaBuilder cb, Root<Salary> root) {
        List<Order> orders = new ArrayList<>();
        boolean isDescending = "DESC".equalsIgnoreCase(sortDirection);

        for (String field : sortFields) {
            orders.add(isDescending ? cb.desc(root.get(field)) : cb.asc(root.get(field)));
        }

        return orders;
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

    private List<String> getAllFieldNames(Root<Salary> root) {
        List<String> allFields = new ArrayList<>();
        root.getModel().getDeclaredSingularAttributes().forEach(attr -> allFields.add(attr.getName()));
        return allFields;
    }
}
