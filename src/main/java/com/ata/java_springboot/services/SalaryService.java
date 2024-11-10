package com.ata.java_springboot.services;

import com.ata.java_springboot.entities.Salary;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class SalaryService {

    @PersistenceContext
    private final EntityManager entityManager;

    public List<Object[]> getSalaryByCriteria(Map<String, String> filters, List<String> selectedFields,
                                              List<String> sortFields, String sortDirection) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Salary> root = query.from(Salary.class);

        // Select Fields
        query.multiselect(getSelectedFields(root, selectedFields));

        // Filters
        query.where(getFilterPredicates(filters, cb, root));

        // Sorting
        query.orderBy(getSortOrders(sortFields, sortDirection, cb, root));

        return entityManager.createQuery(query).getResultList();
    }

    private List<Selection<?>> getSelectedFields(Root<Salary> root, List<String> fields) {
        List<Selection<?>> selections = new ArrayList<>();
        for (String field : fields) {
            selections.add(root.get(field));
        }
        return selections;
    }

    private Predicate[] getFilterPredicates(Map<String, String> filters, CriteriaBuilder cb, Root<Salary> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (filters.containsKey("jobTitle")) {
            predicates.add(cb.equal(root.get("jobTitle"), filters.get("jobTitle")));
        }

        if (filters.containsKey("gender")) {
            predicates.add(cb.equal(root.get("gender"), filters.get("gender")));
        }

        if (filters.containsKey("salary[gte]")) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("salary"), Double.valueOf(filters.get("salary[gte]"))));
        }

        if (filters.containsKey("salary[lte]")) {
            predicates.add(cb.lessThanOrEqualTo(root.get("salary"), Double.valueOf(filters.get("salary[lte]"))));
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
}
