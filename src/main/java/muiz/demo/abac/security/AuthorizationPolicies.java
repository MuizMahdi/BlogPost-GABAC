package muiz.demo.abac.security;

import muiz.demo.abac.data.repositories.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service("authorizationPolicies")
public class AuthorizationPolicies {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationPolicies.class.getName());

    private final HttpServletRequest request;

    private final DocumentRepository documentRepository;

    @Autowired
    public AuthorizationPolicies(HttpServletRequest request, DocumentRepository documentRepository) {
        this.request = request;
        this.documentRepository = documentRepository;
    }

    public boolean isMemberOfAuthorizedDepartment() {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String currentUsername = request.getUserPrincipal().getName();
        Long requestedDocumentId = Long.parseLong(pathVariables.get("id"));
        return documentRepository.isWorking(requestedDocumentId, currentUsername);
    }
}
