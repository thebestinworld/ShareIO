package com.share.io.service.file.undo;

import com.share.io.model.file.File;
import com.share.io.model.file.undo.FileSnap;
import com.share.io.security.UserCurrent;
import java.util.List;

public interface UndoService {

    FileSnap generateSnap(File file);

    void saveSnap(FileSnap fileSnap);

    void revertFile(UserCurrent user, Long fileId, Long versionId);

    void cleanHistory(Long fileId);

    List<Long> getFileVersions(Long fileId, Long version);

    void updateFileData(File file, Long fileId, Long version);
}
