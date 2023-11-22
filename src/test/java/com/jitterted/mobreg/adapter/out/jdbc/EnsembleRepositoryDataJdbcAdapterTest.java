package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.MemberId;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Import(EnsembleRepositoryDataJdbcAdapter.class) // @Repository classes aren't created as part of @DataJdbcTest slicing
class EnsembleRepositoryDataJdbcAdapterTest extends PostgresTestcontainerBase {

    @Autowired
    EnsembleRepositoryDataJdbcAdapter ensembleRepositoryAdapter;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Test
    void savedEnsembleCanBeFoundByItsId() throws Exception {
        Ensemble ensemble = createWithRegisteredMemberEnsembleNamed("test ensemble");

        Ensemble savedEnsemble = ensembleRepositoryAdapter.save(ensemble);

        Optional<Ensemble> found = ensembleRepositoryAdapter.findById(savedEnsemble.getId());

        assertThat(found)
                .isPresent()
                .get()
                .extracting(Ensemble::name)
                .isEqualTo("test ensemble");
    }

    @Test
    void newRepositoryReturnsEmptyForFindAll() throws Exception {
        List<Ensemble> ensembles = ensembleRepositoryAdapter.findAll();

        assertThat(ensembles)
                .isEmpty();
    }

    @Test
    void twoSavedEnsemblesBothReturnedByFindAll() throws Exception {
        Ensemble one = createWithRegisteredMemberEnsembleNamed("one");
        Ensemble two = createWithRegisteredMemberEnsembleNamed("two");

        ensembleRepositoryAdapter.save(one);
        ensembleRepositoryAdapter.save(two);

        List<Ensemble> allEnsembles = ensembleRepositoryAdapter.findAll();
        assertThat(allEnsembles)
                .hasSize(2);

        assertThat(allEnsembles.get(0).acceptedMembers())
                .hasSize(1)
                .containsOnly(MemberId.of(7L));
        assertThat(allEnsembles.get(1).acceptedMembers())
                .hasSize(1)
                .containsOnly(MemberId.of(7L));
    }

    @Test
    void whenEnsembleMeetingLinkIsStoredThenIsRetrievedByFind() throws Exception {
        Ensemble zoom = new Ensemble("With Zoom", URI.create("https://zoom.us/j/123456?pwd=12345"), ZonedDateTime.now());

        EnsembleId savedId = ensembleRepositoryAdapter.save(zoom).getId();

        Optional<Ensemble> found = ensembleRepositoryAdapter.findById(savedId);
        assertThat(found)
                .isPresent()
                .get()
                .extracting(Ensemble::meetingLink)
                .extracting(URI::toString)
                .isEqualTo("https://zoom.us/j/123456?pwd=12345");
    }

    @Test
    void whenEnsembleCompletedWithRecordingLinkThenIsStoredSuccessfully() throws Exception {
        Ensemble ensemble = new Ensemble("Completed", ZonedDateTime.now());
        ensemble.complete();
        ensemble.linkToRecordingAt(URI.create("https://recording.link/database"));

        EnsembleId savedId = ensembleRepositoryAdapter.save(ensemble).getId();

        Ensemble found = ensembleRepositoryAdapter.findById(savedId).get();

        assertThat(found.isCompleted())
                .isTrue();
        assertThat(found.recordingLink().toString())
                .isEqualTo("https://recording.link/database");
    }

    @NotNull
    private Ensemble createWithRegisteredMemberEnsembleNamed(String name) {
        Ensemble ensemble = new Ensemble(name, ZonedDateTime.now());
        ensemble.acceptedBy(MemberId.of(7L));
        return ensemble;
    }
}