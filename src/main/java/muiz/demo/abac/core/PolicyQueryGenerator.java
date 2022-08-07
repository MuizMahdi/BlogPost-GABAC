package muiz.demo.abac.core;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
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

    private Map<String, String> pathVariables;

    public PolicyQueryGenerator(Map<String, String> pathVariables) {
        this.pathVariables = pathVariables;
    }

    /**
     * Operators used for comparison that are used to build the conditions in the query
     */
    private static final List<Character> COMPARISON_OPERATORS = Arrays.asList('<', '>');

    /**
     * Indicates a variable that would be obtained from the request's path variables
     */
    private static final Character PATH_VARIABLE_OPERATOR = '/';

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
        var comparisonProperties = new HashMap<Character, String[]>();
        List<String> externalProperties = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("MATCH ");

        rules.forEach(rule -> buildNode(rule, comparisonProperties, externalProperties, query));
        buildConditions(comparisonProperties, query);

        query.append(String.format(" RETURN count(%s) > 0", rules.stream().toList().get(0).getType()));

        return buildExternalVariables(externalProperties, query.toString());
    }

    private void buildNode(Policy.PolicyRule rule, Map<Character, String[]> comparisonProperties, List<String> externalProperties, StringBuilder query) {
        String node = rule.getType();
        String relation = rule.getProperties().get("relation");
        var properties = rule.getProperties().entrySet().stream()
        .filter(entry -> !entry.getKey().equals("relation"))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        StringBuilder nodeProperties = new StringBuilder();
        buildProperties(node, properties, nodeProperties, comparisonProperties, externalProperties);

        // Add relation
        if (relation != null && !relation.isEmpty()) {
            query.append(String.format("-[:%s]-", relation.replace(" ", "_").toUpperCase()));
        } else if (query.length() > 6) {
            query.append("--");
        }

        // Add node
        query.append("(").append(node).append(nodeProperties).append(")");
    }

    private void buildProperties(String node, Map<String, String> properties, StringBuilder nodeProperties, Map<Character, String[]> comparisonProperties, List<String> externalProperties) {
        if (properties.size() > 0) {
            nodeProperties.append("{");
            for (var property : properties.entrySet()) {
                String value = property.getValue();
                String key = property.getKey();
                Character operator = value.charAt(0);
                if (COMPARISON_OPERATORS.contains(operator)) {
                    comparisonProperties.put(operator, new String[]{node, key, value.substring(1)});
                    continue;
                } else if (PATH_VARIABLE_OPERATOR.equals(operator)) {
                    value = value.substring(1);
                    externalProperties.add(value);
                }
                nodeProperties.append(key).append(String.format(": '%s',", value));
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

    private String buildExternalVariables(List<String> externalProperties, String query) {
        for (String property : externalProperties) {
            String pathVariable = pathVariables.get(property);
            boolean isNumeric = pathVariable.matches("[+-]?\\d*(\\.\\d+)?");
            if (isNumeric) {
                query = query.replaceAll(String.format("'%s'", property), pathVariable);
                continue;
            }
            query = query.replaceAll(property, pathVariable);
        }
        return query;
    }
}
