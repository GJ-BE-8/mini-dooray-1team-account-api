package main.accountapi;

import main.accountapi.controller.AccountController;
import main.accountapi.repository.AccountRepository;
import main.accountapi.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AccountApiApplicationTests {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountController accountController;

    @Test
    void contextLoads() {
        assertNotNull(accountService);
        assertNotNull(accountRepository);
        assertNotNull(accountController);
    }

}
