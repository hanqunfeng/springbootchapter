package com.example.service;/**
 * Created by hanqf on 2020/3/5 16:49.
 */

import com.example.dao.one.OneJpaUserRepository;
import com.example.dao.two.TwoJpaUserRepository;
import com.example.model.one.OneUser;
import com.example.model.two.TwoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hanqf
 * @date 2020/3/5 16:49
 */
@Service
@Transactional(transactionManager = "transactionManager",propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private OneJpaUserRepository oneJpaUserRepository;
    @Autowired
    private TwoJpaUserRepository twoJpaUserRepository;

    @Override
    public void save1And2(OneUser oneUser, TwoUser twoUser) {
        logger.debug("save1And2 debug");
        saveOne(oneUser);
        saveTwo(twoUser);
        throw new RuntimeException("ERROR-------");
    }

    @Override
    public void saveOne(OneUser oneUser) {
        oneJpaUserRepository.save(oneUser);
    }

    @Override
    public void saveTwo(TwoUser twoUser) {
        twoJpaUserRepository.save(twoUser);
    }
}
