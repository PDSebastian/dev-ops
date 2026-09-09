package com.example.springbd3big.user.administrator.mappers;

import com.example.springbd3big.user.administrator.dtos.AdministratorResponse;
import com.example.springbd3big.user.administrator.model.Administrator;
import org.springframework.stereotype.Component;

@Component
public class AdministratorMapper {

    public AdministratorResponse toDto(Administrator administrator) {
        return new AdministratorResponse(
                administrator.getId(),
                administrator.getFirstName(),
                administrator.getLastName(),
                administrator.getEmail(),
                administrator.getEmployeeCode(),
                administrator.getDepartment(),
                administrator.getJobTitle()
        );
    }
}
