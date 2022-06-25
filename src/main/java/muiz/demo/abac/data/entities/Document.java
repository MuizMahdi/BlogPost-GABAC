package muiz.demo.abac.data.entities;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Node("Document")
public class Document {
    @Id
    private Long Id;
    private String title;
    private String type;
    @Property("creation_date")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate creationDate;
}
