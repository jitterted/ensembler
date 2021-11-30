package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.application.port.HuddleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class HuddleRepositoryDataJdbcAdapter implements HuddleRepository {

    private final HuddleJdbcRepository huddleJdbcRepository;

    @Autowired
    public HuddleRepositoryDataJdbcAdapter(HuddleJdbcRepository huddleJdbcRepository) {
        this.huddleJdbcRepository = huddleJdbcRepository;
    }

    @Override
    public Ensemble save(Ensemble ensemble) {
        HuddleEntity huddleEntity = HuddleEntity.from(ensemble);
        HuddleEntity savedHuddleEntity = huddleJdbcRepository.save(huddleEntity);
        return savedHuddleEntity.asHuddle();
    }

    @Override
    public List<Ensemble> findAll() {
        return StreamSupport.stream(
                huddleJdbcRepository.findAll().spliterator(), false)
                            .map(HuddleEntity::asHuddle)
                            .toList();
    }

    @Override
    public Optional<Ensemble> findById(EnsembleId ensembleId) {
        Optional<HuddleEntity> found = huddleJdbcRepository.findById(ensembleId.id());
        return found.map(HuddleEntity::asHuddle);
    }
}
