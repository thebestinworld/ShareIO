package com.share.io.controller;

import com.share.io.dto.file.undo.RevertDTO;
import com.share.io.dto.response.MessageResponse;
import com.share.io.model.file.File;
import com.share.io.security.CurrentUser;
import com.share.io.security.UserCurrent;
import com.share.io.service.file.FileService;
import com.share.io.service.file.undo.UndoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/revert")
public class RevertController {

    private final UndoService undoService;
    private final FileService fileService;

    public RevertController(UndoService undoService, FileService fileService) {
        this.undoService = undoService;
        this.fileService = fileService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Long>> getFileVersions(@PathVariable Long id) {
        File fileById = fileService.getFileById(id);
        return ResponseEntity.status(HttpStatus.OK).body(undoService.getFileVersions(id, fileById.getVersion()));
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> revertFile(@PathVariable Long id, @RequestBody RevertDTO version,
                                                      @CurrentUser UserCurrent userCurrent) {
        File fileById = fileService.getFileById(id);
        undoService.saveSnap(undoService.generateSnap(fileById));
        undoService.revertFile(userCurrent, id, version.getVersionId());
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Revert Successful"));
    }
}
