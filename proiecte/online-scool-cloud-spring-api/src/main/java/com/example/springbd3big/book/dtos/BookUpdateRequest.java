package com.example.springbd3big.book.dtos;

public record BookUpdateRequest(
        String bookName,
        Long studentId
){}
