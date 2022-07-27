package com.nm.utils;

import com.nm.entity.Department;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 生成部门树列表
 * @return
 */
public class DepartmentTree {
    public static List<Department> makeDepartmentTree(List<Department> departmentList,Long pid){
        // 创建集合保存部门信息
        List<Department> list=new ArrayList<>();
        // 判断部门列表是否为空,如果不为空则使用部门列表,否则创建集合对象
        Optional.ofNullable(departmentList).orElse(new ArrayList<>())
                .stream().filter(item -> item!=null && item.getPid() == pid)
                .forEach(item ->{
                    // 创建部门对象
                    Department department=new Department();
                    // 复制属性
                    BeanUtils.copyProperties(item,department);
                    // 获取到每一个item的下级部门,通过递归生成部门数
                    List<Department> children = makeDepartmentTree(departmentList, item.getId());
                    // 设置子部门
                    department.setChildren(children);
                    // 将部门添加到集合中
                    list.add(department);
                });
        //返回数据
        return list;

    }
}
