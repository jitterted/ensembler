package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class EnsembleRepositoryDataJdbcAdapter implements EnsembleRepository {

    private final EnsembleJdbcRepository ensembleJdbcRepository;

    public EnsembleRepositoryDataJdbcAdapter(EnsembleJdbcRepository ensembleJdbcRepository) {
        this.ensembleJdbcRepository = ensembleJdbcRepository;
    }

    @Override
    public Ensemble save(Ensemble ensemble) {
        EnsembleDbo ensembleDbo = EnsembleDbo.from(ensemble);
        EnsembleDbo savedEnsembleDbo = ensembleJdbcRepository.save(ensembleDbo);
        return savedEnsembleDbo.asEnsemble();
    }

    @Override
    public List<Ensemble> findAll() {
        return StreamSupport.stream(ensembleJdbcRepository.findAll().spliterator(), false)
                            .map(EnsembleDbo::asEnsemble)
                            .toList();
    }

    @Override
    public Optional<Ensemble> findById(EnsembleId ensembleId) {
        return ensembleJdbcRepository
                .findById(ensembleId.id())
                .map(EnsembleDbo::asEnsemble);
    }
}
