package muiz.demo.abac.data.repositories;

import muiz.demo.abac.data.entities.Position;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends Neo4jRepository<Position, Long> {
}
