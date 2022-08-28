package muiz.demo.gabac.data.entities;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Node("Document")
public class Document {
    @Id
    private Long id;

    private String title;

    @Property("creation_date")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate creationDate;

    @Relationship(type = "HAS_TYPE")
    private DocumentType type;
}
