package main.accountapi.service;

import main.accountapi.model.UserStatus;
import main.accountapi.model.dto.AccountResponse;
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
        when(accountRepository.existsAccountByIds(registerRequest.ids())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponse response = accountService.register(registerRequest);

        assertNotNull(response);
        assertEquals("testuser", response.ids());
        assertEquals("test@example.com", response.email());
        assertEquals(UserStatus.ACTIVE, response.status());
    }

    @Test
    public void login_ShouldReturnAccountResponse_WhenValidCredentials() {
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
        when(accountRepository.findAccountByIds(loginRequest.ids())).thenReturn(account);
        when(passwordEncoder.matches(loginRequest.password(), account.getPassword())).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.login(loginRequest);
        });

        assertEquals("아이디 또는 비밀번호가 잘못되었습니다.", exception.getMessage());
    }

    @Test
    public void updateAccount_ShouldUpdateAccountDetails() {
        when(accountRepository.findById(account.getId())).thenReturn(java.util.Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        RegisterRequest updatedRequest = new RegisterRequest("testuser1", "newPassword", "삼사오", "newEmail@example.com");

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
    }

    @Test
    public void deleteAccount_ShouldDeleteAccount() {
        when(accountRepository.findById(account.getId())).thenReturn(java.util.Optional.of(account));
        doNothing().when(accountRepository).delete(account);

        accountService.deleteAccount(account.getId());

        verify(accountRepository, times(1)).delete(account);
    }
}

//    @Test
//    void testSaveAccount() {
//        Account account = new Account();
//        account.setIds("user1");
//        account.setPassword("password123");
//        account.setEmail("user1@example.com");
//
//        AccountResponse savedAccount = accountService.register(new RegisterRequest(account.getIds(), account.getPassword(), account.getEmail()));
//
//        assertNotNull(savedAccount.id()); // ID가 자동으로 생성되었는지 확인
//    }
