package com.eldercare.common.core.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装（不依赖 MyBatis-Plus，由服务层自行填充）。
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 当前页码 */
    private long current;

    /** 每页大小 */
    private long size;

    /** 数据列表 */
    private List<T> records;

    public static <T> PageResult<T> of(List<T> records, long total, long current, long size) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setCurrent(current);
        result.setSize(size);
        return result;
    }
}
