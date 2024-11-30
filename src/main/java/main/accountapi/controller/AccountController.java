package main.accountapi.controller;

import main.accountapi.model.UserStatus;
import main.accountapi.model.dto.AccountResponse;
import main.accountapi.model.dto.LoginRequest;
import main.accountapi.model.dto.RegisterRequest;
import main.accountapi.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<AccountResponse> register(@RequestBody RegisterRequest request) {
        AccountResponse response = accountService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<AccountResponse> login(@RequestBody LoginRequest request) {
        try {
            // 로그인 요청을 처리하고 성공적인 응답을 반환
            AccountResponse response = accountService.login(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 로그인 실패시 예외 처리 (잘못된 아이디 또는 비밀번호)
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    // 모든 계정 조회
    @GetMapping("/all")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // 아이디로 계정 조회
    @GetMapping("/{ids}")
    public ResponseEntity<AccountResponse> getAccountByIds(@PathVariable String ids) {
        try {
            AccountResponse account = accountService.getAccountByIds(ids);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 회원 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable long id, @RequestBody RegisterRequest request) {
        try {
            AccountResponse updatedAccount = accountService.updateAccount(id, request);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 회원 상태 변경
    @PutMapping("/{id}/status")
    public ResponseEntity<AccountResponse> updateStatus(@PathVariable long id, @RequestParam String status) {
        try {
            UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
            AccountResponse updatedStatus = accountService.updateStatus(id, userStatus);
            return new ResponseEntity<>(updatedStatus, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // 회원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable long id) {
        try {
            accountService.deleteAccount(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
