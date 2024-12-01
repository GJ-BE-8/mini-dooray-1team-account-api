package main.accountapi.service.Impl;

import main.accountapi.model.UserStatus;
import main.accountapi.model.dto.AccountResponse;
import main.accountapi.model.dto.AuthenticationResponse;
import main.accountapi.model.dto.LoginRequest;
import main.accountapi.model.dto.RegisterRequest;
import main.accountapi.model.entity.Account;
import main.accountapi.repository.AccountRepository;
import main.accountapi.service.AccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder){
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    @Override
    public AccountResponse register(RegisterRequest request){
        if(accountRepository.existsAccountByIds(request.ids())){
            throw new IllegalArgumentException("존재하는 아이디입니다.");
        }

        //비밀번호 암호화
        String encodePassword = passwordEncoder.encode(request.password());

        // 디티오를 받은 정보를 새로운 회원을 만들어서 저장.
        Account account = new Account();
        account.setIds(request.ids());
        account.setPassword(encodePassword); // 암호화된 비밀번호
        account.setName(request.name());
        account.setEmail(request.email());
        account.setStatus(UserStatus.ACTIVE); // 기본값은 화성으로

        Account savedAccount = accountRepository.save(account);

        // 응답 디티오로 바꿈
        return new AccountResponse(savedAccount.getId(), savedAccount.getIds(), account.getName(), account.getEmail(), account.getStatus());
    }

    // 로그인
    @Override
    public AccountResponse login(LoginRequest request) {
        // 아이디로 유저를 찾기
        Account account = accountRepository.findAccountByIds(request.ids());
        if (account == null) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        // 비밀번호 비교
        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        // 로그인 성공시 AccountResponse 반환
        return new AccountResponse(account.getId(), account.getIds(), account.getName(), account.getEmail(), account.getStatus());
    }

    //모든 멤버들 불러오기
    @Override
    public List<AccountResponse> getAllAccounts(){
        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map(account -> new AccountResponse(account.getId(),account.getIds(), account.getName(), account.getEmail(), account.getStatus()))
                .collect(Collectors.toList());
    }

    // 아이디로 유저정보 조회
    @Override
    public AccountResponse getAccountByIds(String ids){
        Account account = accountRepository.findAccountByIds(ids);
        if(account == null){
            throw new IllegalArgumentException("찾을 수 없는 아이디입니다.");
        }

        return new AccountResponse(account.getId(), account.getIds(), account.getName(), account.getEmail(), account.getStatus());
    }

    // 회원정보 수정
    @Override
    public AccountResponse updateAccount(long id, RegisterRequest request){
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        existingAccount.setIds(request.ids());
        existingAccount.setPassword(request.password());
        existingAccount.setName(request.name());
        existingAccount.setEmail(request.email());

        // 비밀번호 변경 시 암호화
        if (request.password() != null && !request.password().isEmpty()) {
            existingAccount.setPassword(passwordEncoder.encode(request.password()));
        }

        accountRepository.save(existingAccount);
        return new AccountResponse(existingAccount.getId(), existingAccount.getIds(), existingAccount.getName(), existingAccount.getEmail(), existingAccount.getStatus());
    }

    // 회원상태 변경
    @Override
    public AccountResponse updateStatus(long id, UserStatus status) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        account.setStatus(status);
        accountRepository.save(account);
        return new AccountResponse(account.getId(), account.getIds(), account.getName(), account.getEmail(), account.getStatus());
    }

    // 회원 삭제
    @Override
    public void deleteAccount(long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        accountRepository.delete(account);
    }

    @Override
    public AuthenticationResponse authenticateLogin(String ids) {

        Account account = accountRepository.findAccountByIds(ids);
        return new AuthenticationResponse(account.getIds(), account.getPassword(), account.getName(), account.getEmail());

    }

}
