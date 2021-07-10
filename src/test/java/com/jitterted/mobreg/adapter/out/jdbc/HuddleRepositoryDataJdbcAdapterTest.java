package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Tag("integration")
class HuddleRepositoryDataJdbcAdapterTest {

    @Autowired
    HuddleRepositoryDataJdbcAdapter huddleRepositoryAdapter;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @MockBean
    MemberService memberService;

    @Test
    public void savedHuddleCanBeFoundByItsId() throws Exception {
        Huddle huddle = createHuddleWithRegisteredMemberNamed("test huddle");

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
        Huddle one = createHuddleWithRegisteredMemberNamed("one");
        Huddle two = createHuddleWithRegisteredMemberNamed("two");

        huddleRepositoryAdapter.save(one);
        huddleRepositoryAdapter.save(two);

        assertThat(huddleRepositoryAdapter.findAll())
                .hasSize(2);
    }

    @NotNull
    private Huddle createHuddleWithRegisteredMemberNamed(String huddleName) {
        Huddle huddle = new Huddle(huddleName, ZonedDateTime.now());
        huddle.register(
                new Member(
                        "test participant", "github"));
        return huddle;
    }
}