package muiz.demo.abac.data.entities;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@Node("Position")
public class Position {
    @Id
    private Long id;

    private String name;
}
