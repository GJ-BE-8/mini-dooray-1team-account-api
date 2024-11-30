package main.accountapi.repository;

import main.accountapi.model.dto.AccountResponse;
import main.accountapi.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsAccountByIds(String ids);
    Account findAccountByIds(String ids);
}
