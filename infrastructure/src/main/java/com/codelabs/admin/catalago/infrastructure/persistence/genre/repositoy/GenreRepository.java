package com.codelabs.admin.catalago.infrastructure.persistence.genre.repositoy;

import com.codelabs.admin.catalago.infrastructure.persistence.genre.entity.GenreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<GenreEntity, String> {

    Page<GenreEntity> findAll(Specification<GenreEntity> whereClause, Pageable page);
}
