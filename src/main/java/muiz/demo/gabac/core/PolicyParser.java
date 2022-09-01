package muiz.demo.gabac.core;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Reads and parses policies json and constructs Policies.
 */
public class PolicyParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyParser.class.getName());

    private String policiesJson;

    public PolicyParser() {
        try {
            policiesJson = getAccessPolicies();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    /**
     * Constructs policies and their rules from the policies JSON.
     * @return Parsed policies
     */
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

    /**
     * Retrieves the access policies JSON
     * @return Policies in JSON format
     * @throws IOException
     */
    private String getAccessPolicies() throws IOException {
        File policiesFile = ResourceUtils.getFile("classpath:data/access_policies.json");
        return new String(Files.readAllBytes(policiesFile.toPath()));
    }

    /**
     * Parses the JSON representation of the nested, tree structured policy rules in order to construct the relational
     * part of the cypher query.
     * @param map Rules tree
     * @param rule The rule which will contain the parsed information
     * @param policyRules List of rules that would contain all parsed policy rules
     */
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
