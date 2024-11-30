package main.accountapi.model.dto;

// 회원가입 요청
public record RegisterRequest(String ids, String password, String name, String email) {}

