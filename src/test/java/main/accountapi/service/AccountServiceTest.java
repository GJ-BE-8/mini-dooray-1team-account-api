package main.accountapi.service;

import main.accountapi.model.UserStatus;
import main.accountapi.model.dto.AccountResponse;
import main.accountapi.model.dto.AuthenticationResponse;
import main.accountapi.model.dto.LoginRequest;
import main.accountapi.model.dto.RegisterRequest;
import main.accountapi.model.entity.Account;
import main.accountapi.repository.AccountRepository;
import main.accountapi.service.Impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        account = new Account();
        account.setId(1L);
        account.setIds("testuser");
        account.setPassword("encodedPassword");
        account.setEmail("test@example.com");
        account.setStatus(UserStatus.ACTIVE);

        registerRequest = new RegisterRequest("testuser", "password123", "1팀", "test@example.com");
        loginRequest = new LoginRequest("testuser", "password123");
    }


    @Test
    public void register_ShouldRegisterNewUser() {
        // 정상적인 회원가입
        when(accountRepository.existsAccountByIds(registerRequest.ids())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponse response = accountService.register(registerRequest);

        assertNotNull(response);
        assertEquals("testuser", response.ids());
        assertEquals("test@example.com", response.email());
        assertEquals(UserStatus.ACTIVE, response.status());

        // 아이디가 이미 존재하는 경우 예외 처리
        when(accountRepository.existsAccountByIds(registerRequest.ids())).thenReturn(true);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.register(registerRequest);
        });

        assertEquals("존재하는 아이디입니다.", exception.getMessage());
    }

    @Test
    public void login_ShouldReturnAccountResponse_WhenValidCredentials() {
        // 로그인 성공
        when(accountRepository.findAccountByIds(loginRequest.ids())).thenReturn(account);
        when(passwordEncoder.matches(loginRequest.password(), account.getPassword())).thenReturn(true);

        AccountResponse response = accountService.login(loginRequest);

        assertNotNull(response);
        assertEquals("testuser", response.ids());
        assertEquals("test@example.com", response.email());
        assertEquals(UserStatus.ACTIVE, response.status());
    }

    @Test
    public void login_ShouldThrowException_WhenInvalidCredentials() {
        // 비밀번호 틀렸을 때
        when(accountRepository.findAccountByIds(loginRequest.ids())).thenReturn(account);
        when(passwordEncoder.matches(loginRequest.password(), account.getPassword())).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.login(loginRequest);
        });

        assertEquals("아이디 또는 비밀번호가 잘못되었습니다.", exception.getMessage());

        // 아이디가 없을 때
        when(accountRepository.findAccountByIds(loginRequest.ids())).thenReturn(null);

        exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.login(loginRequest);
        });

        assertEquals("아이디 또는 비밀번호가 잘못되었습니다.", exception.getMessage());
    }

    @Test
    public void getAllAccounts_ShouldReturnAllAccounts() {
        // 모든 회원 불러오기
        Account account1 = new Account(1L, "user1", "password1","이름", "user1@example.com", UserStatus.ACTIVE);
        Account account2 = new Account(2L, "user2", "password2", "이름2", "user2@example.com", UserStatus.INACTIVE);
        List<Account> accounts = List.of(account1, account2);

        when(accountRepository.findAll()).thenReturn(accounts);

        List<AccountResponse> responses = accountService.getAllAccounts();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("user1", responses.get(0).ids());
        assertEquals("user2", responses.get(1).ids());
    }

    @Test
    public void getAccountByIds_ShouldReturnAccount_WhenAccountExists() {
        // 회원 아이디로 회원불러오기
        Account account = new Account(1L, "testuser", "encodedPassword", "테스트이름", "test@example.com", UserStatus.ACTIVE);
        when(accountRepository.findAccountByIds("testuser")).thenReturn(account);

        AccountResponse response = accountService.getAccountByIds("testuser");

        assertNotNull(response);
        assertEquals("testuser", response.ids());
        assertEquals("test@example.com", response.email());
        assertEquals(UserStatus.ACTIVE, response.status());
    }

    @Test
    public void getAccountByIds_ShouldThrowException_WhenAccountNotFound() {
        // 없는 회원 아이디로 회원불러오기
        when(accountRepository.findAccountByIds("nonexistentuser")).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.getAccountByIds("nonexistentuser");
        });

        assertEquals("찾을 수 없는 아이디입니다.", exception.getMessage());
    }

    @Test
    public void updateAccount_ShouldUpdateAccountDetails() {
        // 정상적으로 회원정보 수정
        when(accountRepository.findById(account.getId())).thenReturn(java.util.Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        RegisterRequest updatedRequest = new RegisterRequest("testuser1", "newPassword", "삼사오", "newEmail@example.com");

        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        AccountResponse response = accountService.updateAccount(account.getId(), updatedRequest);

        assertEquals("testuser1", response.ids());
        assertEquals("newEmail@example.com", response.email());
        assertEquals("삼사오", response.name());
        assertEquals("encodedPassword", account.getPassword());

        // 유저를 찾을 수 없을 때
        when(accountRepository.findById(account.getId())).thenReturn(java.util.Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.updateAccount(account.getId(), updatedRequest);
        });

        assertEquals("유저를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    public void updateAccount_ShouldNotEncryptWhenPasswordIsEmpty() {
        when(accountRepository.findById(account.getId())).thenReturn(java.util.Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        // 비밀번호가 빈 값일 때
        RegisterRequest updatedRequest = new RegisterRequest("testuser1", "", "삼사오", "newEmail@example.com");

        // 암호화 메소드 호출되지 않음
        verify(passwordEncoder, times(0)).encode(anyString());

        AccountResponse response = accountService.updateAccount(account.getId(), updatedRequest);

        assertEquals("testuser1", response.ids());
        assertEquals("newEmail@example.com", response.email());
        assertEquals("삼사오", response.name());
    }

    @Test
    public void updateAccount_ShouldNotEncryptWhenPasswordIsNull() {
        when(accountRepository.findById(account.getId())).thenReturn(java.util.Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        // 비밀번호가 null일 때
        RegisterRequest updatedRequest = new RegisterRequest("testuser1", null, "삼사오", "newEmail@example.com");

        // 암호화 메소드 호출되지 않음
        verify(passwordEncoder, times(0)).encode(anyString());

        AccountResponse response = accountService.updateAccount(account.getId(), updatedRequest);

        assertEquals("testuser1", response.ids());
        assertEquals("newEmail@example.com", response.email());
        assertEquals("삼사오", response.name());
    }


    @Test
    public void updateStatus_ShouldUpdateAccountStatus() {
        when(accountRepository.findById(account.getId())).thenReturn(java.util.Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponse response = accountService.updateStatus(account.getId(), UserStatus.INACTIVE);

        assertEquals(UserStatus.INACTIVE, response.status());

        // 유저를 찾을 수 없을 때
        when(accountRepository.findById(account.getId())).thenReturn(java.util.Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.updateStatus(account.getId(), UserStatus.INACTIVE);
        });

        assertEquals("유저를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    public void deleteAccount_ShouldDeleteAccount() {
        when(accountRepository.findById(account.getId())).thenReturn(java.util.Optional.of(account));
        doNothing().when(accountRepository).delete(account);

        accountService.deleteAccount(account.getId());

        verify(accountRepository, times(1)).delete(account);

        // 유저를 찾을 수 없을 때
        when(accountRepository.findById(account.getId())).thenReturn(java.util.Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.deleteAccount(account.getId());
        });

        assertEquals("유저를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    public void authenticateLoginTest(){
        when(accountRepository.findAccountByIds(account.getIds())).thenReturn(account);

        AuthenticationResponse authenticationResponse = accountService.authenticateLogin(account.getIds());

        assertEquals(authenticationResponse.ids(), account.getIds());
        assertEquals(authenticationResponse.name(), account.getName());
        assertEquals(authenticationResponse.password(), account.getPassword());
        assertEquals(authenticationResponse.email(), account.getEmail());
    }

}
