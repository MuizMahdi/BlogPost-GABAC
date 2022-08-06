package muiz.demo.abac.configuration;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.Neo4jException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Configuration
public class DatabaseConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class.getName());

    private final Driver driver;

    @Autowired
    public DatabaseConfig(Driver driver) {
        this.driver = driver;
    }

    @Bean
    public CommandLineRunner dataLoader() {
        return (args) -> {
            String query = getCypherQuery();
            try (Session session = driver.session()) {
                session.writeTransaction(tx -> tx.run(query));
            } catch (Neo4jException ex) {
                LOGGER.error(ex.getMessage());
                throw ex;
            }
        };
    }

    private String getCypherQuery() throws IOException {
        File cypherFile = ResourceUtils.getFile("classpath:data/data.cypher");
        return new String(Files.readAllBytes(cypherFile.toPath()));
    }
}
