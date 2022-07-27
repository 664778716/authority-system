package com.nm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nm.config.vo.query.DepartmentQueryVo;
import com.nm.dao.UserMapper;
import com.nm.entity.Department;
import com.nm.dao.DepartmentMapper;
import com.nm.entity.User;
import com.nm.service.DepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nm.utils.DepartmentTree;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author nm
 * @since 2022-07-06
 */
@Service
@Transactional
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<Department> findDepartList(DepartmentQueryVo departmentQueryVo) {
        // 创建条件构造器对象
        QueryWrapper<Department> queryWrapper=new QueryWrapper<>();
        // 部门名称
        queryWrapper.eq(!ObjectUtils.isEmpty(departmentQueryVo.getDepartmentName()),"department_name",departmentQueryVo.getDepartmentName());
        // 排序
        queryWrapper.orderByAsc("order_num");
        // 查询部门列表
        List<Department> departmentList = baseMapper.selectList(queryWrapper);
        // 生成部门树
        List<Department> departmentTree = DepartmentTree.makeDepartmentTree(departmentList, 0L);
        return departmentTree;
    }

    @Override
    public List<Department> findParentDepartment() {
        // 创建条件构造器对象
        QueryWrapper<Department> queryWrapper=new QueryWrapper<>();
        // 排序
        queryWrapper.orderByAsc("order_num");
        // 查询部门列表
        List<Department> departmentList = baseMapper.selectList(queryWrapper);
        // 创建部门对象
        Department department=new Department();
        department.setId(0L);
        department.setDepartmentName("顶级部门");
        department.setPid(-1L);
        departmentList.add(department);
        // 生成部门树列表
        List<Department> departmentTree = DepartmentTree.makeDepartmentTree(departmentList, -1L);
        // 返回数据
        return departmentTree;
    }

    @Override
    public boolean hasChildrenOfDepartment(Long id) {
        QueryWrapper<Department> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("pid",id);
        // 如果数量大于0表示存在
        if(baseMapper.selectCount(queryWrapper)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean hasUserOfDepartment(Long id) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<User>();
        queryWrapper.eq("department_id",id);
        // 如果数量大于0表示存在
        if(userMapper.selectCount(queryWrapper)>0){
            return true;
        }
        return false;
    }
}
