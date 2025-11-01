package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final IoLibraryAccessors laccForIoLibraryAccessors = new IoLibraryAccessors(owner);
    private final OrgLibraryAccessors laccForOrgLibraryAccessors = new OrgLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Group of libraries at <b>io</b>
     */
    public IoLibraryAccessors getIo() {
        return laccForIoLibraryAccessors;
    }

    /**
     * Group of libraries at <b>org</b>
     */
    public OrgLibraryAccessors getOrg() {
        return laccForOrgLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class IoLibraryAccessors extends SubDependencyFactory {
        private final IoGithubLibraryAccessors laccForIoGithubLibraryAccessors = new IoGithubLibraryAccessors(owner);

        public IoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>io.github</b>
         */
        public IoGithubLibraryAccessors getGithub() {
            return laccForIoGithubLibraryAccessors;
        }

    }

    public static class IoGithubLibraryAccessors extends SubDependencyFactory {
        private final IoGithubResilience4jLibraryAccessors laccForIoGithubResilience4jLibraryAccessors = new IoGithubResilience4jLibraryAccessors(owner);

        public IoGithubLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>io.github.resilience4j</b>
         */
        public IoGithubResilience4jLibraryAccessors getResilience4j() {
            return laccForIoGithubResilience4jLibraryAccessors;
        }

    }

    public static class IoGithubResilience4jLibraryAccessors extends SubDependencyFactory {
        private final IoGithubResilience4jResilience4jLibraryAccessors laccForIoGithubResilience4jResilience4jLibraryAccessors = new IoGithubResilience4jResilience4jLibraryAccessors(owner);

        public IoGithubResilience4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>io.github.resilience4j.resilience4j</b>
         */
        public IoGithubResilience4jResilience4jLibraryAccessors getResilience4j() {
            return laccForIoGithubResilience4jResilience4jLibraryAccessors;
        }

    }

    public static class IoGithubResilience4jResilience4jLibraryAccessors extends SubDependencyFactory {
        private final IoGithubResilience4jResilience4jSpringLibraryAccessors laccForIoGithubResilience4jResilience4jSpringLibraryAccessors = new IoGithubResilience4jResilience4jSpringLibraryAccessors(owner);

        public IoGithubResilience4jResilience4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>io.github.resilience4j.resilience4j.spring</b>
         */
        public IoGithubResilience4jResilience4jSpringLibraryAccessors getSpring() {
            return laccForIoGithubResilience4jResilience4jSpringLibraryAccessors;
        }

    }

    public static class IoGithubResilience4jResilience4jSpringLibraryAccessors extends SubDependencyFactory {

        public IoGithubResilience4jResilience4jSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>boot3</b> with <b>io.github.resilience4j:resilience4j-spring-boot3</b> coordinates and
         * with version reference <b>io.github.resilience4j.resilience4j.spring.boot3</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getBoot3() {
            return create("io.github.resilience4j.resilience4j.spring.boot3");
        }

    }

    public static class OrgLibraryAccessors extends SubDependencyFactory {
        private final OrgPostgresqlLibraryAccessors laccForOrgPostgresqlLibraryAccessors = new OrgPostgresqlLibraryAccessors(owner);
        private final OrgProjectlombokLibraryAccessors laccForOrgProjectlombokLibraryAccessors = new OrgProjectlombokLibraryAccessors(owner);
        private final OrgSpringframeworkLibraryAccessors laccForOrgSpringframeworkLibraryAccessors = new OrgSpringframeworkLibraryAccessors(owner);

        public OrgLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.postgresql</b>
         */
        public OrgPostgresqlLibraryAccessors getPostgresql() {
            return laccForOrgPostgresqlLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.projectlombok</b>
         */
        public OrgProjectlombokLibraryAccessors getProjectlombok() {
            return laccForOrgProjectlombokLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.springframework</b>
         */
        public OrgSpringframeworkLibraryAccessors getSpringframework() {
            return laccForOrgSpringframeworkLibraryAccessors;
        }

    }

    public static class OrgPostgresqlLibraryAccessors extends SubDependencyFactory {

        public OrgPostgresqlLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>postgresql</b> with <b>org.postgresql:postgresql</b> coordinates and
         * with version reference <b>org.postgresql.postgresql</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getPostgresql() {
            return create("org.postgresql.postgresql");
        }

    }

    public static class OrgProjectlombokLibraryAccessors extends SubDependencyFactory {

        public OrgProjectlombokLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>lombok</b> with <b>org.projectlombok:lombok</b> coordinates and
         * with version reference <b>org.projectlombok.lombok</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getLombok() {
            return create("org.projectlombok.lombok");
        }

    }

    public static class OrgSpringframeworkLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootLibraryAccessors laccForOrgSpringframeworkBootLibraryAccessors = new OrgSpringframeworkBootLibraryAccessors(owner);
        private final OrgSpringframeworkCloudLibraryAccessors laccForOrgSpringframeworkCloudLibraryAccessors = new OrgSpringframeworkCloudLibraryAccessors(owner);
        private final OrgSpringframeworkKafkaLibraryAccessors laccForOrgSpringframeworkKafkaLibraryAccessors = new OrgSpringframeworkKafkaLibraryAccessors(owner);

        public OrgSpringframeworkLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot</b>
         */
        public OrgSpringframeworkBootLibraryAccessors getBoot() {
            return laccForOrgSpringframeworkBootLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.springframework.cloud</b>
         */
        public OrgSpringframeworkCloudLibraryAccessors getCloud() {
            return laccForOrgSpringframeworkCloudLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.springframework.kafka</b>
         */
        public OrgSpringframeworkKafkaLibraryAccessors getKafka() {
            return laccForOrgSpringframeworkKafkaLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringLibraryAccessors laccForOrgSpringframeworkBootSpringLibraryAccessors = new OrgSpringframeworkBootSpringLibraryAccessors(owner);

        public OrgSpringframeworkBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot.spring</b>
         */
        public OrgSpringframeworkBootSpringLibraryAccessors getSpring() {
            return laccForOrgSpringframeworkBootSpringLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringBootLibraryAccessors laccForOrgSpringframeworkBootSpringBootLibraryAccessors = new OrgSpringframeworkBootSpringBootLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot.spring.boot</b>
         */
        public OrgSpringframeworkBootSpringBootLibraryAccessors getBoot() {
            return laccForOrgSpringframeworkBootSpringBootLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringBootStarterLibraryAccessors laccForOrgSpringframeworkBootSpringBootStarterLibraryAccessors = new OrgSpringframeworkBootSpringBootStarterLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot.spring.boot.starter</b>
         */
        public OrgSpringframeworkBootSpringBootStarterLibraryAccessors getStarter() {
            return laccForOrgSpringframeworkBootSpringBootStarterLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors laccForOrgSpringframeworkBootSpringBootStarterDataLibraryAccessors = new OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringBootStarterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>aop</b> with <b>org.springframework.boot:spring-boot-starter-aop</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.aop</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAop() {
            return create("org.springframework.boot.spring.boot.starter.aop");
        }

        /**
         * Dependency provider for <b>test</b> with <b>org.springframework.boot:spring-boot-starter-test</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.test</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getTest() {
            return create("org.springframework.boot.spring.boot.starter.test");
        }

        /**
         * Dependency provider for <b>validation</b> with <b>org.springframework.boot:spring-boot-starter-validation</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.validation</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getValidation() {
            return create("org.springframework.boot.spring.boot.starter.validation");
        }

        /**
         * Dependency provider for <b>web</b> with <b>org.springframework.boot:spring-boot-starter-web</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.web</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getWeb() {
            return create("org.springframework.boot.spring.boot.starter.web");
        }

        /**
         * Group of libraries at <b>org.springframework.boot.spring.boot.starter.data</b>
         */
        public OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors getData() {
            return laccForOrgSpringframeworkBootSpringBootStarterDataLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jpa</b> with <b>org.springframework.boot:spring-boot-starter-data-jpa</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.data.jpa</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJpa() {
            return create("org.springframework.boot.spring.boot.starter.data.jpa");
        }

    }

    public static class OrgSpringframeworkCloudLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringLibraryAccessors laccForOrgSpringframeworkCloudSpringLibraryAccessors = new OrgSpringframeworkCloudSpringLibraryAccessors(owner);

        public OrgSpringframeworkCloudLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring</b>
         */
        public OrgSpringframeworkCloudSpringLibraryAccessors getSpring() {
            return laccForOrgSpringframeworkCloudSpringLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringCloudLibraryAccessors laccForOrgSpringframeworkCloudSpringCloudLibraryAccessors = new OrgSpringframeworkCloudSpringCloudLibraryAccessors(owner);

        public OrgSpringframeworkCloudSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring.cloud</b>
         */
        public OrgSpringframeworkCloudSpringCloudLibraryAccessors getCloud() {
            return laccForOrgSpringframeworkCloudSpringCloudLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors laccForOrgSpringframeworkCloudSpringCloudStarterLibraryAccessors = new OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors(owner);

        public OrgSpringframeworkCloudSpringCloudLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring.cloud.starter</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors getStarter() {
            return laccForOrgSpringframeworkCloudSpringCloudStarterLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors laccForOrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors = new OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors(owner);

        public OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring.cloud.starter.netflix</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors getNetflix() {
            return laccForOrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors laccForOrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors = new OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors(owner);

        public OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring.cloud.starter.netflix.eureka</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors getEureka() {
            return laccForOrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>client</b> with <b>org.springframework.cloud:spring-cloud-starter-netflix-eureka-client</b> coordinates and
         * with version reference <b>org.springframework.cloud.spring.cloud.starter.netflix.eureka.client</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getClient() {
            return create("org.springframework.cloud.spring.cloud.starter.netflix.eureka.client");
        }

    }

    public static class OrgSpringframeworkKafkaLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkKafkaSpringLibraryAccessors laccForOrgSpringframeworkKafkaSpringLibraryAccessors = new OrgSpringframeworkKafkaSpringLibraryAccessors(owner);

        public OrgSpringframeworkKafkaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.kafka.spring</b>
         */
        public OrgSpringframeworkKafkaSpringLibraryAccessors getSpring() {
            return laccForOrgSpringframeworkKafkaSpringLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkKafkaSpringLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkKafkaSpringKafkaLibraryAccessors laccForOrgSpringframeworkKafkaSpringKafkaLibraryAccessors = new OrgSpringframeworkKafkaSpringKafkaLibraryAccessors(owner);

        public OrgSpringframeworkKafkaSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.kafka.spring.kafka</b>
         */
        public OrgSpringframeworkKafkaSpringKafkaLibraryAccessors getKafka() {
            return laccForOrgSpringframeworkKafkaSpringKafkaLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkKafkaSpringKafkaLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public OrgSpringframeworkKafkaSpringKafkaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>kafka</b> with <b>org.springframework.kafka:spring-kafka</b> coordinates and
         * with version reference <b>org.springframework.kafka.spring.kafka</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("org.springframework.kafka.spring.kafka");
        }

        /**
         * Dependency provider for <b>test</b> with <b>org.springframework.kafka:spring-kafka-test</b> coordinates and
         * with version reference <b>org.springframework.kafka.spring.kafka.test</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getTest() {
            return create("org.springframework.kafka.spring.kafka.test");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final IoVersionAccessors vaccForIoVersionAccessors = new IoVersionAccessors(providers, config);
        private final OrgVersionAccessors vaccForOrgVersionAccessors = new OrgVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io</b>
         */
        public IoVersionAccessors getIo() {
            return vaccForIoVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org</b>
         */
        public OrgVersionAccessors getOrg() {
            return vaccForOrgVersionAccessors;
        }

    }

    public static class IoVersionAccessors extends VersionFactory  {

        private final IoGithubVersionAccessors vaccForIoGithubVersionAccessors = new IoGithubVersionAccessors(providers, config);
        public IoVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io.github</b>
         */
        public IoGithubVersionAccessors getGithub() {
            return vaccForIoGithubVersionAccessors;
        }

    }

    public static class IoGithubVersionAccessors extends VersionFactory  {

        private final IoGithubResilience4jVersionAccessors vaccForIoGithubResilience4jVersionAccessors = new IoGithubResilience4jVersionAccessors(providers, config);
        public IoGithubVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io.github.resilience4j</b>
         */
        public IoGithubResilience4jVersionAccessors getResilience4j() {
            return vaccForIoGithubResilience4jVersionAccessors;
        }

    }

    public static class IoGithubResilience4jVersionAccessors extends VersionFactory  {

        private final IoGithubResilience4jResilience4jVersionAccessors vaccForIoGithubResilience4jResilience4jVersionAccessors = new IoGithubResilience4jResilience4jVersionAccessors(providers, config);
        public IoGithubResilience4jVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io.github.resilience4j.resilience4j</b>
         */
        public IoGithubResilience4jResilience4jVersionAccessors getResilience4j() {
            return vaccForIoGithubResilience4jResilience4jVersionAccessors;
        }

    }

    public static class IoGithubResilience4jResilience4jVersionAccessors extends VersionFactory  {

        private final IoGithubResilience4jResilience4jSpringVersionAccessors vaccForIoGithubResilience4jResilience4jSpringVersionAccessors = new IoGithubResilience4jResilience4jSpringVersionAccessors(providers, config);
        public IoGithubResilience4jResilience4jVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io.github.resilience4j.resilience4j.spring</b>
         */
        public IoGithubResilience4jResilience4jSpringVersionAccessors getSpring() {
            return vaccForIoGithubResilience4jResilience4jSpringVersionAccessors;
        }

    }

    public static class IoGithubResilience4jResilience4jSpringVersionAccessors extends VersionFactory  {

        public IoGithubResilience4jResilience4jSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>io.github.resilience4j.resilience4j.spring.boot3</b> with value <b>2.2.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getBoot3() { return getVersion("io.github.resilience4j.resilience4j.spring.boot3"); }

    }

    public static class OrgVersionAccessors extends VersionFactory  {

        private final OrgPostgresqlVersionAccessors vaccForOrgPostgresqlVersionAccessors = new OrgPostgresqlVersionAccessors(providers, config);
        private final OrgProjectlombokVersionAccessors vaccForOrgProjectlombokVersionAccessors = new OrgProjectlombokVersionAccessors(providers, config);
        private final OrgSpringframeworkVersionAccessors vaccForOrgSpringframeworkVersionAccessors = new OrgSpringframeworkVersionAccessors(providers, config);
        public OrgVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.postgresql</b>
         */
        public OrgPostgresqlVersionAccessors getPostgresql() {
            return vaccForOrgPostgresqlVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.projectlombok</b>
         */
        public OrgProjectlombokVersionAccessors getProjectlombok() {
            return vaccForOrgProjectlombokVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.springframework</b>
         */
        public OrgSpringframeworkVersionAccessors getSpringframework() {
            return vaccForOrgSpringframeworkVersionAccessors;
        }

    }

    public static class OrgPostgresqlVersionAccessors extends VersionFactory  {

        public OrgPostgresqlVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.postgresql.postgresql</b> with value <b>42.7.7</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getPostgresql() { return getVersion("org.postgresql.postgresql"); }

    }

    public static class OrgProjectlombokVersionAccessors extends VersionFactory  {

        public OrgProjectlombokVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.projectlombok.lombok</b> with value <b>1.18.38</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLombok() { return getVersion("org.projectlombok.lombok"); }

    }

    public static class OrgSpringframeworkVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootVersionAccessors vaccForOrgSpringframeworkBootVersionAccessors = new OrgSpringframeworkBootVersionAccessors(providers, config);
        private final OrgSpringframeworkCloudVersionAccessors vaccForOrgSpringframeworkCloudVersionAccessors = new OrgSpringframeworkCloudVersionAccessors(providers, config);
        private final OrgSpringframeworkKafkaVersionAccessors vaccForOrgSpringframeworkKafkaVersionAccessors = new OrgSpringframeworkKafkaVersionAccessors(providers, config);
        public OrgSpringframeworkVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot</b>
         */
        public OrgSpringframeworkBootVersionAccessors getBoot() {
            return vaccForOrgSpringframeworkBootVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.springframework.cloud</b>
         */
        public OrgSpringframeworkCloudVersionAccessors getCloud() {
            return vaccForOrgSpringframeworkCloudVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.springframework.kafka</b>
         */
        public OrgSpringframeworkKafkaVersionAccessors getKafka() {
            return vaccForOrgSpringframeworkKafkaVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringVersionAccessors vaccForOrgSpringframeworkBootSpringVersionAccessors = new OrgSpringframeworkBootSpringVersionAccessors(providers, config);
        public OrgSpringframeworkBootVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring</b>
         */
        public OrgSpringframeworkBootSpringVersionAccessors getSpring() {
            return vaccForOrgSpringframeworkBootSpringVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringBootVersionAccessors vaccForOrgSpringframeworkBootSpringBootVersionAccessors = new OrgSpringframeworkBootSpringBootVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring.boot</b>
         */
        public OrgSpringframeworkBootSpringBootVersionAccessors getBoot() {
            return vaccForOrgSpringframeworkBootSpringBootVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringBootStarterVersionAccessors vaccForOrgSpringframeworkBootSpringBootStarterVersionAccessors = new OrgSpringframeworkBootSpringBootStarterVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringBootVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring.boot.starter</b>
         */
        public OrgSpringframeworkBootSpringBootStarterVersionAccessors getStarter() {
            return vaccForOrgSpringframeworkBootSpringBootStarterVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringBootStarterDataVersionAccessors vaccForOrgSpringframeworkBootSpringBootStarterDataVersionAccessors = new OrgSpringframeworkBootSpringBootStarterDataVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringBootStarterVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.aop</b> with value <b>3.5.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAop() { return getVersion("org.springframework.boot.spring.boot.starter.aop"); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.test</b> with value <b>3.5.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getTest() { return getVersion("org.springframework.boot.spring.boot.starter.test"); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.validation</b> with value <b>3.5.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getValidation() { return getVersion("org.springframework.boot.spring.boot.starter.validation"); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.web</b> with value <b>3.5.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getWeb() { return getVersion("org.springframework.boot.spring.boot.starter.web"); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring.boot.starter.data</b>
         */
        public OrgSpringframeworkBootSpringBootStarterDataVersionAccessors getData() {
            return vaccForOrgSpringframeworkBootSpringBootStarterDataVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterDataVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkBootSpringBootStarterDataVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.data.jpa</b> with value <b>3.5.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJpa() { return getVersion("org.springframework.boot.spring.boot.starter.data.jpa"); }

    }

    public static class OrgSpringframeworkCloudVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringVersionAccessors vaccForOrgSpringframeworkCloudSpringVersionAccessors = new OrgSpringframeworkCloudSpringVersionAccessors(providers, config);
        public OrgSpringframeworkCloudVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring</b>
         */
        public OrgSpringframeworkCloudSpringVersionAccessors getSpring() {
            return vaccForOrgSpringframeworkCloudSpringVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringCloudVersionAccessors vaccForOrgSpringframeworkCloudSpringCloudVersionAccessors = new OrgSpringframeworkCloudSpringCloudVersionAccessors(providers, config);
        public OrgSpringframeworkCloudSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring.cloud</b>
         */
        public OrgSpringframeworkCloudSpringCloudVersionAccessors getCloud() {
            return vaccForOrgSpringframeworkCloudSpringCloudVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringCloudStarterVersionAccessors vaccForOrgSpringframeworkCloudSpringCloudStarterVersionAccessors = new OrgSpringframeworkCloudSpringCloudStarterVersionAccessors(providers, config);
        public OrgSpringframeworkCloudSpringCloudVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring.cloud.starter</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterVersionAccessors getStarter() {
            return vaccForOrgSpringframeworkCloudSpringCloudStarterVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors vaccForOrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors = new OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors(providers, config);
        public OrgSpringframeworkCloudSpringCloudStarterVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring.cloud.starter.netflix</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors getNetflix() {
            return vaccForOrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors vaccForOrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors = new OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors(providers, config);
        public OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring.cloud.starter.netflix.eureka</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors getEureka() {
            return vaccForOrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.cloud.spring.cloud.starter.netflix.eureka.client</b> with value <b>4.3.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getClient() { return getVersion("org.springframework.cloud.spring.cloud.starter.netflix.eureka.client"); }

    }

    public static class OrgSpringframeworkKafkaVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkKafkaSpringVersionAccessors vaccForOrgSpringframeworkKafkaSpringVersionAccessors = new OrgSpringframeworkKafkaSpringVersionAccessors(providers, config);
        public OrgSpringframeworkKafkaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.kafka.spring</b>
         */
        public OrgSpringframeworkKafkaSpringVersionAccessors getSpring() {
            return vaccForOrgSpringframeworkKafkaSpringVersionAccessors;
        }

    }

    public static class OrgSpringframeworkKafkaSpringVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkKafkaSpringKafkaVersionAccessors vaccForOrgSpringframeworkKafkaSpringKafkaVersionAccessors = new OrgSpringframeworkKafkaSpringKafkaVersionAccessors(providers, config);
        public OrgSpringframeworkKafkaSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.kafka.spring.kafka</b>
         */
        public OrgSpringframeworkKafkaSpringKafkaVersionAccessors getKafka() {
            return vaccForOrgSpringframeworkKafkaSpringKafkaVersionAccessors;
        }

    }

    public static class OrgSpringframeworkKafkaSpringKafkaVersionAccessors extends VersionFactory  implements VersionNotationSupplier {

        public OrgSpringframeworkKafkaSpringKafkaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.kafka.spring.kafka</b> with value <b>3.3.7</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> asProvider() { return getVersion("org.springframework.kafka.spring.kafka"); }

        /**
         * Version alias <b>org.springframework.kafka.spring.kafka.test</b> with value <b>3.3.7</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getTest() { return getVersion("org.springframework.kafka.spring.kafka.test"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
