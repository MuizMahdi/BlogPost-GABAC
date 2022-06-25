package muiz.demo.abac.data.repositories;

import muiz.demo.abac.data.entities.Document;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends Neo4jRepository<Document, Long> {
}
