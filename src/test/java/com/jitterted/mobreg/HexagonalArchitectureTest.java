package com.jitterted.mobreg;

import com.jitterted.mobreg.adapter.in.web.admin.InviteEditor;
import com.jitterted.mobreg.adapter.in.web.admin.InviteEditorTest;
import com.jitterted.mobreg.adapter.out.jdbc.InviteJdbcRepository;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@Tag("architecture")
class HexagonalArchitectureTest {
    @Test
    public void domainMustNotDependOnAnythingOutsideOfDomain() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..adapter..")
                .orShould().dependOnClassesThat().resideInAPackage("..application..")
                .orShould().dependOnClassesThat().resideInAPackage("com.jitterted.mobreg")
                .check(productionAndTestClasses());
    }

    @Test
    public void applicationMustNotDependOnAdapters() {
        noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..adapter..")
                .check(productionAndTestClasses());
    }

    @Test
    public void adaptersMustNotDependOnEachOther() {
        slices().matching("..adapter.*.(*)..")
                .should().notDependOnEachOther()
                .as("Adapters must not depend on each other")
                .ignoreDependency(InviteEditor.class, InviteJdbcRepository.class)
                .ignoreDependency(InviteEditorTest.class, InviteJdbcRepository.class)
                .check(productionAndTestClasses());
    }

    @Test
    void domainAndApplicationMustNotAccessSystemTime() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .or().resideInAPackage("..application..")

                .should().callMethod(Clock.class, "instant")
                .orShould().callMethod(Instant.class, "now")
                .orShould().callMethod(ZonedDateTime.class, "now")
                .orShould().callMethod(LocalDateTime.class, "now")

                .as("Application and Domain must not access System Time.")
                .check(new ClassFileImporter()
                               .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                               .importPackages("com.jitterted.mobreg"));
    }

    private JavaClasses productionAndTestClasses() {
        return new ClassFileImporter().importPackages("com.jitterted.mobreg");
    }

}
