package de.philippbomers.management.students.student_manager.service;

import de.philippbomers.management.students.student_manager.entity.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class FileService {

    // We use constructor based injection because field based injection is not immutable

    protected final EmploymentService employmentService;

    protected final PeriodService periodService;

    protected final ProjectService projectService;

    protected final AllocationService allocationService;

    protected final StudentService studentService;

    // Variable initialization to read the fields.
    // I added more variables than actual needed for possible future feature
    // upgrades.
    LocalDate localDate;
    Double doubleValue;
    String stringValue;
    Boolean booleanValue;
    String formulaValue;

    String firstName, lastName, employmentName, projectName;
    LocalDate allocationFrom, allocationTo, projectFrom, projectTo;

    public FileService(EmploymentService employmentService, PeriodService periodService, ProjectService projectService, AllocationService allocationService, StudentService studentService) {
        this.employmentService = employmentService;
        this.periodService = periodService;
        this.projectService = projectService;
        this.allocationService = allocationService;
        this.studentService = studentService;
    }

    /**
     * Constructor with standard table beginning on the top left
     *
     * @param multipartFile .xlsx file
     * @return String with status
     */
    public String importExcelFile(final MultipartFile multipartFile) {
        return this.importExcelFile(multipartFile, 0, 1, 0);
    }

    /**
     * Current Excel table structure: first_name last_name employment_name allocation_from
     * allocation_to project_name project_from project_to
     * <p>
     * If you begin at the top left, the call would be: importExcelFile(multipartFile, 0, 1, 0);
     *
     * <a href="https://poi.apache.org/">Developed with Apache POI API (5.2.2).</a>
     *
     * @param multipartFile .xlsx file
     * @param sheetNumber   number of working sheet, beginning by 0
     * @param beginAtRow    row number of data, beginning by 1
     * @param beginAtColumn column number of data, beginning by 0
     * @return String with status
     */
    public String importExcelFile(final MultipartFile multipartFile, int sheetNumber,
                                  final int beginAtRow, final int beginAtColumn) {

        /*
          TODO: Shorten this method
         */

        File file;
        FileInputStream fileInputStream;
        XSSFWorkbook workbook;
        XSSFSheet sheet;
        final AtomicReference<StringBuilder> issueCollector = new AtomicReference<>(new StringBuilder());

        try {

            // Opens file
            file = this.multipartToFile(multipartFile);

            // Reads the file
            fileInputStream = new FileInputStream(file);

            // Creates Workbook instance holding reference to .xlsx file
            // Automatically throws an issue when the file is not an .xlsx file
            workbook = new XSSFWorkbook(fileInputStream);


        } catch (final Exception e) {
            // Return an error message
            return "The file could not be opened or read.";
        }

        // Set first sheet if user inserts an invalid number
        if (workbook.getNumberOfSheets() >= sheetNumber || workbook.getNumberOfSheets() < 0) {
            sheetNumber = 0;
        }

        // Gets first/desired sheet from the workbook
        sheet = workbook.getSheetAt(sheetNumber);

        // Iterate through each row one by one
        final AtomicReference<Iterator<Row>> rowIterator = new AtomicReference<>(sheet.iterator());

        // call cellIterator to continue or break over multiple loops
        cellIterator:
        while (rowIterator.get().hasNext()) {
            final AtomicReference<Row> row = new AtomicReference<>(rowIterator.get().next());

            // Selects the actual field
            final AtomicInteger actualNumber = new AtomicInteger(0);

            // Checks if the table content row begins
            if (row.get().getRowNum() >= beginAtRow) {

                // For each row, iterates through all the columns
                final Iterator<Cell> cellIterator = row.get().cellIterator();
                while (cellIterator.hasNext()) {
                    final AtomicReference<Cell> cell = new AtomicReference<>(cellIterator.next());

                    // checks if the table content column begins
                    if (cell.get().getColumnIndex() >= beginAtColumn) {

                        try {
                            /*
                              TODO: add the possibility to let the user decide the row number of
                              each entry
                             */

                            // reads the current field with the correct format
                            this.setCurrentField(cell.get());

                            // sets the entity field for later save in database
                            this.addEntities(actualNumber.getAndIncrement());

                        } catch (final Exception e) {

                            // Collects exceptions
                            issueCollector.get().append("Row: ").append(cell.get().getRowIndex())
                                    .append(", Column: ").append(cell.get().getColumnIndex()).append(": ")
                                    .append(e).append(employmentName).append('\n');

                            // Clears variables for next iteration
                            this.clearCurrentRowCache();

                            // Continues iteration without saving to database
                            continue cellIterator;
                        }

                    }
                }
            }

            try {

                // checks if all values are set
                if (!(Objects.equals(this.firstName, "") || Objects.equals(this.lastName, "") || Objects.equals(this.employmentName, "")
                        || this.projectFrom == null || this.projectTo == null
                        || this.allocationFrom == null || this.allocationTo == null)) {
                    // Saves the row to database
                    this.saveToDatabase();
                }

            } catch (final Exception e) {

                // Collects exceptions
                issueCollector.get().append("Server issue: ").append(e).append("\n");

            }

            // Clears the row variables for next iteration
            this.clearCurrentRowCache();
        }
        try {

            // Closes all file handlers
            fileInputStream.close();
            workbook.close();

            // Deletes the file from server
            assert file.delete() : "File could not be deleted from Server.";

        } catch (final Exception e) {

            // Collects exceptions
            issueCollector.get().append("Server issue: ").append(e).append("\n");
        }

        // Returns success message with exception hints
        return issueCollector.get().append("\n").append("Upload Success!").toString();
    }

    /**
     * Saves a row to database
     *
     * @throws Exception if there was any issue
     */
    private void saveToDatabase() throws Exception {

        // Variables for the entities that we save in the database
        Student currentStudent;
        Employment currentEmployment;
        Project currentProject;

        try {

            // Adds employment
            // If the employment does not exist, it creates a new one
            final Optional<Employment> findEmployment = this.employmentService.getAllEmployments()
                    .stream().filter(emp -> Objects.equals(emp.getName(), this.employmentName)).findFirst();

            currentEmployment = findEmployment.orElseGet(() -> this.employmentService
                    .setEmployment(Employment.builder().name(this.employmentName).build()));

        } catch (final Exception e) {

            // Throws exception if there is any issue
            throw new Exception(
                    "Database issue by adding employment " + this.employmentName + ": " + e);
        }

        try {

            // Adds student
            // If the student does not exist, it creates a new one.
            // Adds the employment from above.
            final Optional<Student> findStudent = this.studentService.getAllStudents().stream()
                    .filter(student -> Objects.equals(student.getFirstName(), firstName)
                            && Objects.equals(student.getLastName(), lastName)
                            && Objects.equals(student.getEmployment().getName(), employmentName))
                    .findFirst();

            currentStudent = findStudent.orElseGet(() -> this.studentService.setStudent(Student.builder().firstName(firstName)
                    .lastName(lastName).employment(currentEmployment).build()));

        } catch (final Exception e) {

            // Throws exception if there is any issue
            throw new Exception("Database issue by adding student " + this.firstName + " "
                    + this.lastName + ": " + e);
        }

        try {

            // Adds project
            // If the project does not exist, creates a new one including the given period.
            final AtomicReference<Optional<Project>> findProject = new AtomicReference<>(this.projectService.getAllProjects().stream()
                    .filter(project -> project.getName().equals(this.projectName)).findFirst());

            currentProject = findProject.get().orElseGet(() -> this.projectService.setProject(Project
                    .builder().name(this.projectName).period(Period.builder()
                            .begin(this.projectFrom).end(this.projectTo).build())
                    .build()));

        } catch (final Exception e) {

            // Throws exception if there is any issue
            throw new Exception("Database issue by adding project " + this.projectName + ": " + e);
        }

        try {

            // Adds allocation
            // If the allocation does not exist, it creates a new one
            final AtomicReference<Optional<Allocation>> findAllocation = new AtomicReference<>(this.allocationService.getAllAllocations()
                    .stream()
                    .filter(alloc -> alloc.getPeriod().getBegin().equals(this.allocationFrom)
                            && alloc.getPeriod().getEnd().equals(this.allocationTo)
                            && alloc.getProject().equals(currentProject)
                            && alloc.getStudent().equals(currentStudent))
                    .findFirst());

            if (findAllocation.get().isEmpty()) {
                this.allocationService.setAllocation(Allocation
                        .builder().project(currentProject).period(Period.builder()
                                .begin(this.projectFrom).end(this.projectTo).build())
                        .student(currentStudent).build());
            }
            // When allocation is present, go to the next entry

        } catch (final Exception e) {

            // Throws exception if there is any issue
            throw new Exception("Database issue by adding the allocation " + this.firstName + " "
                    + this.lastName + " to " + this.employmentName + ": " + e);
        }
    }

    /**
     * Saves Excel field values to save them into database
     *
     * @param currentNumber current order of reading the entities from the table
     */
    private void addEntities(final int currentNumber) {
        switch (currentNumber) {
            case 0 ->
                // set first name
                    this.firstName = this.stringValue;
            case 1 ->
                // set last name
                    this.lastName = this.stringValue;
            case 2 ->
                // set employment name
                    this.employmentName = this.stringValue;
            case 3 ->
                // set allocation from
                    this.allocationFrom = this.localDate;
            case 4 ->
                // set allocation to
                    this.allocationTo = this.localDate;
            case 5 ->
                // set project name
                    this.projectName = this.stringValue;
            case 6 ->
                // set project from
                    this.projectFrom = this.localDate;
            case 7 ->
                // set project to
                    this.projectTo = this.localDate;
        }
    }

    /**
     * Resets all variables to create new Entities in the next row
     */
    private void clearCurrentRowCache() {

        /*
          TODO: Think about the alternative of creating a new Entity instead of resetting
          everything?
         */

        this.localDate = null;
        this.doubleValue = null;
        this.stringValue = null;
        this.booleanValue = null;
        this.formulaValue = null;

        this.firstName = null;
        this.lastName = null;
        this.employmentName = null;
        this.projectName = null;
        this.allocationFrom = null;
        this.allocationTo = null;
        this.projectFrom = null;
        this.projectTo = null;
    }

    /**
     * Converts Excel Fields into Java Objects
     *
     * @param cell current
     */
    private void setCurrentField(final Cell cell) {

        // Reads cell type
        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Converts field into Java LocalDate Object
                    /*
                      TODO: Currently it can only handle the date format yyyy-MM-dd. Please add
                      more date formats for a better compatibility
                     */
                    this.localDate = LocalDate.parse(
                            new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue()));
                } else {
                    // Converts field into Double
                    this.doubleValue = cell.getNumericCellValue();
                }
                break;
            case STRING:
                // Converts field into String
                this.stringValue = cell.getStringCellValue();
                break;
            case BOOLEAN:
                // Converts field into Boolean
                this.booleanValue = cell.getBooleanCellValue();
                break;
            case FORMULA:
                // Converts field into String
                /*
                  TODO: Make it possible to evaluate field-formulas, so one can upload a sheet with
                  formulas
                 */
                this.formulaValue = cell.getCellFormula();
                break;
            default:
                break;
        }
    }

    /**
     * Converts a multipart file to File and saves it onto disk
     *
     * @param multipart uploaded File
     * @return locally saved File
     */
    private File multipartToFile(final MultipartFile multipart) throws IOException {

        /*
          TODO: Add the sources path.
         */
        // Creates the filepath with filename
        final AtomicReference<String> filePath = new AtomicReference<>(multipart.getOriginalFilename());

        // Creates new and empty file
        assert filePath.get() != null;
        AtomicReference<File> convFile = new AtomicReference<>(new File(filePath.get()));

        // Fills the file with content from multipart
        multipart.transferTo(Path.of(filePath.get()));

        // Saves the new File
        convFile.get().createNewFile();

        return convFile.get();
    }
}
