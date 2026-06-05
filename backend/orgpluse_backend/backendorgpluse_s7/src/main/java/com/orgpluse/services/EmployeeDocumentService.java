package com.orgpluse.services;

import com.orgpluse.entities.Employee;
import com.orgpluse.entities.EmployeeDocument;
import com.orgpluse.repositories.EmployeeDocumentRepository;
import com.orgpluse.repositories.EmployeeRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeDocumentService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Autowired
    private EmployeeDocumentRepository documentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UniversalResponse response;

    // ── UPLOAD ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> uploadDocument(Long employeeId,
                                                          MultipartFile file,
                                                          String documentLabel) {
        // Validate employee
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            return response.send("Employee not found with id: " + employeeId,
                    null, HttpStatus.NOT_FOUND);
        }

        // Reject empty files
        if (file == null || file.isEmpty()) {
            return response.send("File must not be empty", null, HttpStatus.BAD_REQUEST);
        }

        // Sanitise original filename
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "document";
        }
        // Strip any path separators a malicious client might send
        originalName = Paths.get(originalName).getFileName().toString();

        // Build storage path:  uploads/documents/{employeeId}/{uuid}_{originalName}
        String storedName = UUID.randomUUID().toString().replace("-", "") + "_" + originalName;
        String relativePath = "documents/" + employeeId + "/" + storedName;

        try {
            Path targetDir  = Paths.get(uploadDir).toAbsolutePath().normalize()
                                   .resolve("documents").resolve(String.valueOf(employeeId));
            Files.createDirectories(targetDir);

            Path targetFile = targetDir.resolve(storedName);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return response.send("Failed to store file: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Persist metadata
        EmployeeDocument doc = new EmployeeDocument();
        doc.setEmployee(empOpt.get());
        doc.setOriginalName(originalName);
        doc.setStoredName(storedName);
        doc.setFileType(file.getContentType());
        doc.setFileSize(file.getSize());
        doc.setFilePath(relativePath);
        doc.setDocumentLabel(documentLabel != null && !documentLabel.isBlank()
                ? documentLabel : originalName);

        EmployeeDocument saved = documentRepository.save(doc);
        return response.send("Document uploaded successfully", saved, HttpStatus.CREATED);
    }

    // ── LIST ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> listDocuments(Long employeeId) {
        if (employeeRepository.findById(employeeId).isEmpty()) {
            return response.send("Employee not found with id: " + employeeId,
                    null, HttpStatus.NOT_FOUND);
        }
        List<EmployeeDocument> docs =
                documentRepository.findByEmployeeIdOrderByUploadedAtDesc(employeeId);
        return response.send("Documents fetched successfully", docs, HttpStatus.OK);
    }

    // ── DOWNLOAD ──────────────────────────────────────────────────────────────

    public ResponseEntity<?> downloadDocument(Long employeeId, Long documentId) {
        Optional<EmployeeDocument> docOpt = documentRepository.findById(documentId);
        if (docOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        EmployeeDocument doc = docOpt.get();

        // Security: only the owning employee can download
        if (!doc.getEmployee().getId().equals(employeeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize()
                                 .resolve(doc.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = doc.getFileType() != null
                    ? doc.getFileType()
                    : MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + doc.getOriginalName() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> deleteDocument(Long employeeId, Long documentId) {
        Optional<EmployeeDocument> docOpt = documentRepository.findById(documentId);
        if (docOpt.isEmpty()) {
            return response.send("Document not found with id: " + documentId,
                    null, HttpStatus.NOT_FOUND);
        }
        EmployeeDocument doc = docOpt.get();

        if (!doc.getEmployee().getId().equals(employeeId)) {
            return response.send("You are not authorised to delete this document",
                    null, HttpStatus.FORBIDDEN);
        }

        // Delete physical file (best-effort — don't fail if already gone)
        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize()
                                 .resolve(doc.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
            // Log-worthy but not fatal — proceed to remove the DB record
        }

        documentRepository.deleteById(documentId);
        return response.send("Document deleted successfully", null, HttpStatus.OK);
    }

}
