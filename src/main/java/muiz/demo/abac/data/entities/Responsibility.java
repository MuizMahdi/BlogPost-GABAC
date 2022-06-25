package muiz.demo.abac.data.entities;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@Node("Responsibility")
public class Responsibility {
    @Id
    private Long Id;
    private String name;
}
