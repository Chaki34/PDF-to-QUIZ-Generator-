package PDFquizAI.com.PDFquizAI.Controllers;


import PDFquizAI.com.PDFquizAI.Entites.PdfFile;
import PDFquizAI.com.PDFquizAI.Services.PdfFileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@Controller
public class FileController {

    private static final String UPLOAD_DIR = "uploads/";

    private final PdfFileService pdfFileService;

    public FileController(PdfFileService pdfFileService) {
        this.pdfFileService = pdfFileService;
    }

    @PostMapping("/upload")
    public String uploadPdf(@RequestParam("pdfFile") MultipartFile file,
                            Model model) {

        if (file.isEmpty()) {
            model.addAttribute("error", "No file selected");
            return "index";
        }

        try {
            // file names
            String originalName = file.getOriginalFilename();
            String fileName = System.currentTimeMillis() + "_" + originalName;

            // create folder
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);

            // save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 🧠 SAVE TO DATABASE (IMPORTANT PART)
            PdfFile pdf = new PdfFile();
            pdf.setFileName(fileName);
            pdf.setOriginalName(originalName);
            pdf.setFilePath(filePath.toString());

            // 🔐 TEMP USER ID (replace with login user later)
            Long userId = 1L; // 👉 replace with Spring Security user
            pdf.setUserId(userId);

            pdf.setUploadedAt(LocalDateTime.now());

            pdfFileService.save(pdf);

            System.out.println("Saved file + DB record: " + fileName);

            return "redirect:/viewer?file=" + fileName;

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Upload failed: " + e.getMessage());
            return "index";
        }
    }

    @GetMapping("/viewer")
    public String viewPdf(@RequestParam("file") String file, Model model) {
        model.addAttribute("filePath", "/uploads/" + file);
        return "quiz-viewer";
    }
}