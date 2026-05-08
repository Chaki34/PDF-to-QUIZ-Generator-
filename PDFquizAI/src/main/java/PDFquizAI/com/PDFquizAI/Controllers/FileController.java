package PDFquizAI.com.PDFquizAI.Controllers;


import PDFquizAI.com.PDFquizAI.Entites.PdfFile;
import PDFquizAI.com.PDFquizAI.Entites.User;
import PDFquizAI.com.PDFquizAI.Repos.UserRepository;
import PDFquizAI.com.PDFquizAI.Services.PdfFileService;
import jakarta.servlet.http.HttpSession;
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

    private final UserRepository userRepository;

    public FileController(PdfFileService pdfFileService, UserRepository userRepository) {
        this.pdfFileService = pdfFileService;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    public String uploadPdf(@RequestParam("pdfFile") MultipartFile file,
                            HttpSession session,
                            Model model) {

        if (file.isEmpty()) {
            model.addAttribute("error", "No file selected");
            return "index";
        }

        try {
            // 🔐 GET USER FROM SESSION
            String email = (String) session.getAttribute("email");

            if (email == null) {
                return "redirect:/auth";
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

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

            // 🧠 SAVE TO DATABASE
            PdfFile pdf = new PdfFile();
            pdf.setFileName(fileName);
            pdf.setOriginalName(originalName);
            pdf.setFilePath(filePath.toString());

            // 🔥 FIX HERE (IMPORTANT)
            pdf.setUser(user);   // ✅ correct mapping

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
    public String viewPdf(
            @RequestParam("file") String file,
            HttpSession session,
            Model model) {

        // protect route
        if (session.getAttribute("email") == null) {
            return "redirect:/auth";
        }

        // pdf path
        model.addAttribute("filePath", "/uploads/" + file);

        // user details
        model.addAttribute("name", session.getAttribute("name"));
        model.addAttribute("email", session.getAttribute("email"));
        model.addAttribute("picture", session.getAttribute("picture"));

        return "quiz-viewer";
    }
}