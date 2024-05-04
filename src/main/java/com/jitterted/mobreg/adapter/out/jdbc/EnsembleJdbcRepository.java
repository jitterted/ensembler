package com.jitterted.mobreg.adapter.out.jdbc;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EnsembleJdbcRepository extends CrudRepository<EnsembleDbo, Long> {
    long countAllByNameContainingIgnoreCase(String name);

    @Modifying
    @Query("DELETE FROM ensembles WHERE name = :name")
    void deleteWhereNameEquals(@Param("name") String name);
}
