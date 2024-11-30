package main.accountapi.service;

import main.accountapi.model.dto.RegisterRequest;
import main.accountapi.model.entity.Account;
import main.accountapi.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder){
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    public RegisterRequest register(RegisterRequest request){
        if(accountRepository.existsAccountByIds(request.ids())){
            throw new IllegalArgumentException("이미있는 아이디입니다.");
        }

        return null;
    }
}
