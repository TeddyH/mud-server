package mud.api.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NpcRepository extends CrudRepository<Npc, Integer> {
    List<Npc> findAll();
}
