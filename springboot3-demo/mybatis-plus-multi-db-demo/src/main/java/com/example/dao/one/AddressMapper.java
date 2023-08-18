package com.example.dao.one;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

import com.example.model.one.Address;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author hanqf
* @description 针对表【address】的数据库操作Mapper
* @createDate 2023-08-18 09:46:59
* @Entity com.example.model.one.Address
*/
public interface AddressMapper extends BaseMapper<Address> {

    @ResultMap("BaseResultMap")
    @Select("<script>"
            + "select * "
            + "from address"
            + "<where>"
            + "<if test=\"city != null and city != ''\">"
            + "city = #{city,jdbcType=VARCHAR}"
            + "</if>"
            + "</where>"
            + "</script>")
    List<Address> findAllByCity(@Param("city") String city);

    List<Address> findAllByProvince(@Param("province") String province);
}




