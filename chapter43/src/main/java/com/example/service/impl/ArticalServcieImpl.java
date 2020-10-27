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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
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

        //指定查询方法
        Page<Artical> articalPage = specificationFindAll(artical, pageRequest);

        if (articalPage.hasContent()) {
            list.setTotal(articalPage.getTotalElements());
            list.setData(articalPage.getContent());
        }
        return list;
    }

    /**
     * 功能比较薄弱，不推荐使用
    */
    private Page<Artical> exampleFindAll(Artical artical, PageRequest pageRequest){
        //劣势：参考：https://blog.csdn.net/wangchengming1/article/details/90639728
        //1.不支持组合查询，比如：firstname = ?0 or (firstname = ?1 and lastname = ?2).
        //2.只支持字符串的starts/contains/ends/regex匹配，对于非字符串的属性，只支持精确匹配。换句话说，并不支持大于、小于、between等匹配。
        ExampleMatcher exampleMatcher;
        //CONTAINING: 字符串类型模糊匹配，不走索引的，尽量不要使用
        //STARTING: 以字符串开头
        //exampleMatcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.STARTING);
        exampleMatcher = ExampleMatcher.matching()
                .withMatcher("title", m -> m.contains())
                .withMatcher("author", m -> m.startsWith());
        return articalRepository.findAll(Example.of(artical, exampleMatcher), pageRequest);
    }

    /**
     * Repository需要实现JpaSpecificationExecutor接口
    */
    private Page<Artical> specificationFindAll(Artical artical, PageRequest pageRequest){
        Specification<Artical> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            //标题模糊匹配
            if (StringUtils.hasText(artical.getTitle())) {
                predicates.add(builder.like(root.get("title"), "%" + artical.getTitle() + "%"));
            }

            //作者前缀匹配
            if (StringUtils.hasText(artical.getAuthor())) {
                predicates.add(builder.like(root.get("author"), artical.getAuthor() + "%"));
            }

            //发布日期精确匹配
            if (!StringUtils.isEmpty(artical.getPublishDate())) {
                predicates.add(builder.equal(root.get("publishDate"), artical.getPublishDate()));
            }

            if (predicates.size() > 1) {
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            } else if (predicates.size() == 1) {
                return predicates.get(0);
            } else {
                return null;
            }
        };

        return articalRepository.findAll(specification, pageRequest);
    }
}
