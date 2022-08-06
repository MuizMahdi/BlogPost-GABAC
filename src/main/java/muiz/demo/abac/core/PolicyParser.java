package muiz.demo.abac.core;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import java.util.*;

/**
 * Reads and parses policies json and constructs Policies.
 */
@AllArgsConstructor
public class PolicyParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyParser.class.getName());

    private final String policiesJson;

    public List<Policy> getPolicies() {
        Gson gson = new Gson();
        List<Policy> policies = Arrays.asList(gson.fromJson(policiesJson, Policy[].class));
        policies.forEach(policy -> {
            var policyRules = new LinkedHashSet<Policy.PolicyRule>();
            var parsedRules = policy.getRule();
            Policy.PolicyRule rule = new Policy.PolicyRule();
            constructRules(parsedRules, rule, policyRules);
            policy.setRules(policyRules);
        });
        return policies;
    }

    private static void constructRules(LinkedTreeMap<?, ?> map, Policy.PolicyRule rule, LinkedHashSet<Policy.PolicyRule> policyRules) {
        var leftKey = map.keySet().stream().findFirst();
        var leftVal = map.values().stream().findFirst();
        var rightKey = map.keySet().stream().skip(1).findFirst();
        var rightVal = map.values().stream().skip(1).findFirst();

        constructNodeRules(Pair.of(leftKey, leftVal), rule, policyRules);
        constructNodeRules(Pair.of(rightKey, rightVal), rule, policyRules);

        if (!rule.isEmpty()) policyRules.add(rule);
    }

    private static void constructNodeRules(Pair<Optional<?>, Optional<?>> node, Policy.PolicyRule rule, LinkedHashSet<Policy.PolicyRule> policyRules) {
        var key = node.getFirst();
        var value = node.getSecond();
        if (value.isPresent() && key.isPresent()) {
            if (key.get().equals("properties")) rule.setProperties((LinkedTreeMap) value.get());
            else {
                if (!rule.isEmpty()) {
                    policyRules.add(rule);
                }
                rule = new Policy.PolicyRule();
                rule.setType(key.get().toString());
                if (value.get() instanceof LinkedTreeMap<?, ?> nestedMap) {
                    constructRules(nestedMap, rule, policyRules);
                }
            }
        }
    }
}
