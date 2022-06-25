package muiz.demo.abac.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

    @GetMapping("/report")
    @PreAuthorize("@authorizationPolicies.hasAccess()")
    public ResponseEntity getReport() {
        return ResponseEntity.ok().build();
    }

}
