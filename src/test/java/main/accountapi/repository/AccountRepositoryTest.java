package main.accountapi.repository;

import main.accountapi.model.entity.Account;
import main.accountapi.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    public void setUp() {
        // 테스트용 Account 객체 생성
        account = new Account();
        account.setIds("testuser");
        account.setPassword("password123");
        account.setName("Test User");
        account.setEmail("testuser@example.com");
        account.setStatus(UserStatus.ACTIVE);

        // 저장
        accountRepository.save(account);
    }

    @Test
    public void testExistsAccountByIds() {
        String ids = "testuser";

        boolean exists = accountRepository.existsAccountByIds(ids);

        assertThat(exists).isTrue(); // "testuser" 아이디가 존재하는지 확인
    }

    @Test
    public void testFindAccountByIds() {
        String ids = "testuser";

        Account foundAccount = accountRepository.findAccountByIds(ids);

        assertThat(foundAccount).isNotNull();
        assertThat(foundAccount.getIds()).isEqualTo(ids); // "testuser" 아이디로 조회된 계정이 맞는지 확인
    }

    @Test
    public void testExistsAccountByIdsWhenAccountDoesNotExist() {
        String ids = "nonexistentuser";

        boolean exists = accountRepository.existsAccountByIds(ids);

        assertThat(exists).isFalse(); // "nonexistentuser" 아이디는 존재하지 않음
    }

    @Test
    public void testFindAccountByIdsWhenAccountDoesNotExist() {

        String ids = "nonexistentuser";

        Account foundAccount = accountRepository.findAccountByIds(ids);

        assertThat(foundAccount).isNull(); // "nonexistentuser" 아이디로 조회된 계정이 없으면 null
    }
}
