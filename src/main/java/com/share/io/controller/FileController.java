package com.share.io.controller;

import com.share.io.dto.file.FileShareDTO;
import com.share.io.dto.file.FileUploadDTO;
import com.share.io.dto.query.file.FileQuery;
import com.share.io.dto.response.FileResponse;
import com.share.io.dto.response.MessageResponse;
import com.share.io.model.file.File;
import com.share.io.security.CurrentUser;
import com.share.io.security.UserCurrent;
import com.share.io.service.file.FileStorageService;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/file")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                      @CurrentUser UserCurrent userCurrent) {
        String message;
        try {
            File result = fileStorageService.save(file, userCurrent.getId());
            message = "Uploaded the file successfully with id: " + result.getId();
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
        }
    }

    @PostMapping("/{id}/upload/metadata")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> uploadFileMetadata(@PathVariable("id") String id,
                                                              @RequestBody FileUploadDTO file,
                                                              @CurrentUser UserCurrent userCurrent) {
        File result = fileStorageService.saveFileMetadata(id, file, userCurrent.getId());
        String message = "Uploaded fie metadata successfully with id: " + result.getId();
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));

    }

    @PostMapping("/{id}/share")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> shareFile(@PathVariable("id") String id,
                                                     @RequestBody FileShareDTO fileShareDTO,
                                                     @CurrentUser UserCurrent userCurrent) {
        fileStorageService.shareFile(id, userCurrent.getId(), fileShareDTO.getUserToShareId());
        String message = "File shared successfully to user with id: " + fileShareDTO.getUserToShareId();
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
    }

    @GetMapping("/response")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<FileResponse>> getListFileResponse() {
        List<FileResponse> files = fileStorageService.getAllFiles().map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/file/")
                    .path(dbFile.getId())
                    .toUriString();

            return new FileResponse(
                    dbFile.getName(),
                    fileDownloadUri,
                    dbFile.getContentType(),
                    dbFile.getData().length);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @PostMapping()
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<File>> getFiles(@RequestBody FileQuery fileQuery) {
        Collection<File> files = fileStorageService.findAllFilesBySpecification(fileQuery);
        return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>(files));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        File fileDB = fileStorageService.getFileById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(fileDB.getData());
    }

    //TODO: Add search
}
