package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2021/9/6 14:38.
 */

@RestController
public class DemoController {

    @Autowired
    CountryJpaRepository countryJpaRepository;

    @Autowired
    DemoEntityJpaRepository demoEntityJpaRepository;

    @GetMapping("/")
    public Map index() {
        Map<String, Object> map = new HashMap<>();
        final BigInteger size = countryJpaRepository.findBySqlFirst("SELECT count(*) as count FROM ad_country", BigInteger.class, true);
        map.put("size", size);
        //分页
        Pageable pageable = PageRequest.of(1, 20, Sort.by(Sort.Direction.DESC, "id").and(Sort.by(Sort.Direction.ASC, "name_zh")));
        final Page<Country> pages = countryJpaRepository.findPageBySql("SELECT id,name_zh as nameZh ,name_en as nameEn FROM ad_country", pageable, Country.class, false);

        map.put("pages", pages);



        //批量新增
        List<DemoEntity> entityList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            DemoEntity demoEntity = new DemoEntity();
            demoEntity.setName("测试" + i);
            demoEntity.initInsert();
            entityList.add(demoEntity);
        }
        demoEntityJpaRepository.batchSave(entityList);

        final Page<DemoEntity> demoEntities = demoEntityJpaRepository.findAll(PageRequest.of(1, 100, Sort.by(Sort.Direction.DESC, "name")));

        //批量删除
        demoEntityJpaRepository.deleteAllInBatch(demoEntities.getContent());

        return map;
    }

    @GetMapping("/query")
    public Map query() {

        final Country country = countryJpaRepository.findCountrySqlByName("中国");
        final Country country2 = countryJpaRepository.findCountryHqlByName("中国");
        final CountryDto country3 = countryJpaRepository.findCountryDtoNewHqlByName("中国");

        final CountryDto countryDtoSql = countryJpaRepository.findCountryDtoSqlByName("中国");
        final CountryDto countryDtoHql = countryJpaRepository.findCountryDtoHqlByName("中国");

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));

        final Page<Country> countryPage = countryJpaRepository.findByIdAfter(45L, pageable);
        final Page<CountryDto> countryPojoPage = countryJpaRepository.findByIdAfterDto(45L, pageable);

        Map<String, Object> map = new HashMap<>();

        return map;
    }

    @GetMapping("/query2")
    public Map query2() {

        final List<Country> countries = countryJpaRepository.nameHqlFindAll();
        final List<Country> countries1 = countryJpaRepository.nameSqlFindAll();
        final List<Country> countries2 = countryJpaRepository.nameSqlFindAll2();
        final List<Country> countries3 = countryJpaRepository.nameSqlFindSomeField();

        List<Country> list = countryJpaRepository.getEntityManager().createNamedQuery("SQL_FIND_ALL_COUNTRY").getResultList();

        List<Country> list2 = countryJpaRepository.getEntityManager().createNativeQuery("select id,name_zh as nameZh,name_en as nameEn from tbl_country","SQL_RESULT_COUNTRY_SOME_FIELD").getResultList();


        Map<String, Object> map = new HashMap<>();

        return map;
    }
}
