package muiz.demo.abac.security;

import muiz.demo.abac.core.PolicyEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthorizationPoliciesInterceptor implements HandlerInterceptor {

    private final PolicyEvaluator evaluator;

    @Autowired
    public AuthorizationPoliciesInterceptor(PolicyEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!evaluator.isAllowed(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
