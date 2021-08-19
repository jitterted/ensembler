package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.MemberId;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class HuddleRepositoryDataJdbcAdapterTest extends TestContainerBase {

    @Autowired
    HuddleRepositoryDataJdbcAdapter huddleRepositoryAdapter;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Test
    public void savedHuddleCanBeFoundByItsId() throws Exception {
        Huddle huddle = createWithRegisteredMemberHuddleNamed("test huddle");

        Huddle savedHuddle = huddleRepositoryAdapter.save(huddle);

        Optional<Huddle> found = huddleRepositoryAdapter.findById(savedHuddle.getId());

        assertThat(found)
                .isPresent()
                .get()
                .extracting(Huddle::name)
                .isEqualTo("test huddle");
    }

    @Test
    public void newRepositoryReturnsEmptyForFindAll() throws Exception {
        List<Huddle> huddles = huddleRepositoryAdapter.findAll();

        assertThat(huddles)
                .isEmpty();
    }

    @Test
    public void twoSavedHuddlesBothReturnedByFindAll() throws Exception {
        Huddle one = createWithRegisteredMemberHuddleNamed("one");
        Huddle two = createWithRegisteredMemberHuddleNamed("two");

        huddleRepositoryAdapter.save(one);
        huddleRepositoryAdapter.save(two);

        List<Huddle> allHuddles = huddleRepositoryAdapter.findAll();
        assertThat(allHuddles)
                .hasSize(2);

        assertThat(allHuddles.get(0).registeredMembers())
                .hasSize(1)
                .containsOnly(MemberId.of(7L));
        assertThat(allHuddles.get(1).registeredMembers())
                .hasSize(1)
                .containsOnly(MemberId.of(7L));
    }

    @NotNull
    private Huddle createWithRegisteredMemberHuddleNamed(String huddleName) {
        Huddle huddle = new Huddle(huddleName, ZonedDateTime.now());
        huddle.registerById(MemberId.of(7L));
        return huddle;
    }
}