package com.chapter5.mapper;

import com.chapter5.entity.EmployeeTask;

public interface EmployeeTaskMapper {
    public EmployeeTask getEmployeeTaskByEmpId(Long empId);
}
