package muiz.demo.abac.controllers;

import muiz.demo.abac.core.PolicyEvaluator;
import muiz.demo.abac.core.PolicyParser;
import muiz.demo.abac.data.entities.Document;
import muiz.demo.abac.services.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("document")
public class DocumentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class.getName());

    private final DocumentService documentService;

    private final PolicyEvaluator policyEvaluator;

    @Autowired
    public DocumentController(DocumentService documentService, PolicyEvaluator policyEvaluator) {
        this.documentService = documentService;
        this.policyEvaluator = policyEvaluator;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationPolicies.isMemberOfAuthorizedDepartment()")
    public Document getDocument(@PathVariable Long id) {
        // TODO: Should be called in a generic manner (perhaps via a filter, or Spring AOP)
        policyEvaluator.evaluate();
        Optional<Document> document = documentService.getDocumentById(id);
        if (document.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Document");
        }
        return document.get();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationPolicies.isHeadOfDepartmentAtEvening()")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        return ResponseEntity.ok().build();
    }
}
