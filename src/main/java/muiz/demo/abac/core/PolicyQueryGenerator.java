package muiz.demo.abac.core;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
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
