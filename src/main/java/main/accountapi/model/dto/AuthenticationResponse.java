package main.accountapi.model.dto;

import main.accountapi.model.UserStatus;

public record AuthenticationResponse(String ids, String password, String name, String email) {
}