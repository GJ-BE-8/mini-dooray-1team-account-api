package main.accountapi.model;


import main.accountapi.model.UserStatus;
import main.accountapi.model.entity.Account;
import main.accountapi.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AccountTest {

    @Autowired
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    public void setUp() {
        // Account 엔티티 생성
        account = new Account();
        account.setIds("testuser");
        account.setPassword("testpassword");
        account.setName("Test User");
        account.setEmail("testuser@example.com");
        account.setStatus(UserStatus.ACTIVE); // 예시로 ACTIVE 상태 설정
    }

    // Account 저장 및 조회 테스트
    @Test
    public void whenAccountIsSaved_thenItShouldBePersisted() {
        // 계정 저장
        Account savedAccount = accountRepository.save(account);

        // 저장된 계정이 null이 아니고, 필드가 올바르게 저장되었는지 확인
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount.getIds()).isEqualTo("testuser");
        assertThat(savedAccount.getPassword()).isEqualTo("testpassword");
        assertThat(savedAccount.getName()).isEqualTo("Test User");
        assertThat(savedAccount.getEmail()).isEqualTo("testuser@example.com");
        assertThat(savedAccount.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    // Account 조회 테스트
    @Test
    public void whenAccountIsRetrievedById_thenItShouldReturnCorrectAccount() {
        // 계정 저장
        accountRepository.save(account);

        // 저장된 계정을 ID로 조회
        Account foundAccount = accountRepository.findById(account.getId()).orElse(null);

        // 조회된 계정이 null이 아니고, 필드가 올바르게 조회되었는지 확인
        assertThat(foundAccount).isNotNull();
        assertThat(foundAccount.getIds()).isEqualTo("testuser");
        assertThat(foundAccount.getPassword()).isEqualTo("testpassword");
        assertThat(foundAccount.getName()).isEqualTo("Test User");
        assertThat(foundAccount.getEmail()).isEqualTo("testuser@example.com");
        assertThat(foundAccount.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    // UserStatus Enum 값 저장 테스트
    @Test
    public void whenAccountIsSavedWithStatus_thenStatusShouldBePersisted() {
        // 계정 상태를 'INACTIVE'로 설정
        account.setStatus(UserStatus.INACTIVE);

        // 계정 저장
        Account savedAccount = accountRepository.save(account);

        // 저장된 계정 상태가 'INACTIVE'인지 확인
        assertThat(savedAccount.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }
}

