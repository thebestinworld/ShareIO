package com.share.io.repository.file;

import com.share.io.model.file.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, String>, JpaSpecificationExecutor<File> {
}
