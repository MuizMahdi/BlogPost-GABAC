package muiz.demo.abac.security;

import muiz.demo.abac.core.PolicyEvaluator;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class AuthorizationPoliciesInterceptor implements HandlerInterceptor {

    private final Driver driver;

    @Autowired
    public AuthorizationPoliciesInterceptor(Driver driver) {
        this.driver = driver;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        var isValid = new PolicyEvaluator(pathVariables, driver).evaluate();
        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (!isValid) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return true;
    }
}
