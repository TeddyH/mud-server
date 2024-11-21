package mud.api.manager;

import mud.api.entity.Account;
import mud.api.entity.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AccountManager {
    private static final Logger logger = LoggerFactory.getLogger(AccountManager.class);

    @Autowired
    AccountRepository accountRepository;

    private Map<String, Account> accountMap = new HashMap<>();

    @PostConstruct
    public void run() {
        initialize();
    }

    private void initialize() {
        List<Account> accountList = accountRepository.findAll();

        for (Account account : accountList) {
            accountMap.put(account.getUserid(), account);
        }

        logger.info("AccountManager Initialized. Load: " + accountMap.size());
    }

    public Account getAccount(String userid) {
        return accountRepository.findByUserid(userid);
    }

    public Account getAccount(String userid, String password) {
        return accountRepository.findByUseridAndPassword(userid, password);
    }

    public Account createAccount(String userid, String password) {
        Account check = accountRepository.findByUserid(userid);

        if (check == null) {
            accountRepository.createAccount(userid, password);
            Account account = accountRepository.findByUserid(userid);
            accountMap.put(userid, account);

            return account;
        } else {
            // 이미 존재하는 계정일 때 처리
            accountMap.put(check.getUserid(), check);

            return null;
        }
    }
}
