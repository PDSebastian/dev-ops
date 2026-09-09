package com.example.springbd3big.book.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookCreateRequest(
        @NotBlank
        @Size(min = 3, max = 50, message = "size 3-50")
        String bookName,
        Long studentId
){}
