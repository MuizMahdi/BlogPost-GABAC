package muiz.demo.gabac.data.repositories;

import muiz.demo.gabac.data.entities.Document;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends Neo4jRepository<Document, Long> {
    @Query("MATCH (doc:Document {id:$documentId})-[:HAS_TYPE]-(type:DocumentType)-[:PUBLISHES]-(dept:Department)-[:MEMBER_OF]-(user:User) WHERE user.name = $username RETURN count(user) > 0")
    boolean isPublishedByUserDepartment(@Param("documentId") Long documentId, @Param("username") String username);
}
