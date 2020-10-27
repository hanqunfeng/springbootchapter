package com.example.service.impl;

import com.example.dao.ArticalRepository;
import com.example.exception.CustomException;
import com.example.exception.CustomExceptionType;
import com.example.model.Artical;
import com.example.service.ArticalServcie;
import com.example.views.CustomPage;
import com.example.views.CustomSort;
import com.example.views.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <h1>文章service</h1>
 * Created by hanqf on 2020/10/25 10:22.
 */

@Service
@Transactional(rollbackFor = {Exception.class})
//@Transactional(rollbackOn = {Exception.class})
//该注解的作用是表明此类上所有方法上的事务都是CGLib方式代理的。实际上spring会自动判断其代理方式。
//@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
//缓存key的前缀
@CacheConfig(cacheNames = "articalCache")
public class ArticalServcieImpl implements ArticalServcie {
    @Autowired
    private ArticalRepository articalRepository;


    //@Transactional(value = Transactional.TxType.SUPPORTS)
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    //先查询缓存，如果存在则返回缓存的值，不存在则执行方法，并将返回值存储到缓存
    @Cacheable(key = "'ArticalServcieImpl.findById_'+#id")
    public Artical findById(Long id) {
        Artical artical = articalRepository.findById(id).orElse(null);
        if (artical == null) {
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "给定id文章不存在!");
        }
        return artical;
    }


    //@Transactional(value = Transactional.TxType.SUPPORTS)
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Cacheable(key = "'ArticalServcieImpl.findAll'")
    public List<Artical> findAll() {
        return articalRepository.findAll();
    }


    //清除掉指定key的缓存
    @Override
    @Caching(evict = {
            @CacheEvict(key = "'ArticalServcieImpl.findAll'"),
            @CacheEvict(cacheNames = "articalCachePages", allEntries = true)
    })
    public Artical save(Artical artical) {
        if (artical.getId() != null) {
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "新增操作，文章Id不能赋值，修改操作请使用put method!");
        }
        artical = articalRepository.save(artical);
        return artical;
    }

    //一次可以配置多个类型
    @Override
    @Caching(evict = {
            @CacheEvict(key = "'ArticalServcieImpl.findAll'"),
            @CacheEvict(cacheNames = "articalCachePages", allEntries = true)
    }, put = {
            @CachePut(key = "'ArticalServcieImpl.findById_'+#artical.id")
    })
    public Artical update(Artical artical) {
        if (artical.getId() == null) {
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "修改操作，文章Id不能为空!");
        }
        return articalRepository.save(artical);
    }

    //一次可以配置多个类型
    @Override
    @Caching(evict = {
            @CacheEvict(key = "'ArticalServcieImpl.findAll'"),
            @CacheEvict(key = "'ArticalServcieImpl.findById_'+#id"),
            @CacheEvict(cacheNames = "articalCachePages", allEntries = true)
    })
    public void deleteById(Long id) {
        articalRepository.deleteById(id);
    }

    /**
     * <h2>这里返回自定义类型，是因为Page没有无参的构造函数，缓存后取出时不能从json转换为对象</h2>
     * Created by hanqf on 2020/10/26 18:15. <br>
     *
     * @param artical
     * @param page
     * @param sort
     * @return com.example.views.PageList&lt;com.example.model.Artical&gt;
     * @author hanqf
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    //page缓存使用新的分组类型，因为查询参数不确定，所以key是不确定的，可以不指定key
    //@Cacheable(cacheNames = "articalCachePages", key = "'ArticalServcieImpl.pages_' + #artical + #page + #sort")
    @Cacheable(cacheNames = "articalCachePages")
    public PageList<Artical> findAll(Artical artical, CustomPage page, CustomSort sort) {
        PageList<Artical> list = new PageList<>();
        PageRequest pageRequest = PageRequest.of(page.getIndex(), page.getSize(), Sort.by(sort.getOrder()));
        //CONTAINING: 字符串类型模糊匹配，不走索引的，尽量不要使用
        //STARTING: 以字符串开头
        Page<Artical> articalPage = articalRepository.findAll(Example.of(artical, ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.STARTING)), pageRequest);
        if (articalPage.hasContent()) {
            list.setTotal(articalPage.getTotalElements());
            list.setData(articalPage.getContent());
        }
        return list;
    }
}
