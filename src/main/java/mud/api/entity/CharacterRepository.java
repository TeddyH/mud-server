package mud.api.entity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface CharacterRepository extends CrudRepository<Character, Integer> {
    Character save(Character character);

    Character findByName(String name);

    List<Character> findAll();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO `character` (name, job, account, exp, money, head, body, pants, boots, weapon, shield)" +
            " VALUES(?1, ?2, ?3, 0, 0, 0, 0, 0, 0, 0, 0)", nativeQuery = true)
    Integer createCharacter(String name, String job, String account);

}
