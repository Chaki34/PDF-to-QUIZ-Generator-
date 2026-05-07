package PDFquizAI.com.PDFquizAI.Controllers;

import PDFquizAI.com.PDFquizAI.Entites.PdfFile;
import PDFquizAI.com.PDFquizAI.Entites.QuizAttempt;
import PDFquizAI.com.PDFquizAI.Entites.User;
import PDFquizAI.com.PDFquizAI.Repos.UserRepository;
import PDFquizAI.com.PDFquizAI.Services.PdfFileService;
import PDFquizAI.com.PDFquizAI.Services.QuizAttemptService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class pageViewControllers {

  private final PdfFileService pdfFileService;

  private final QuizAttemptService  quizAttemptService;

  private final UserRepository  userRepository;

    public pageViewControllers(PdfFileService pdfFileService, QuizAttemptService quizAttemptService, UserRepository userRepository) {
        this.pdfFileService = pdfFileService;
        this.quizAttemptService = quizAttemptService;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
  public String  homeView(){
      return "home";
  }

  @GetMapping("/gateway")
    public String  quizgatewayView(){
      return "quizgateway";
    }

  @GetMapping("/dashboard")
  public String user_dashboardView(HttpSession session,
                                   org.springframework.ui.Model model) {

    // 🔐 protect route
    String email = (String) session.getAttribute("email");
    if (email == null) {
      return "redirect:/auth";
    }

    // 🔥 FETCH USER FROM DB
    User user = userRepository.findByEmail(email)
            .orElse(null);

    if (user == null) {
      return "redirect:/auth";
    }

    // 🔥 GET RECENT QUIZ ATTEMPTS (latest first recommended in service)
    List<QuizAttempt> recentAttempts =
            quizAttemptService.getRecentByUser(user.getId());

    // ===============================
    // 📊 STATS CALCULATION
    // ===============================

    int totalQuizzes = recentAttempts.size();

    double avgScore = recentAttempts.stream()
            .mapToDouble(QuizAttempt::getScore)
            .average()
            .orElse(0.0);

    long totalSeconds = recentAttempts.stream()
            .mapToLong(QuizAttempt::getTimeTakenSeconds)
            .sum();

    long studyMinutes = totalSeconds / 60;

    int correctAnswers = recentAttempts.stream()
            .mapToInt(QuizAttempt::getCorrectAnswers)
            .sum();

    int wrongAnswers = recentAttempts.stream()
            .mapToInt(QuizAttempt::getWrongAnswers)
            .sum();

    double accuracy = (correctAnswers + wrongAnswers) == 0
            ? 0.0
            : (correctAnswers * 100.0 / (correctAnswers + wrongAnswers));

    int streak = quizAttemptService.calculateStreak(recentAttempts);

    // ===============================

    // 👇 PASS DATA TO UI
    model.addAttribute("name", session.getAttribute("name"));
    model.addAttribute("email", email);
    model.addAttribute("picture", session.getAttribute("picture"));

    model.addAttribute("recentAttempts", recentAttempts);

    // 📊 STATS
    model.addAttribute("totalQuizzes", totalQuizzes);
    model.addAttribute("avgScore", Math.round(avgScore));
    model.addAttribute("studyMinutes", studyMinutes);
    model.addAttribute("accuracy", Math.round(accuracy));
    model.addAttribute("streak", streak);

    return "student-dashboard";
  }

  @GetMapping("/collections")
  public String  user_collectionsView(){
    return "user-collections";
  }

  @GetMapping("/analatics")
  public String  user_AnalaticsView(){
    return "user-analatics";
  }

  @GetMapping("/library")
  public String pdfLibrary(Model model) {

    Long userId = 1L;

    List<PdfFile> pdfList = pdfFileService.getUserFiles(userId);

    // NEW: fetch all attempts for user
    List<QuizAttempt> attempts = quizAttemptService.getRecentByUser(userId);

    model.addAttribute("pdfList", pdfList);
    model.addAttribute("attempts", attempts);

    return "pdf-library";
  }

  @GetMapping("/room-types")
  public String roomtypeView(

          @RequestParam String file,
          @RequestParam String difficulty,
          @RequestParam String level,
          @RequestParam List<String> categories,

          HttpSession session,
          Model model) {

    // protect route
    if (session.getAttribute("email") == null) {
      return "redirect:/auth";
    }

    // USER DETAILS
    model.addAttribute("name", session.getAttribute("name"));
    model.addAttribute("email", session.getAttribute("email"));
    model.addAttribute("picture", session.getAttribute("picture"));

    // PDF
    model.addAttribute("filePath", "/uploads/" + file);
    model.addAttribute("fileName", file);

    // QUIZ DETAILS
    model.addAttribute("difficulty", difficulty);
    model.addAttribute("level", level);
    model.addAttribute("categories", categories);

    return "select-quiz-room-type";
  }



}
