package com.share.io.repository.file.undo;

import com.share.io.model.file.FileType;
import com.share.io.model.file.undo.FileSnap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

@Repository
public interface FileSnapRepository extends JpaRepository<FileSnap, Long> {

    @Modifying
    @Query(value = "update file as f"
            + "             inner join file_snap snap "
            + "                 on snap.id = f.id and snap.version = ?1"
            + "         set f.original_name           = snap.original_name, "
            + "             f.name                    = snap.name, "
            + "             f.description             = snap.description, "
            + "             f.file_type               = snap.file_type, "
            + "             f.content_type            = snap.content_type, "
            + "             f.extension               = snap.extension, "
            + "             f.upload_date             = snap.upload_date, "
            + "             f.update_date             = snap.update_date, "
            + "             f.data                    = snap.data, "
            + "             f.version                 = snap.version "
       ,
            nativeQuery = true)
    void revertToVersion(Long version);

    @Modifying
    @Query(value = "delete from file_snap where id = ?1", nativeQuery = true)
    void deleteByFileId(Long fileId);

    @Query(value = "select version from file_snap where id = ?1 and version != ?2", nativeQuery = true)
    List<Long> getFileVersionsByFileId(Long fileId, Long version);


    @Modifying
    @Query(value = "update file_snap set data = ?1, original_name = ?2, content_type = ?3, extension = ?4, file_type = ?5 " +
            "where  id = ?6 and version =?7", nativeQuery = true)
    void updateFileSnap(byte[] data, String originalName, String contentType, String extension, FileType fileType, Long fileId, Long version);
}
