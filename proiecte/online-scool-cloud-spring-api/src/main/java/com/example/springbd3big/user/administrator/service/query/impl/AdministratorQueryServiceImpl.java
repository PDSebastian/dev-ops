package com.example.springbd3big.user.administrator.service.query.impl;

import com.example.springbd3big.config.exceptions.InvalidRequestException;
import com.example.springbd3big.user.administrator.dtos.AdministratorResponse;
import com.example.springbd3big.user.administrator.exceptions.AdministratorNotFoundException;
import com.example.springbd3big.user.administrator.mappers.AdministratorMapper;
import com.example.springbd3big.user.administrator.repository.AdministratorRepository;
import com.example.springbd3big.user.administrator.service.query.AdministratorQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AdministratorQueryServiceImpl implements AdministratorQueryService {

    private final AdministratorRepository administratorRepository;
    private final AdministratorMapper administratorMapper;

    public AdministratorQueryServiceImpl(
            AdministratorRepository administratorRepository,
            AdministratorMapper administratorMapper
    ) {
        this.administratorRepository = administratorRepository;
        this.administratorMapper = administratorMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdministratorResponse> getAllAdministrators() {
        return administratorRepository.findAll().stream()
                .map(administratorMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdministratorResponse getAdministratorById(Long administratorId) {
        return administratorRepository.findById(administratorId)
                .map(administratorMapper::toDto)
                .orElseThrow(() -> new AdministratorNotFoundException(administratorId));
    }

    @Override
    @Transactional(readOnly = true)
    public AdministratorResponse findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidRequestException("email must not be blank");
        }

        return administratorRepository.findByEmailIgnoreCase(email)
                .map(administratorMapper::toDto)
                .orElseThrow(() -> new AdministratorNotFoundException(email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdministratorResponse> searchAdministrators(String query) {
        String normalized = query == null ? "" : query.trim();
        if (normalized.isEmpty()) {
            return List.of();
        }

        return administratorRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        normalized,
                        normalized,
                        normalized
                )
                .stream()
                .map(administratorMapper::toDto)
                .toList();
    }
}
