package main.accountapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.accountapi.model.UserStatus;
import main.accountapi.model.dto.AccountResponse;
import main.accountapi.model.dto.LoginRequest;
import main.accountapi.model.dto.RegisterRequest;
import main.accountapi.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Autowired
    private MockMvc mockMvc;

    private AccountResponse accountResponse;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();

        accountResponse = new AccountResponse(1L, "testuser", "testuser", "test@example.com", UserStatus.ACTIVE);
        registerRequest = new RegisterRequest("testuser", "password", "testname", "test@example.com");
        loginRequest = new LoginRequest("testuser", "password");
    }

    // 회원가입 테스트
    @Test
    public void register_ShouldCreateAccount() throws Exception {
        when(accountService.register(registerRequest)).thenReturn(accountResponse);

        mockMvc.perform(post("/accounts/register")
                        .contentType("application/json")
                        .content("{\"ids\":\"testuser\",\"password\":\"password\",\"name\":\"testname\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ids").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    // 로그인 테스트
    @Test
    public void login_ShouldReturnAccountResponse() throws Exception {
        when(accountService.login(loginRequest)).thenReturn(accountResponse);

        mockMvc.perform(post("/accounts/login")
                        .contentType("application/json")
                        .content("{\"ids\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void login_ShouldReturnUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");
        when(accountService.login(loginRequest)).thenThrow(new IllegalArgumentException("잘못된 아이디 또는 비밀번호"));

        mockMvc.perform(post("/accounts/login")
                        .contentType("application/json")
                        //.content("{\"ids\":\"testuser\",\"password\":\"wrongpassword\"}"))
                        .content(jacksonObjectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    // 모든 계정 조회 테스트
    @Test
    public void getAllAccounts_ShouldReturnAccountList() throws Exception {
        when(accountService.getAllAccounts()).thenReturn(Arrays.asList(accountResponse));

        mockMvc.perform(get("/accounts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ids").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    // 아이디로 계정 조회 테스트
    @Test
    public void getAccountByIds_ShouldReturnAccount() throws Exception {
        when(accountService.getAccountByIds("testuser")).thenReturn(accountResponse);

        mockMvc.perform(get("/accounts/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void getAccountByIds_ShouldReturnNotFound() throws Exception {
        when(accountService.getAccountByIds("nonexistent")).thenThrow(new IllegalArgumentException("유저를 찾을 수 없습니다."));

        mockMvc.perform(get("/accounts/nonexistent"))
                .andExpect(status().isNotFound());
    }

    // 회원 정보 수정 테스트
    @Test
    public void updateAccount_ShouldUpdateAccount() throws Exception {
        when(accountService.updateAccount(1L, registerRequest)).thenReturn(accountResponse);

        mockMvc.perform(put("/accounts/1")
                        .contentType("application/json")
                        .content("{\"ids\":\"testuser\",\"password\":\"password\",\"name\":\"testname\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void updateAccount_ShouldReturnNotFound() throws Exception {
        when(accountService.updateAccount(1L, registerRequest)).thenThrow(new IllegalArgumentException("유저를 찾을 수 없습니다."));

        mockMvc.perform(put("/accounts/1")
                        .contentType("application/json")
                        .content("{\"ids\":\"testuser\",\"password\":\"password\",\"name\":\"testname\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isNotFound());
    }

    // 회원 상태 변경 테스트
    @Test
    public void updateStatus_ShouldUpdateStatus() throws Exception {
        when(accountService.updateStatus(1L, UserStatus.ACTIVE)).thenReturn(accountResponse);

        mockMvc.perform(put("/accounts/1/status?status=ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").value("testuser"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    public void updateStatus_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(put("/accounts/1/status?status=INVALID"))
                .andExpect(status().isBadRequest());
    }

    // 회원 삭제 테스트
    @Test
    public void deleteAccount_ShouldDeleteAccount() throws Exception {
        doNothing().when(accountService).deleteAccount(1L);

        mockMvc.perform(delete("/accounts/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteAccount_ShouldReturnNotFound() throws Exception {
        doThrow(new IllegalArgumentException("유저를 찾을 수 없습니다.")).when(accountService).deleteAccount(1L);

        mockMvc.perform(delete("/accounts/1"))
                .andExpect(status().isNotFound());
    }
}
