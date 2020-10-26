package com.example.views;

import lombok.Data;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;


/**
 * <h1></h1>
 * Created by hanqf on 2020/10/26 14:02.
 */

@Data
public class CustomSort {

    public static final String DESC = "desc";

    public static final String ASC = "asc";

    /**
     * @Fields sortName : TODO(排序的列名称)
     */
    private String sortName = "id";

    /**
     * @Fields sortType : TODO(排序类型：升序、降序)
     */
    private String sortType = "desc";

    public Sort.Order getOrder() {
        if (!StringUtils.hasText(sortName)) {
            sortName = "id";
        }
        if (ASC.equalsIgnoreCase(sortType)) {
            return new Sort.Order(Sort.Direction.ASC,sortName);
        } else {
            return new Sort.Order(Sort.Direction.DESC,sortName);
        }
    }

}
