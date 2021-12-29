package com.share.io.controller;

import com.share.io.dto.file.FileDTO;
import com.share.io.dto.file.FileShareDTO;
import com.share.io.dto.file.FileUpdateDTO;
import com.share.io.dto.file.FileUploadDTO;
import com.share.io.dto.query.file.FileQuery;
import com.share.io.dto.response.FileResponse;
import com.share.io.dto.response.MessageResponse;
import com.share.io.model.file.File;
import com.share.io.security.CurrentUser;
import com.share.io.security.UserCurrent;
import com.share.io.service.file.FileStorageService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
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

    @PostMapping("/{id}/upload/data")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> uploadFile(@PathVariable("id") String id,
                                                      @RequestParam("file") MultipartFile file,
                                                      @CurrentUser UserCurrent userCurrent) {
        String message;
        try {
            File result = fileStorageService.save(id, file, userCurrent.getId());
            message = "Uploaded the file successfully with id: " + result.getId();
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
        }
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FileDTO> uploadFileMetadata(@RequestBody FileUploadDTO file, @CurrentUser UserCurrent userCurrent) {
        File result = fileStorageService.saveFileMetadata(file, userCurrent.getId());
        String message = "Uploaded fie metadata successfully with id: " + result.getId();
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(result.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(fileDTO);
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

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<FileDTO>> getFiles(@RequestBody FileQuery fileQuery) {
        ModelMapper modelMapper = new ModelMapper();
        Collection<File> files = fileStorageService.findAllFilesBySpecification(fileQuery);
        List<FileDTO> fileDTOS = files.stream()
                .map(file -> modelMapper.map(file, FileDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(fileDTOS);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FileDTO> getFile(@PathVariable String id) {
        File fileDB = fileStorageService.getFileById(id);
        ModelMapper modelMapper = new ModelMapper();
        FileDTO map = modelMapper.map(fileDB, FileDTO.class);
        String byteToString = Base64.getEncoder().encodeToString(fileDB.getData());
        map.setEncodedData(byteToString);
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FileDTO> updateFile(@PathVariable String id, @RequestBody FileUpdateDTO dto) {
        File fileDB = fileStorageService.updateFileMetaData(id, dto);
        ModelMapper modelMapper = new ModelMapper();
        FileDTO map = modelMapper.map(fileDB, FileDTO.class);
        String byteToString = Base64.getEncoder().encodeToString(fileDB.getData());
        map.setEncodedData(byteToString);
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    @PostMapping("/{id}/data")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateFileData(@PathVariable("id") String id,
                                                          @RequestParam("file") MultipartFile file) {
        String message;
        try {
            File result = fileStorageService.update(id, file);
            message = "Uploaded the file successfully with id: " + result.getId();

            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteFile(@PathVariable String id) {
        fileStorageService.deleteFile(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted");
    }

    //TODO: Add search
}
