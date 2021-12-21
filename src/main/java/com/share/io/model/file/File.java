package com.share.io.model.file;

import com.share.io.model.user.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String originalName;

    private String name;

    private String description;

    @Enumerated
    private FileType fileType;

    private String contentType;

    private String extension;

    private Long version;

    private LocalDateTime uploadDate;

    @ManyToOne
    @JoinColumn(name="uploader_id", nullable=false)
    private User uploader;

    @ManyToMany(mappedBy = "sharedFiles")
    Set<User> sharedUsers;

    @Lob
    private byte[] data;

    public File() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setContentType(String originalType) {
        this.contentType = originalType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public Set<User> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(Set<User> sharedUsers) {
        this.sharedUsers = sharedUsers;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime dateUploaded) {
        this.uploadDate = dateUploaded;
    }
}
