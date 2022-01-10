package cn.henuer.yygh.hosp.service.impl;

import cn.henuer.model.hosp.Department;
import cn.henuer.vo.hosp.DepartmentQueryVo;
import cn.henuer.yygh.hosp.repository.DepartmentRepository;
import cn.henuer.yygh.hosp.service.DepartmentService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    //上传科室接口
    @Override
    public void save(Map<String, Object> paramMap) {
        //parmaMap转换成Department对象
        String parmaMapObject = JSONObject.toJSONString(paramMap);
        Department department= JSONObject.parseObject(parmaMapObject,Department.class);
        //通过医院编号和科室编号查询科室信息
        Department departmentExist=departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());
        //判断科室时候存在
        if (departmentExist!=null){
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        }else{
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //创建Pageable对象，设置当前页和每页记录数
        //0是第一页
       Pageable  pageable= PageRequest.of(page-1,limit);

       //创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        Department department=new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted(0);

        Example<Department> example = Example.of(department, matcher);
        Page<Department> departments =departmentRepository.findAll(example,pageable);
        return departments;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        //先查询判断有没有这个科室
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            departmentRepository.deleteById(department.getId());
        }

    }
}