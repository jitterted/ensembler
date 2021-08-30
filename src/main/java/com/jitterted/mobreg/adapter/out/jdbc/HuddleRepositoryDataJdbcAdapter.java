package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.port.HuddleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class HuddleRepositoryDataJdbcAdapter implements HuddleRepository {

    private final HuddleJdbcRepository huddleJdbcRepository;

    @Autowired
    public HuddleRepositoryDataJdbcAdapter(HuddleJdbcRepository huddleJdbcRepository) {
        this.huddleJdbcRepository = huddleJdbcRepository;
    }

    @Override
    public Huddle save(Huddle huddle) {
        HuddleEntity huddleEntity = HuddleEntity.from(huddle);
        HuddleEntity savedHuddleEntity = huddleJdbcRepository.save(huddleEntity);
        return savedHuddleEntity.asHuddle();
    }

    @Override
    public List<Huddle> findAll() {
        return StreamSupport.stream(
                huddleJdbcRepository.findAll().spliterator(), false)
                            .map(HuddleEntity::asHuddle)
                            .collect(Collectors.toList());
    }

    @Override
    public Optional<Huddle> findById(HuddleId huddleId) {
        Optional<HuddleEntity> found = huddleJdbcRepository.findById(huddleId.id());
        return found.map(HuddleEntity::asHuddle);
    }
}
