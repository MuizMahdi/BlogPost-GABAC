package muiz.demo.abac.data.entities;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@Node("Department")
public class Department {
    @Id
    private Long Id;
    private String name;
}
