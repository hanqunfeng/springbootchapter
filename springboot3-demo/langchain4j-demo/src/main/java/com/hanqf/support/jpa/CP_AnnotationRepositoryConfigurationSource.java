package com.hanqf.support.jpa;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h1></h1>
 */


public class CP_AnnotationRepositoryConfigurationSource extends AnnotationRepositoryConfigurationSource {
    private final Environment environment;

    public CP_AnnotationRepositoryConfigurationSource(AnnotationMetadata metadata, Class<? extends Annotation> annotation, ResourceLoader resourceLoader, Environment environment, BeanDefinitionRegistry registry, @Nullable BeanNameGenerator generator) {
        super(metadata, annotation, resourceLoader, environment, registry, generator);
        this.environment = environment;
    }

    @Override
    public Streamable<String> getBasePackages() {
        Streamable<String> rawPackages = super.getBasePackages();
        return Streamable.of(() -> rawPackages.stream()
                .flatMap(raw -> parsePackagesSpel(raw).stream())
        );
    }

    private List<String> parsePackagesSpel(@Nullable String rawPackage) {
        Objects.requireNonNull(rawPackage, "Package specification cannot be null");

        if (!rawPackage.trim().startsWith("$")) {
            return Collections.singletonList(rawPackage);
        }

        rawPackage = rawPackage.trim();
        String propertyName = rawPackage.substring("${".length(), rawPackage.length() - "}".length());
        String packages = this.environment.getProperty(propertyName);

        if (!StringUtils.hasText(packages)) {
            throw new IllegalStateException(
                    String.format("Could not resolve the following packages definition: %s", rawPackage));
        }

        return Arrays.stream(packages.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }
}
