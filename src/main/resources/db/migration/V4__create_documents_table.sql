CREATE TABLE documents(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_id BIGINT NOT NULL ,
    file_name VARCHAR(255) NOT NULL ,
    file_path VARCHAR(500) NOT NULL ,
    content_type VARCHAR(100) NOT NULL ,
    file_size BIGINT NOT NULL ,
    uploaded_at TIMESTAMP(6) NOT NULL ,
    CONSTRAINT fk_document_claim FOREIGN KEY (claim_id) REFERENCES claims(id)
);