package main.accountapi.model.dto;

import main.accountapi.model.UserStatus;

//회원 응답
public record AccountResponse(long id, String ids, String name, String email, UserStatus status) {}

