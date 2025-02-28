package com.codelabs.admin.catalago.infrastructure.persistence.category.repository;

import com.codelabs.admin.catalago.infrastructure.persistence.category.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {

    Page<CategoryEntity> findAll(Specification<CategoryEntity> whereClause, Pageable page);
}
