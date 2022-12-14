package muiz.demo.gabac.data.entities;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@Node("DocumentType")
public class DocumentType {
    @Id
    private Long id;

    private String name;
}
