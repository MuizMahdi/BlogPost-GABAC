package muiz.demo.abac.core;

import com.google.gson.internal.LinkedTreeMap;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class Policy {
    private PolicyResource resource;
    private LinkedTreeMap<?, ?> rule;
    private transient Set<PolicyRule> rules;

    @Data
    public static class PolicyResource {
        private String type;
        private String action;
        private String effect;
    }

    @Data
    public static class PolicyRule {
        private static final PolicyRule EMPTY = new PolicyRule();

        private String type;
        private Map<String, String> properties;

        public boolean isEmpty() {
            return this.equals(EMPTY);
        }
    }
}
