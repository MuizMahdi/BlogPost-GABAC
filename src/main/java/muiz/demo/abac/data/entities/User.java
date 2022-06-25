package muiz.demo.abac.data.entities;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Node("User")
public class User {
    @Id
    private Long Id;
    private String name;
    private String country;
    @Property("joining_date")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate joiningDate;
}
