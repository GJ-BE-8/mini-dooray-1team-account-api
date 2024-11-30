package main.accountapi.service;
import main.accountapi.model.dto.AccountResponse;
import main.accountapi.model.dto.LoginRequest;
import main.accountapi.model.dto.RegisterRequest;
import main.accountapi.model.UserStatus;

import java.util.List;

public interface AccountService {

    // 회원가입
    AccountResponse register(RegisterRequest request);

    // 로그인
    AccountResponse login(LoginRequest request);

    // 모든 멤버들 불러오기
    List<AccountResponse> getAllAccounts();

    // 아이디로 유저정보 조회
    AccountResponse getAccountByIds(String ids);

    // 회원정보 수정
    AccountResponse updateAccount(long id, RegisterRequest request);

    // 회원상태 변경
    AccountResponse updateStatus(long id, UserStatus status);

    // 회원 삭제
    void deleteAccount(long id);
}
