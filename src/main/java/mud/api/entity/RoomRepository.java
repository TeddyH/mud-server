package mud.api.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoomRepository extends CrudRepository<Room, Integer> {
    List<Room> findAll();
    Room findById(String id);
}
