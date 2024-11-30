package main.accountapi.model.dto;

import main.accountapi.model.UserStatus;

//회원가입 응답
public record AccountResponse(long id, String ids, String email, UserStatus status) {}

