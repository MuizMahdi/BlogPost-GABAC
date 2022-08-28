package muiz.demo.gabac.security;

import muiz.demo.gabac.data.repositories.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service("authorizationPolicies")
public class AuthorizationPoliciesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationPoliciesService.class.getName());

    private final HttpServletRequest request;

    private final DocumentRepository documentRepository;

    @Autowired
    public AuthorizationPoliciesService(HttpServletRequest request, DocumentRepository documentRepository) {
        this.request = request;
        this.documentRepository = documentRepository;
    }

    public boolean isMemberOfAuthorizedDepartment() {
        var pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String currentUsername = request.getUserPrincipal().getName();
        Long requestedDocumentId = Long.parseLong(pathVariables.get("id"));
        return documentRepository.isPublishedByUserDepartment(requestedDocumentId, currentUsername);
    }
}
