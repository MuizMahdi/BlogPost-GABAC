package muiz.demo.gabac.data.entities;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Data
@Node("Department")
public class Department {
    @Id
    private Long id;

    private String name;

    @Relationship(type = "PUBLISHES")
    private DocumentType publishedDocuments;
}
