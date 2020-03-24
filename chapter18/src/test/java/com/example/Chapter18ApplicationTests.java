package com.example;

import com.example.dao.one.AddressRepository;
import com.example.dao.one.BookRepository;
import com.example.dao.one.UserRepository;
import com.example.dao.two.CityRepository;
import com.example.dao.two.ProvinceRepository;
import com.example.model.one.Address;
import com.example.model.one.Book;
import com.example.model.one.User;
import com.example.model.two.City;
import com.example.model.two.Province;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@SpringBootTest
class Chapter18ApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    @Qualifier("oneMongoTemplate")
    private MongoTemplate oneMongoTemplate;

    @Autowired
    @Qualifier("twoMongoTemplate")
    private MongoTemplate twoMongoTemplate;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private CityRepository cityRepository;


    @Test
    void findCityAllByTemplate(){
        Query query = new Query();
        Criteria criteria = Criteria.where("name").regex("湖北");
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Direction.DESC, "name"));
        List<Province> provinceList = twoMongoTemplate.find(query, Province.class, "province");
        for(Province province:provinceList){
            System.out.println(province);

            Query query2 = new Query();
            Criteria criteria2 = Criteria.where("provinceId").is(province.getId());
            query2.addCriteria(criteria2);
            query2.with(Sort.by(Sort.Direction.DESC, "name"));
            List<City> cityList = twoMongoTemplate.find(query2, City.class, "city");
            cityList.stream().forEach(System.out::println);
        }


    }

    @Test
    void saveProvince(){
        Province province = new Province();
        province.setName("湖北");

        provinceRepository.save(province);

        City city1 = new City();
        city1.setName("武汉");
        city1.setProvinceId(province.getId());
        cityRepository.save(city1);

        City city2 = new City();
        city2.setName("黄冈");
        city2.setProvinceId(province.getId());
        cityRepository.save(city2);

        List<City> cityList = cityRepository.getCitiesByProvinceId(province.getId());
        cityList.stream().forEach(System.out::println);
    }


    @Test
    void insertAllEntity(){
        User user = new User();
        user.setUserName("wangwu");
        user.setAge(10);
        user = userRepository.insert(user);
        System.out.println(user);
        System.out.println("============");
        Address address = new Address();
        address.setCity("shanghai");
        address.setUserId(user.getId());
        address = addressRepository.insert(address);
        System.out.println(address);

        System.out.println("============");
        List<Address> addressList = addressRepository.getAddressesByUserId(user.getId());
        addressList.stream().forEach(System.out::println);
        System.out.println("============");
        Book book = new Book();
        book.setBookName("钢铁是怎样炼成的");
        book.setPrice(12.51);
        book.setTotalPage(120);
        book.setUserId(user.getId());
        book = bookRepository.insert(book);
        System.out.println(book);
        System.out.println("============");
        Book book2 = new Book();
        book2.setBookName("钢铁是怎样炼成的");
        book2.setPrice(12.51);
        book2.setTotalPage(120);
        book2.setUserId(user.getId());
        book2 = bookRepository.insert(book2);
        System.out.println(book2);
        System.out.println("============");
        System.out.println("============");
        List<Book> bookList = bookRepository.getBooksByUserId(user.getId());
        bookList.stream().forEach(System.out::println);


    }

    @Test
    void findUserAllByTemplate(){
        Query query = new Query();
        Criteria criteria = Criteria.where("age").gte(20).lte(30).and("name").regex("zhangsan");
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Direction.DESC, "name"));
        List<User> userList = oneMongoTemplate.find(query, User.class, "user");
        userList.stream().forEach(System.out::println);

    }

    @Test
    void insertUser(){
        User user = new User();
        user.setUserName("lisi");
        user.setAge(10);
        user.setEmail("null");
        user = userRepository.insert(user);
        System.out.println(user);
    }

    @Test
    void findUserAll() {
        List<User> userList = userRepository.findAll();
        userList.stream().forEach(System.out::println);
        //userList.stream().forEach(userRepository::delete);
    }

    @Test
    void findAllByUserNameAndAge(){
        List<User> userList = userRepository.findAllByUserNameAndAge("zhangsan",25);
        userList.stream().forEach(System.out::println);
    }

    @Test
    void findUserByName(){
        List<User> userList = userRepository.findAllByUserName("zhangsan");
        userList.stream().forEach(System.out::println);
    }

    @Test
    void getUserByMyProperties(){
        List<User> userList = userRepository.getUserByMyProperties("zhangsan", 25);
        userList.stream().forEach(System.out::println);
    }

    @Test
    void findUserAllByPage(){

        //查询第一页，按一页三行分页
        Pageable pageable = PageRequest.of(0,3, Sort.by(Sort.Direction.DESC,"name"));
        Page<User> userList = userRepository.findAll(pageable);
        userList.get().forEach(System.out::println);
    }

    @Test
    void getUserByAgeBetween(){
        List<User> userList = userRepository.getUserByAgeBetween(20,30);
        userList.stream().forEach(System.out::println);
    }

    @Test
    void getUserByAgeProperties(){
        List<User> userList = userRepository.getUserByAgeProperties(20,30);
        userList.stream().forEach(System.out::println);
    }


}
