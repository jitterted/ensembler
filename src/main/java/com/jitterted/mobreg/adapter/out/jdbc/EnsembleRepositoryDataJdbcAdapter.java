package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class EnsembleRepositoryDataJdbcAdapter implements EnsembleRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnsembleRepositoryDataJdbcAdapter.class);

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
        long start = System.nanoTime();
        Iterable<EnsembleDbo> allEnsembleDbos = ensembleJdbcRepository.findAll();
        LOGGER.info("Find All Ensemble DBOs Query took {}ms", (System.nanoTime() - start) / 1_000_000);
        start = System.nanoTime();
        List<Ensemble> ensembleList = StreamSupport.stream(allEnsembleDbos.spliterator(), false)
                .map(EnsembleDbo::asEnsemble)
                .toList();
        LOGGER.info("Mapping DBOs to Ensembles took {}ms", (System.nanoTime() - start) / 1_000_000);
        return ensembleList;
    }

    @Override
    public Optional<Ensemble> findById(EnsembleId ensembleId) {
        return ensembleJdbcRepository
                .findById(ensembleId.id())
                .map(EnsembleDbo::asEnsemble);
    }
}
