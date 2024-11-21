package mud.api.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MonsterRepository extends CrudRepository<Monster, Integer> {
    List<Monster> findAll();
}
