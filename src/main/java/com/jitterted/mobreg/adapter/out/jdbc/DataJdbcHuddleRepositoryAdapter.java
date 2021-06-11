package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.HuddleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class DataJdbcHuddleRepositoryAdapter implements HuddleRepository {

    private final JdbcHuddleRepository jdbcHuddleRepository;

    @Autowired
    public DataJdbcHuddleRepositoryAdapter(JdbcHuddleRepository jdbcHuddleRepository) {
        this.jdbcHuddleRepository = jdbcHuddleRepository;
    }

    @Override
    public Huddle save(Huddle huddle) {
        HuddleEntity huddleEntity = HuddleEntity.from(huddle);
        HuddleEntity savedHuddleEntity = jdbcHuddleRepository.save(huddleEntity);
        return savedHuddleEntity.asHuddle();
    }

    @Override
    public List<Huddle> findAll() {
        return StreamSupport.stream(
                jdbcHuddleRepository.findAll().spliterator(), false)
                            .map(HuddleEntity::asHuddle)
                            .toList();
    }

    @Override
    public Optional<Huddle> findById(HuddleId huddleId) {
        Optional<HuddleEntity> found = jdbcHuddleRepository.findById(huddleId.id());
        return found.map(HuddleEntity::asHuddle);
    }
}
