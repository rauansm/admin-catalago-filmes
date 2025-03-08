package com.codelabs.admin.catalago.infrastructure.persistence.castmember.repository;

import com.codelabs.admin.catalago.infrastructure.persistence.castmember.entity.CastMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CastMemberRepository extends JpaRepository<CastMemberEntity, String> {

    Page<CastMemberEntity> findAll(Specification<CastMemberEntity> specification, Pageable page);

    @Query(value = "select c.id from CastMember c where c.id in :ids")
    List<String> existsByIds(@Param("ids") List<String> ids);
}
