package muiz.demo.gabac.data.entities;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@Node("User")
public class User {
    @Id
    private Long id;

    private String name;

    private String country;

    @Property("joining_date")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate joiningDate;

    @Relationship(type = "HAS_POSITION")
    private Position position;

    @Relationship(type = "MEMBER_OF")
    private Department department;

    @Relationship(type = "AUTHORIZED")
    private List<Document> documents;
}
