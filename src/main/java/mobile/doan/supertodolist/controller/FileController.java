package mobile.doan.supertodolist.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import mobile.doan.supertodolist.dto.response.ApiResponse;
import mobile.doan.supertodolist.dto.response.ResUploadFileDTO;
import mobile.doan.supertodolist.services.FileService;
import mobile.doan.supertodolist.util.error.AppException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FileController {
    final FileService fileService;

    @Value("${nqoctai.upload-file.base-uri}")
    private String baseURI;

    @PostMapping("/files")
    public ResponseEntity<ApiResponse<ResUploadFileDTO>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, AppException {
        // validate
        if (file == null || file.isEmpty()) {
            throw new AppException("File is empty. Please upload a file.");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.endsWith(item));

        if (!isValid) {
            throw new AppException("Invalid file extension. only allows");
        }

        // create directory if not exists
        this.fileService.createUploadFolder(baseURI + folder);

        // save file
        String uploadFile = this.fileService.store(file, folder);
        ResUploadFileDTO res = ResUploadFileDTO.builder()
                .fileName(uploadFile)
                .uploadedAt(Instant.now())
                .build();

        ApiResponse<ResUploadFileDTO> response = ApiResponse.<ResUploadFileDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Upload file successful")
                .data(res)
                .build();

        return ResponseEntity.ok().body(response);
    }
}
