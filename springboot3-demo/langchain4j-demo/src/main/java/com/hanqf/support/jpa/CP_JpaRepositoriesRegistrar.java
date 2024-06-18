package com.hanqf.support.jpa;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationDelegate;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * <h1>重写JpaRepositoriesRegistrar</h1>
 */


public class CP_JpaRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;
    private Environment environment;

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return CP_EnableJpaRepositories.class;
    }


    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new JpaRepositoryConfigExtension();
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry, BeanNameGenerator generator) {

        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");
        Assert.notNull(annotationMetadata, "AnnotationMetadata must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

        // Guard against calls for sub-classes
        if (annotationMetadata.getAnnotationAttributes(getAnnotation().getName()) == null) {
            return;
        }

        // 使用自定义的CP_AnnotationRepositoryConfigurationSource
        CP_AnnotationRepositoryConfigurationSource configurationSource
                = new CP_AnnotationRepositoryConfigurationSource(annotationMetadata, getAnnotation(), resourceLoader, environment, registry, generator);

        RepositoryConfigurationExtension extension = getExtension();
        RepositoryConfigurationUtils.exposeRegistration(extension, registry, configurationSource);

        RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(configurationSource, resourceLoader, environment);

        delegate.registerRepositoriesIn(registry, extension);
    }
}
