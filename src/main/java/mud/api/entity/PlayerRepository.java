package mud.api.entity;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;


@EnableJpaRepositories
public interface PlayerRepository extends CrudRepository<Player, Integer> {
    Player save(Player player);
    List<Player> findAll();

    Player findByNickname(String nickName);
    Player findByNicknameAndPassword(String nickName, String password);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO player (nickname, password, exp, money) VALUES (?1, ?2, 0, 0)", nativeQuery = true)
    Integer newPlayer(String nickName, String password);
}
