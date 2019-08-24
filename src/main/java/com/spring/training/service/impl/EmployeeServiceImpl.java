package com.spring.training.service.impl;

import com.spring.training.dto.EmployeeDetailDto;
import com.spring.training.dto.EmployeeSummaryDto;
import com.spring.training.model.Employee;
import com.spring.training.repository.EmployeeRepository;
import com.spring.training.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<EmployeeSummaryDto> getEmployees() {
        return entitiesToDtos(repository.findAll());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public EmployeeDetailDto save(EmployeeDetailDto employee) {
        //Much beautiful way:
        //return modelMapper.map(repository.save(modelMapper.map(employee, Employee.class)), EmployeeDetailDto.class);

        //convert given DTO to Entity
        Employee toSave = modelMapper.map(employee, Employee.class);

        //After save, the EmployeeRepository gives us the saved entity and we need to convert back to DTO
        Employee resp = repository.save(toSave);
        EmployeeDetailDto response = modelMapper.map(resp, EmployeeDetailDto.class);

        return response;
    }

    /**NOT FINISHED -> ADD ALL FIELDS AND FIX NULL POINTERS !!!*/
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<EmployeeDetailDto> update(EmployeeDetailDto employee) {
        Optional<Employee> existingEmployee = repository.findById(employee.getId());

        if(!existingEmployee.isPresent()){
            return Optional.empty();
        }else {
            if(!existingEmployee.get().getFirstName().equalsIgnoreCase(employee.getFirstName()))
                existingEmployee.get().setFirstName(employee.getFirstName());
            if(!existingEmployee.get().getLastName().equalsIgnoreCase(employee.getLastName()))
                existingEmployee.get().setLastName(employee.getLastName());
            if(!existingEmployee.get().getPhone().equalsIgnoreCase(employee.getPhone()))
                existingEmployee.get().setPhone(employee.getPhone());
        }

        return Optional.of(modelMapper.map(existingEmployee.get(), EmployeeDetailDto.class));
    }

    @Override
    public Optional<EmployeeDetailDto> delete(Integer id) {
        Optional<Employee> employeeToDelete = repository.findById(id);

        if (employeeToDelete.isPresent()) {
            repository.delete(employeeToDelete.get());
            return Optional.of(modelMapper.map(employeeToDelete.get(), EmployeeDetailDto.class));
        } else return Optional.empty();
    }

    @Override
    public Optional<EmployeeDetailDto> findById(Integer id) {
        return Optional.ofNullable(modelMapper.map(repository.findById(id), EmployeeDetailDto.class));
    }

    //This method convert a list of Employee Entity to a list of EmployeeSummary
    private List<EmployeeSummaryDto> entitiesToDtos(List<Employee> input) {
        List<EmployeeSummaryDto> output = new ArrayList<>();
        input.forEach(employee -> output.add(modelMapper.map(employee, EmployeeSummaryDto.class)));
        return output;
    }
}
