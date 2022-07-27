package com.nm.config.vo.query;

import com.nm.entity.User;
import lombok.Data;

@Data
public class UserQueryVo extends User {
    // 当前页码
    private Long pageNo=1L;
    // 每页显示数量
    private Long pageSize=10L;
}
