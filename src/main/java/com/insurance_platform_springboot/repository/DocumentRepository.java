package com.insurance_platform_springboot.repository;

import com.insurance_platform_springboot.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByClaimId(Long claimId);
}
