package com.share.io.model.user;


import com.share.io.model.file.File;
import com.share.io.model.role.Role;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    @OneToMany(mappedBy = "uploader")
    private Set<File> uploadedFiles;

    @ManyToMany(mappedBy = "sharedUsers")
    private Set<File> sharedFiles;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<File> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(Set<File> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public Set<File> getSharedFiles() {
        return sharedFiles;
    }

    public void setSharedFiles(Set<File> sharedFiles) {
        this.sharedFiles = sharedFiles;
    }

    public void addSharedFile(File file) {
        if (sharedFiles == null) {
            sharedFiles = new HashSet<>();
        }
        sharedFiles.add(file);
    }

    public void removeSharedFile(File file) {
        if (sharedFiles == null) {
            return;
        }
        sharedFiles.remove(file);
    }
}
