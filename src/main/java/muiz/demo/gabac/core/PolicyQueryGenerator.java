package muiz.demo.gabac.core;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    private static final Character PATH_VARIABLE_OPERATOR = '/';

    /**
     * Indicates a predefined variable that would be obtained from the spring context, e.g. user principal
     */
    private static final Character CONTEXT_VARIABLE_OPERATOR = '$';

    private static Map<Character, String[]> comparisonProperties;

    private static List<String> pathProperties;

    private static List<String> contextProperties;

    private HttpServletRequest request;

    public PolicyQueryGenerator(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Constructs a cypher query from a set of policy rules
     * @return Cypher query string
     */
    public String buildQuery(Set<Policy.PolicyRule> rules) {
        comparisonProperties = new HashMap<>();
        pathProperties = new ArrayList<>();
        contextProperties = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("MATCH ");

        rules.forEach(rule -> buildNode(rule, query));
        buildConditions(query);
        buildPathProperties(query);
        buildContextProperties(query);

        return query.append(String.format(" RETURN count(%s) > 0", rules.stream().toList().get(0).getType())).toString();
    }

    /**
     * Constructs a node for the policy's cypher query from a policy rule
     * @param rule The policy rule
     * @param query The cypher query
     */
    private void buildNode(Policy.PolicyRule rule, StringBuilder query) {
        String node = rule.getType();
        var properties = rule.getProperties();
        String relation = properties != null ? properties.remove("relation") : "";
        StringBuilder nodeProperties = buildProperties(node, properties);

        // Construct node relation
        if (relation != null && !relation.isEmpty()) {
            query.append(String.format("-[:%s]-", relation.replace(" ", "_").toUpperCase()));
        } else if (query.length() > 6) {
            query.append("--");
        }

        // Add node
        query.append("(").append(node).append(nodeProperties).append(")");
    }

    /**
     * Constructs the properties of nodes in the cypher query
     * @param node The node
     * @param properties Properties of the node
     * @return The node's cypher query string representation with the properties attached
     */
    private StringBuilder buildProperties(String node, Map<String, String> properties) {
        StringBuilder nodeProperties = new StringBuilder();
        if (properties != null && properties.size() > 0) {
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
                    pathProperties.add(value);
                } else if (CONTEXT_VARIABLE_OPERATOR.equals(operator)) {
                    value = value.substring(1);
                    contextProperties.add(value);
                }
                nodeProperties.append(key).append(String.format(": '%s',", value));
            }
            nodeProperties.deleteCharAt(nodeProperties.length()-1);
            nodeProperties.append("}");
        }
        return nodeProperties;
    }

    /**
     * Constructs the comparison conditions in the cypher query from the detected comparison properties
     * @param query The cypher query with conditions attached
     */
    private void buildConditions(StringBuilder query) {
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

    /**
     * Constructs the path properties in the cypher query by replacing properties with the path prefix with their
     * actual values
     * @param query The cypher query
     */
    private void buildPathProperties(StringBuilder query) {
        var pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        for (String property : pathProperties) {
            String pathVariable = pathVariables.get(property);
            boolean isNumeric = pathVariable.matches("[+-]?\\d*(\\.\\d+)?");
            if (isNumeric) {
                String formattedProperty = String.format("'%s'", property);
                int propertyIdx = query.indexOf(formattedProperty);
                query.replace(propertyIdx, propertyIdx + formattedProperty.length(), pathVariable);
                continue;
            }
            int propertyIdx = query.indexOf(property);
            query.replace(propertyIdx, propertyIdx + property.length(), pathVariable);
        }
    }

    /**
     * Constructs the predefined spring context properties by replacing the properties with the context prefix with
     * their actual values
     * @param query The cypher query
     */
    private void buildContextProperties(StringBuilder query) {
        var user = request.getUserPrincipal();
        for (String property : contextProperties) {
            if ("current_username".equals(property)) {
                int propertyIdx = query.indexOf(property);
                query.replace(propertyIdx, propertyIdx + property.length(), user.getName());
            }
        }
    }
}
