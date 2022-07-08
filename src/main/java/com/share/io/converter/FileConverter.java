package com.share.io.converter;

import com.share.io.dto.file.FileDTO;
import com.share.io.model.file.File;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class FileConverter {

    private final ModelMapper modelMapper;

    public FileConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<FileDTO> convert(Collection<File> files) {
        return files.stream().map(this::convert)
                .collect(Collectors.toList());
    }

    public FileDTO convert(File file) {
        FileDTO dto = modelMapper.map(file, FileDTO.class);
        if (dto.getUploadDate() != null) {
            String uploadDate = file.getUploadDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            dto.setUploadDate(uploadDate);
        }
        if (dto.getUpdateDate() != null) {
            String updateDate = file.getUpdateDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            dto.setUpdateDate(updateDate);
        }
        if (file.getData() != null) {
            String byteToString = Base64.getEncoder().encodeToString(file.getData());
            dto.setEncodedData(byteToString);
        }
        dto.setUploaderName(file.getUploader().getUsername());
        dto.setUploaderId(file.getUploader().getId());
        return dto;
    }
}
