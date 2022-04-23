package com.share.io.model.file.undo;

import com.share.io.model.file.FileType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import java.io.Serializable;

@Entity
@IdClass(FileSnap.FileSnapPK.class)
public class FileSnap {

    @Id
    private Long id;

    @Id
    private Long version;

    private String originalName;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    private String contentType;

    private String extension;

    @Lob
    private byte[] data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    static class FileSnapPK implements Serializable {

        private static final long serialVersionUID = 1L;

        private Long id;
        private Long version;

        public FileSnapPK() {
        }

        public FileSnapPK(Long id, Long version) {
            this.id = id;
            this.version = version;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getVersion() {
            return version;
        }

        public void setVersion(Long version) {
            this.version = version;
        }
    }
}
