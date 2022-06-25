package muiz.demo.abac.data.repositories;

import muiz.demo.abac.data.entities.Responsibility;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsibilityRepository extends Neo4jRepository<Responsibility, Long> {
}
