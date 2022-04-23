package com.share.io.service.file.undo;

import com.share.io.model.file.File;
import com.share.io.model.file.undo.FileSnap;
import com.share.io.repository.file.undo.FileSnapRepository;
import com.share.io.security.UserCurrent;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UndoServiceImpl implements UndoService {

    private final ModelMapper mapper;
    private final FileSnapRepository fileSnapRepository;

    public UndoServiceImpl(ModelMapper mapper, FileSnapRepository fileSnapRepository) {
        this.mapper = mapper;
        this.fileSnapRepository = fileSnapRepository;
    }

    @Override
    public FileSnap generateSnap(File file) {
        return mapper.map(file, FileSnap.class);
    }

    @Override
    @Transactional
    @Async
    public void saveSnap(FileSnap fileSnap) {
        fileSnapRepository.save(fileSnap);
    }

    @Override
    @Transactional
    public void revertFile(UserCurrent user, Long fileId, Long versionId) {
        fileSnapRepository.revertToVersion(versionId);
    }

    @Override
    @Async
    @Transactional
    public void cleanHistory(Long fileId) {
        this.fileSnapRepository.deleteByFileId(fileId);
    }

    @Override
    public List<Long> getFileVersions(Long fileId, Long version) {
        return fileSnapRepository.getFileVersionsByFileId(fileId, version);
    }

    @Override
    @Transactional
    public void updateFileData(File file, Long fileId, Long version) {
        fileSnapRepository.updateFileSnap(file.getData(), file.getOriginalName(), file.getContentType(), file.getExtension(), file.getFileType()
                ,  fileId,  version);
    }
}
