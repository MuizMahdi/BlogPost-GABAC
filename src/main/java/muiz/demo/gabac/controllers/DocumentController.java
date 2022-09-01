package muiz.demo.gabac.controllers;

import muiz.demo.gabac.data.entities.Document;
import muiz.demo.gabac.services.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("document")
public class DocumentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class.getName());

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/{id}")
    public Document getDocument(@PathVariable Long id) {
        return documentService.getDocumentById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationPolicies.isMemberOfAuthorizedDepartment()")
    public Document getDocumentPreAuthorized(@PathVariable Long id) {
        return documentService.getDocumentById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
