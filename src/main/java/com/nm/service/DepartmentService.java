package com.nm.service;

import com.nm.config.vo.query.DepartmentQueryVo;
import com.nm.entity.Department;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author nm
 * @since 2022-07-06
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 查询部门列表
     * @param departmentQueryVo
     * @return
     */
    List<Department> findDepartList(DepartmentQueryVo departmentQueryVo);

    /**
     * 查询上级部门列表
     * @return
     */
    List<Department> findParentDepartment();

    /**
     * 判断部门下是否有子部门
     * @param id
     * @return
     */
    boolean hasChildrenOfDepartment(Long id);

    /**
     * 判断部门下是否有用户
     * @param id
     * @return
     */
    boolean hasUserOfDepartment(Long id);
}
