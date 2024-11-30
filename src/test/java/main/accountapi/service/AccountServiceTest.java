package main.accountapi.service;

import main.accountapi.model.dto.AccountResponse;
import main.accountapi.model.dto.RegisterRequest;
import main.accountapi.model.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountServiceImpl accountService;

    @Test
    void testSaveAccount() {
        Account account = new Account();
        account.setIds("user1");
        account.setPassword("password123");
        account.setEmail("user1@example.com");

        AccountResponse savedAccount = accountService.register(new RegisterRequest(account.getIds(), account.getPassword(), account.getEmail()));

        assertNotNull(savedAccount.id()); // ID가 자동으로 생성되었는지 확인
    }
}

