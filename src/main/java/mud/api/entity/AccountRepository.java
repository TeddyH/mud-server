package mud.api.entity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;


public interface AccountRepository extends CrudRepository<Account, Integer> {
    List<Account> findAll();

    Account findByUserid(String userid);

    Account findByUseridAndPassword(String userid, String password);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO account(userid, password) VALUES(?1, ?2)", nativeQuery = true)
    Integer createAccount(String userid, String password);
}
