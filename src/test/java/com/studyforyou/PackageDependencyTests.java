package com.studyforyou;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = App.class)
public class PackageDependencyTests {

    public static final String STUDY = "..modules.study..";
    public static final String EVENT = "..modules.event..";
    public static final String ACCOUNT = "..modules.account..";
    public static final String TAG = "..modules.tag..";
    public static final String ZONE = "..modules.zone..";

    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage("com.studyforyou.modules..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("com.studyforyou.modules..");

    @ArchTest
    ArchRule studyPackageRule = classes().that().resideInAPackage(STUDY)
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(STUDY,EVENT);

    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAnyPackage(ACCOUNT)
            .should().accessClassesThat().resideInAnyPackage(ZONE, TAG, ACCOUNT);


    @ArchTest
    ArchRule accountPackageRule_beAccessed = classes().that().resideInAPackage(ACCOUNT)
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(STUDY,EVENT,ACCOUNT);

    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAnyPackage(EVENT)
            .should().accessClassesThat().resideInAnyPackage(EVENT, STUDY,ACCOUNT);

    @ArchTest
    ArchRule cycleCheck = slices().matching("com.studyforyou.modules.(*)..")
            .should().beFreeOfCycles();
}
