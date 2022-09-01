package muiz.demo.gabac.core;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.Neo4jException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * Executes cypher queries generated by the query generator and responds with whether access is allowed or not for the current request.
 */
@Component
public class PolicyEvaluator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyEvaluator.class.getName());

    private final Driver driver;

    @Autowired
    public PolicyEvaluator(Driver driver) {
        this.driver = driver;
    }

    /**
     * Checks whether access is permitted for the request
     * @param request The HTTP request to evaluate
     * @return Whether access is permitted
     */
    public boolean isAllowed(HttpServletRequest request) {
        var queries = new HashMap<Policy.PolicyResource, String>();
        var policies = new PolicyParser().getPolicies();
        var generator = new PolicyQueryGenerator(request);
        String resource = request.getRequestURI().split("/")[1];
        String action = request.getMethod();

        policies.forEach(policy -> queries.put(policy.getResource(), generator.buildQuery(policy.getRules())));

        var currentRequestQuery = queries.keySet().stream().filter(policyResource ->
            resource.equals(policyResource.getType().toLowerCase()) && action.equals(policyResource.getAction())
        ).findFirst();

        if (currentRequestQuery.isPresent()) {
            String query = queries.get(currentRequestQuery.get());
            try (Session session = driver.session()) {
                Record record = session.readTransaction(tx -> tx.run(query).single());
                var result = record.asMap().entrySet().stream().findFirst();
                boolean hasMatch = result.isPresent() && (Boolean) result.get().getValue();
                boolean isPermitted = currentRequestQuery.get().isPermitted();
                return hasMatch == isPermitted;
            } catch (Neo4jException ex) {
                LOGGER.error(ex.getMessage());
            }
        }

        return false;
    }
}
