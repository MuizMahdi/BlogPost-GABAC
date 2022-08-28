package muiz.demo.gabac.core;

import com.google.gson.internal.LinkedTreeMap;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class Policy {
    private PolicyResource resource;
    private LinkedTreeMap<?, ?> rule;
    private transient Set<PolicyRule> rules;

    @Data
    @AllArgsConstructor
    public static class PolicyResource {
        private String type;
        private String action;
        private boolean permitted;
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
