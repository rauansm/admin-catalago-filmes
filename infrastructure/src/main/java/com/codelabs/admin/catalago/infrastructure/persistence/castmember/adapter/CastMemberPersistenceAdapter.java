package com.codelabs.admin.catalago.infrastructure.persistence.castmember.adapter;

import com.codelabs.admin.catalago.application.ports.out.CastMemberPort;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.common.stereotype.PersistenceAdapter;
import com.codelabs.admin.catalago.common.utils.SpecificationUtils;
import com.codelabs.admin.catalago.domain.castmember.CastMember;
import com.codelabs.admin.catalago.domain.castmember.CastMemberID;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import com.codelabs.admin.catalago.infrastructure.persistence.castmember.entity.CastMemberEntity;
import com.codelabs.admin.catalago.infrastructure.persistence.castmember.repository.CastMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static net.logstash.logback.marker.Markers.append;

@PersistenceAdapter
@RequiredArgsConstructor
@Slf4j
public class CastMemberPersistenceAdapter implements CastMemberPort {

    private final CastMemberRepository castMemberRepository;

    @Override
    public CastMember save(final CastMember castMember) {
        log.info(append("cast member", castMember), "Starting cast member persistence in the database...");

        final CastMemberEntity entity = CastMemberEntity.from(castMember);
        log.info(append("entity", entity), "Object mapped successfully!");

        final CastMemberEntity savedEntity = this.castMemberRepository.save(entity);

        log.info(append("entity", savedEntity), "Cast member persisted successfully!");
        return savedEntity.toAggregate();
    }

    @Override
    public CastMember getById(final CastMemberID id) {
        log.info("Searching cast member in the database... {}", id.getValue());

        final Optional<CastMemberEntity> castMemberEntity = this.castMemberRepository.findById(id.getValue());

        castMemberEntity.ifPresent(
                entity -> log.info(append("entity", entity), "Cast member found successfully!"));

        final CastMember castMember = castMemberEntity
                .map(CastMemberEntity::toAggregate)
                .orElseThrow(() -> new NotFoundException(String.format("Cast member not found in database with id %s", id.getValue())));

        log.info(append("cast memeber", castMember), "Entity to domain mapping done!");
        return castMember;
    }

    @Override
    public void deleteById(final CastMemberID id) {
        log.info("Starting cast member deletion in the database... {}", id.getValue());

        final String idValue = id.getValue();
        if (this.castMemberRepository.existsById(idValue)) {
            this.castMemberRepository.deleteById(idValue);
            log.info("Cast member deleted successfully! {}", idValue);
        }
    }

    @Override
    public Pagination<CastMember> listCastMembers(final SearchQuery query) {
        log.info(append("params", query), "Searching cast members in database by parameters");

        final var page = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.by(Sort.Direction.fromString(query.direction()), query.sort())
        );

        final var where = Optional.ofNullable(query.terms())
                .filter(str -> !str.isBlank())
                .map(this::assembleSpecification)
                .orElse(null);

        final var pageResult =
                this.castMemberRepository.findAll(where, page);
        log.info("were found {} cast members", kv("members_size", pageResult.getTotalElements()));

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CastMemberEntity::toAggregate).toList()
        );
    }

    @Override
    public List<CastMemberID> existsByIds(final Iterable<CastMemberID> memberIDS) {
        log.info("Searching cast member ids in the database... {}", memberIDS);

        final var ids = StreamSupport.stream(memberIDS.spliterator(), false)
                .map(CastMemberID::getValue)
                .toList();

        return this.castMemberRepository.existsByIds(ids).stream()
                .peek(id -> log.info("id found in database: {}", id))
                .map(CastMemberID::from)
                .toList();
    }

    private Specification<CastMemberEntity> assembleSpecification(final String terms) {
        return SpecificationUtils.like("name", terms);
    }

}
