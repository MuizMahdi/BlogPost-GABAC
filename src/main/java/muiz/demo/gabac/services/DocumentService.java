package muiz.demo.gabac.services;

import muiz.demo.gabac.data.entities.Document;
import muiz.demo.gabac.data.repositories.DocumentRepository;
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

    /**
     * Retrieves document by documentId
     * @param documentId The document ID
     * @return The document
     */
    public Optional<Document> getDocumentById(Long documentId) {
        return documentRepository.findById(documentId);
    }
}
