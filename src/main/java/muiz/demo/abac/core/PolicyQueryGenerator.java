package muiz.demo.abac.core;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Constructs cypher queries from Policies
 */
@NoArgsConstructor
public class PolicyQueryGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyQueryGenerator.class.getName());

    /**
     * Operators used for comparison that are used to build the conditions in the query
     */
    private static final List<Character> COMPARISON_OPERATORS = Arrays.asList('<', '>');

    /**
     * Indicates a variable that would be obtained from the request's path variables
     */
    private static final String PATH_VARIABLE_OPERATOR = "/";

    /**
     * Indicates a predefined variable that would be obtained from the user principal
     */
    private static final String PRINCIPAL_VARIABLE_OPERATOR = "$";

    public HashMap<Policy.PolicyResource, String> getQueries() {
        HashMap<Policy.PolicyResource, String> policiesQueries = new HashMap<>();

        try {
            PolicyParser parser = new PolicyParser(getAccessPolicies());
            parser.getPolicies().forEach(policy -> policiesQueries.put(policy.getResource(), buildQuery(policy.getRules())));
        }
        catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }

        return policiesQueries;
    }

    private String getAccessPolicies() throws IOException {
        File policiesFile = ResourceUtils.getFile("classpath:data/access_policies.json");
        return new String(Files.readAllBytes(policiesFile.toPath()));
    }

    private String buildQuery(Set<Policy.PolicyRule> rules) {
        Map<Character, String[]> comparisonProperties = new HashMap<>();
        StringBuilder query = new StringBuilder();
        query.append("MATCH ");

        rules.forEach(rule -> buildNode(rule, comparisonProperties, query));
        buildConditions(comparisonProperties, query);
        buildExternalVariables(query);

        query.append(String.format(" RETURN count(%s) > 0", rules.stream().toList().get(0).getType()));

        return query.toString();
    }

    private void buildNode(Policy.PolicyRule rule, Map<Character, String[]> comparisonProperties, StringBuilder query) {
        String node = rule.getType();
        String relation = rule.getProperties().get("relation");
        Map<String, String> properties = rule.getProperties().entrySet().stream()
        .filter(entry -> !entry.getKey().equals("relation"))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        StringBuilder nodeProperties = new StringBuilder();
        buildProperties(node, properties, nodeProperties, comparisonProperties);

        // Add relation
        if (relation != null && !relation.isEmpty()) {
            query.append(String.format("-[:%s]-", relation.replace(" ", "_").toUpperCase()));
        } else if (query.length() > 6) {
            query.append("--");
        }

        // Add node
        query.append("(").append(node).append(nodeProperties).append(")");
    }

    private void buildProperties(String node, Map<String, String> properties, StringBuilder nodeProperties, Map<Character, String[]> comparisonProperties) {
        if (properties.size() > 0) {
            nodeProperties.append("{");
            for (var property : properties.entrySet()) {
                String value = property.getValue();
                String key = property.getKey();
                Character comparisonOperator = value.charAt(0);
                if (COMPARISON_OPERATORS.contains(comparisonOperator)) {
                    comparisonProperties.put(comparisonOperator, new String[]{node, key, value.substring(1)});
                } else {
                    nodeProperties.append(key).append(String.format(": '%s',", value));
                }
            }
            nodeProperties.deleteCharAt(nodeProperties.length()-1);
            nodeProperties.append("}");
        }
    }

    private void buildConditions(Map<Character, String[]> comparisonProperties, StringBuilder query) {
        if (comparisonProperties.size() > 0) {
            query.append(" WHERE ");
            int i = 0;
            for (var property : comparisonProperties.entrySet()) {
                query.append(property.getValue()[0])
                        .append(".")
                        .append(property.getValue()[1])
                        .append(property.getKey())
                        .append(property.getValue()[2]);
                if (i++ != comparisonProperties.size()-1) query.append(" && ");
            }
        }
    }

    private void buildExternalVariables(StringBuilder query) {

    }

}
