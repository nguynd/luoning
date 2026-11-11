package com.yuddy.luoning.service;

import com.yuddy.luoning.dto.QuestionImportResponse;
import com.yuddy.luoning.entity.Course;
import com.yuddy.luoning.entity.ExamType;
import com.yuddy.luoning.entity.Question;
import com.yuddy.luoning.exception.ApiException;
import com.yuddy.luoning.repository.QuestionRepository;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

// Doc file .xlsx, kiem tra tung dong, neu khong co loi thi luu (them moi hoac cap nhat theo ma)
@Service
public class QuestionImportService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String LETTERS = "ABCDEF";
    private static final int MIN_DIFFICULTY = 1;
    private static final int MAX_DIFFICULTY = 3;

    // Vi tri cot trong file Excel (0 = cot A)
    private static final int COL_CODE = 0;
    private static final int COL_CONTENT = 1;
    private static final int COL_FIRST_OPTION = 2; // C..H la dap an A..F
    private static final int COL_MULTIPLE = 8;
    private static final int COL_ANSWER = 9;
    private static final int COL_DIFFICULTY = 10;
    private static final int COL_EXAM_TYPE = 11; // 0 = giua ky, 1 = cuoi ky, 2 = ca hai

    private final QuestionRepository questionRepository;
    private final CourseService courseService;

    public QuestionImportService(QuestionRepository questionRepository, CourseService courseService) {
        this.questionRepository = questionRepository;
        this.courseService = courseService;
    }

    // Mot dong da doc va hop le, chua luu
    private record ParsedQuestion(String code, String content, List<String> options,
            boolean multipleChoice, String correctAnswers, int difficulty, ExamType examType) {
    }

    // Loi cua mot dong, chi dung noi bo de gom thanh danh sach loi
    private static class RowError extends RuntimeException {
        RowError(String message) {
            super(message);
        }
    }

    @Transactional
    public QuestionImportResponse importQuestions(Long courseId, MultipartFile file, Long userId) {
        Course course = courseService.findOwnedCourse(courseId, userId);
        checkFile(file);

        List<ParsedQuestion> parsed = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        readRows(file, parsed, errors);

        // Co loi thi khong luu dong nao, de ngan hang khong bao gio "import duoc mot nua"
        if (!errors.isEmpty()) {
            throw ApiException.badRequest("File co " + errors.size() + " dong loi, chua luu cau hoi nao", errors);
        }
        if (parsed.isEmpty()) {
            throw ApiException.badRequest("File khong co cau hoi nao");
        }

        return save(course, parsed);
    }

    private void checkFile(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (file.isEmpty() || name == null || !name.toLowerCase().endsWith(".xlsx")) {
            throw ApiException.badRequest("Chi chap nhan file .xlsx");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw ApiException.badRequest("File toi da 5MB");
        }
    }

    private void readRows(MultipartFile file, List<ParsedQuestion> parsed, List<String> errors) {
        // try-with-resources: tu dong dong file du co loi hay khong
        try (InputStream in = file.getInputStream(); Workbook workbook = openWorkbook(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            // DataFormatter doc o dung nhu Excel hien thi: so 1 thanh "1" (khong phai "1.0")
            DataFormatter formatter = new DataFormatter();
            Set<String> codesInFile = new HashSet<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // dong 0 la tieu de
                Row row = sheet.getRow(i);
                if (isBlankRow(row, formatter)) {
                    continue;
                }
                try {
                    ParsedQuestion question = parseRow(row, formatter);
                    if (!codesInFile.add(question.code())) {
                        throw new RowError("ma " + question.code() + " bi trung voi mot dong khac trong file");
                    }
                    parsed.add(question);
                } catch (RowError e) {
                    errors.add("Dong " + (i + 1) + ": " + e.getMessage()); // i + 1 = so dong trong Excel
                }
            }
        } catch (IOException e) {
            // Loi doc/dong luong du lieu cua file upload
            throw ApiException.badRequest("Khong doc duoc file Excel");
        }
        // Loi khac (vi du NullPointerException do bug trong parseRow) KHONG bat o day:
        // de no thanh loi 500 va in stack trace, lap trinh vien moi biet ma sua
    }

    // Chi bat loi o dung cho mo file: file hong, khong phai Excel that, hoac bi doi duoi thanh .xlsx
    private Workbook openWorkbook(InputStream in) {
        try {
            return new XSSFWorkbook(in);
        } catch (IOException | RuntimeException e) {
            throw ApiException.badRequest("Khong doc duoc file Excel");
        }
    }

    private ParsedQuestion parseRow(Row row, DataFormatter formatter) {
        String code = cell(row, COL_CODE, formatter);
        String content = cell(row, COL_CONTENT, formatter);
        if (code.isEmpty()) {
            throw new RowError("thieu ma cau hoi");
        }
        if (code.length() > 50) {
            throw new RowError("ma cau hoi toi da 50 ky tu");
        }
        if (content.isEmpty()) {
            throw new RowError("thieu noi dung cau hoi");
        }
        if (content.length() > 2000) {
            throw new RowError("noi dung cau hoi toi da 2000 ky tu");
        }

        List<String> options = readOptions(row, formatter);

        String multiple = cell(row, COL_MULTIPLE, formatter);
        if (!multiple.equals("0") && !multiple.equals("1")) {
            throw new RowError("cot nhieu dap an phai la 0 hoac 1");
        }
        boolean multipleChoice = multiple.equals("1");

        String answers = normalizeAnswers(cell(row, COL_ANSWER, formatter), options.size());
        if (!multipleChoice && answers.length() != 1) {
            throw new RowError("cau mot dap an nhung co " + answers.length() + " dap an dung (" + answers + ")");
        }

        int difficulty = parseDifficulty(cell(row, COL_DIFFICULTY, formatter));
        ExamType examType = parseExamType(cell(row, COL_EXAM_TYPE, formatter));

        return new ParsedQuestion(code, content, options, multipleChoice, answers, difficulty, examType);
    }

    // Dap an phai lien nhau tu A: A, B bat buoc; o trong roi thi cac o sau cung phai trong
    private List<String> readOptions(Row row, DataFormatter formatter) {
        List<String> options = new ArrayList<>();
        boolean emptySeen = false;
        for (int k = 0; k < Question.MAX_OPTIONS; k++) {
            String option = cell(row, COL_FIRST_OPTION + k, formatter);
            char letter = LETTERS.charAt(k);
            if (option.isEmpty()) {
                emptySeen = true;
                continue;
            }
            if (emptySeen) {
                throw new RowError("dap an " + letter + " co noi dung nhung dap an truoc no bi trong");
            }
            if (option.length() > 1000) {
                throw new RowError("dap an " + letter + " toi da 1000 ky tu");
            }
            options.add(option);
        }
        if (options.size() < 2) {
            throw new RowError("can it nhat dap an A va B");
        }
        return options;
    }

    // "ca" -> "AC"; moi chu phai ung voi mot dap an co noi dung
    private String normalizeAnswers(String raw, int optionCount) {
        String value = raw.toUpperCase().replace(" ", "");
        if (value.isEmpty()) {
            throw new RowError("thieu dap an dung");
        }
        TreeSet<Character> letters = new TreeSet<>(); // TreeSet tu sap xep A..F
        for (char c : value.toCharArray()) {
            int index = LETTERS.indexOf(c);
            if (index < 0) {
                throw new RowError("dap an dung '" + raw + "' chi duoc gom cac chu A-F viet lien, vi du AC");
            }
            if (index >= optionCount) {
                throw new RowError("dap an dung " + c + " khong co noi dung (cau nay chi co "
                        + optionCount + " dap an A-" + LETTERS.charAt(optionCount - 1) + ")");
            }
            if (!letters.add(c)) {
                throw new RowError("dap an dung " + c + " bi lap");
            }
        }
        StringBuilder sb = new StringBuilder();
        letters.forEach(sb::append);
        return sb.toString();
    }

    private int parseDifficulty(String raw) {
        try {
            int value = Integer.parseInt(raw);
            if (value >= MIN_DIFFICULTY && value <= MAX_DIFFICULTY) {
                return value;
            }
        } catch (NumberFormatException ignored) {
            // roi xuong throw ben duoi
        }
        throw new RowError("muc do phai la so nguyen tu " + MIN_DIFFICULTY + " den " + MAX_DIFFICULTY);
    }

    // switch dang moi (Java 14+): moi nhanh tra ve mot gia tri, default bat moi gia tri con lai
    private ExamType parseExamType(String raw) {
        return switch (raw) {
            case "0" -> ExamType.MIDTERM;
            case "1" -> ExamType.FINAL;
            case "2" -> ExamType.BOTH;
            default -> throw new RowError("loai de phai la 0 (giua ky), 1 (cuoi ky) hoac 2 (ca hai)");
        };
    }

    private QuestionImportResponse save(Course course, List<ParsedQuestion> parsed) {
        // Lay het cau cu cua khoa vao Map mot lan, thay vi moi dong mot cau SQL (tranh N+1)
        Map<String, Question> existing = new HashMap<>();
        for (Question q : questionRepository.findByCourseIdOrderByCodeAsc(course.getId())) {
            existing.put(q.getCode(), q);
        }

        int created = 0;
        int updated = 0;
        List<Question> toSave = new ArrayList<>();
        for (ParsedQuestion p : parsed) {
            Question question = existing.get(p.code());
            if (question == null) {
                question = new Question(course, p.code());
                created++;
            } else {
                updated++;
            }
            question.setContent(p.content());
            question.setOptions(p.options());
            question.setMultipleChoice(p.multipleChoice());
            question.setCorrectAnswers(p.correctAnswers());
            question.setDifficulty(p.difficulty());
            question.setExamType(p.examType());
            toSave.add(question);
        }
        questionRepository.saveAll(toSave);
        return new QuestionImportResponse(created, updated);
    }

    private static String cell(Row row, int col, DataFormatter formatter) {
        return formatter.formatCellValue(row.getCell(col)).trim();
    }

    private static boolean isBlankRow(Row row, DataFormatter formatter) {
        if (row == null) {
            return true;
        }
        for (int col = COL_CODE; col <= COL_EXAM_TYPE; col++) {
            if (!cell(row, col, formatter).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
