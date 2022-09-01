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

    private final Map<String, String> pathVariables;

    @Autowired
    public AuthorizationPoliciesService(HttpServletRequest request, DocumentRepository documentRepository) {
        this.request = request;
        this.documentRepository = documentRepository;
        this.pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }

    /**
     * Checks whether the current user is a member of an authorized department for a document
     * @return Whether the document is published by the department in which the current user is a member of
     */
    public boolean isMemberOfAuthorizedDepartment() {
        String currentUsername = request.getUserPrincipal().getName();
        Long requestedDocumentId = Long.parseLong(pathVariables.get("id"));
        return documentRepository.isPublishedByUserDepartment(requestedDocumentId, currentUsername);
    }
}
