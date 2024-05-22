package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.Country;
import softuni.exam.models.entity.Volcano;

import java.util.Optional;
import java.util.Set;

@Repository
public interface VolcanoRepository extends JpaRepository<Volcano, Long> {
    Optional<Volcano> findByName(String name);
    @Query(value = "SELECT * FROM volcanoes WHERE elevation > 3000" +
            " AND last_eruption IS NOT NULL AND is_active = 1 ORDER BY elevation DESC", nativeQuery = true)
    Set<Volcano> findByElevationGreaterThanAndActiveIsTrueAndLastEruptionIsNotNullOrderByElevationDesc();

}
