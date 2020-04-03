package com.example.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 实现新的工厂类，目的是把自定义实现BaseJpaRepositoryImpl引入进来
 * @EnableJpaRepositories(basePackages = "com.example.dao",repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
 */
public class BaseRepositoryFactoryBean<T extends JpaRepository<S, ID>, S, ID extends Serializable> extends JpaRepositoryFactoryBean<T, S, ID> {

    public BaseRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new BaseRepositoryFactory(entityManager);
    }
}

class BaseRepositoryFactory extends JpaRepositoryFactory {
    public BaseRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }


    //指定实现类
    @Override
    protected SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        BaseJpaRepositoryImpl customRepository = new BaseJpaRepositoryImpl(JpaEntityInformationSupport.getEntityInformation((Class) information.getDomainType(), entityManager), entityManager);
        return customRepository;
    }

    //指定实现类类型
    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return BaseJpaRepositoryImpl.class;
    }
}
