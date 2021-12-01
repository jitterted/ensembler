package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class EnsembleRepositoryDataJdbcAdapter implements EnsembleRepository {

    private final EnsembleJdbcRepository ensembleJdbcRepository;

    @Autowired
    public EnsembleRepositoryDataJdbcAdapter(EnsembleJdbcRepository ensembleJdbcRepository) {
        this.ensembleJdbcRepository = ensembleJdbcRepository;
    }

    @Override
    public Ensemble save(Ensemble ensemble) {
        EnsembleEntity ensembleEntity = EnsembleEntity.from(ensemble);
        EnsembleEntity savedEnsembleEntity = ensembleJdbcRepository.save(ensembleEntity);
        return savedEnsembleEntity.asEnsemble();
    }

    @Override
    public List<Ensemble> findAll() {
        return StreamSupport.stream(
                                    ensembleJdbcRepository.findAll().spliterator(), false)
                            .map(EnsembleEntity::asEnsemble)
                            .toList();
    }

    @Override
    public Optional<Ensemble> findById(EnsembleId ensembleId) {
        Optional<EnsembleEntity> found = ensembleJdbcRepository.findById(ensembleId.id());
        return found.map(EnsembleEntity::asEnsemble);
    }
}
