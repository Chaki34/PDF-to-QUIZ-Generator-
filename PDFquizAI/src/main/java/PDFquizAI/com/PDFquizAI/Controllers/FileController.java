package PDFquizAI.com.PDFquizAI.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Controller
public class FileController {

    // ✅ Store outside resources (reliable)
    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public String uploadPdf(@RequestParam("pdfFile") MultipartFile file, Model model) {

        // 🔴 check empty
        if (file.isEmpty()) {
            model.addAttribute("error", "No file selected");
            return "index";
        }

        try {
            // ✅ clean filename
            String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String fileName = System.currentTimeMillis() + "_" + originalName;

            // ✅ create folder if not exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // ✅ final file path
            Path filePath = uploadPath.resolve(fileName);

            // ✅ save file (best method)
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 🔍 debug (VERY IMPORTANT)
            System.out.println("File saved at: " + filePath.toAbsolutePath());

            // ✅ redirect to viewer
            return "redirect:/viewer?file=" + fileName;

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Upload failed: " + e.getMessage());
            return "index";
        }
    }

    @GetMapping("/viewer")
    public String viewPdf(@RequestParam("file") String file, Model model) {

        // ✅ pass correct path to frontend
        model.addAttribute("filePath", "/uploads/" + file);

        return "quiz-viewer";
    }
}