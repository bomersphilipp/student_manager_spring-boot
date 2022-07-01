package de.philippbomers.management.students.student_manager.controller;

import de.philippbomers.management.students.student_manager.service.FileService;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * API Rest Controller to handle file uploads
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    // We use constructor based injection because field based injection is not immutable

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Uploads an Excel file and save content to database
     *
     * @param file .xlsx MultipartFile
     * @return String with report of data saving
     */
    @PostMapping
    public String uploadExcelFile(@RequestParam("file") final MultipartFile file) throws ResponseStatusException {
        try {
            final String result = this.fileService.importExcelFile(file);

            // the string must be converted into JSON object
            return JSONObject.quote(result);
        } catch (final Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Upload failed");
        }
    }
}
