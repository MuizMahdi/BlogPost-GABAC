package muiz.demo.abac.services;

import muiz.demo.abac.data.entities.Document;
import muiz.demo.abac.data.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Optional<Document> getDocumentById(Long documentId) {
        return documentRepository.findById(documentId);
    }
}
