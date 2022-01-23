package com.jitterted.mobreg;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class HexagonalArchitectureTest {
    @Test
    public void domainMustNotDependOnAdapters() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..adapter..")
                .check(productionAndTestClasses());
    }

    @Test
    public void domainMustNotDependOnApplication() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..application..")
                .check(productionClasses());
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
               .check(productionAndTestClasses());
    }

    private JavaClasses productionAndTestClasses() {
        return new ClassFileImporter().importPackages("com.jitterted.mobreg");
    }

    private JavaClasses productionClasses() {
        return new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.jitterted.mobreg");
    }
}
