package de.philippbomers.management.students.student_manager;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FileController.
 */
public class FileControllerTest extends StudentManagerTest {

    /**
     * Tests the file upload and save to database
     */
    @Test
    void testUploadExcelFile() {

        try {

            // Fetches test file
            final Resource resource = new ClassPathResource("test.xlsx");
            final File file = resource.getFile();
            final FileInputStream input = new FileInputStream(file);

            // Tests if file is readable
            assertTrue(file.canRead());

            // Create a multipartFile to simulate a POST request with attachment
            final MockMultipartFile multipartFile = new MockMultipartFile("attachments",
                    file.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, input);

            // Checks if creation was successfully
            assertFalse(multipartFile.isEmpty());

            this.FileController.uploadExcelFile(multipartFile);

        } catch (final Exception e) {

            // Test fails if exception is thrown
            assertNull(e);
        } finally {

            // check if employment creation works
            assertFalse(this.employmentService.getAllEmployments().isEmpty());

            // check if student creation works
            assertFalse(this.studentService.getAllStudents().isEmpty());

            // check if project creation works
            assertFalse(this.projectService.getAllProjects().isEmpty());

            // check if allocation creation works
            assertFalse(this.allocationService.getAllAllocations().isEmpty());
        }
    }
}
