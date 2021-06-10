package com.jitterted.mobreg;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"GITHUB_OAUTH=dummy"})
@Tag("integration")
class MobRegApplicationTests {

    @Test
    void contextLoads() {
    }

}
