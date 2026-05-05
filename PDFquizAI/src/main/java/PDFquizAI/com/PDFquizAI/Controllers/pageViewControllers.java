package PDFquizAI.com.PDFquizAI.Controllers;

import PDFquizAI.com.PDFquizAI.Entites.PdfFile;
import PDFquizAI.com.PDFquizAI.Services.PdfFileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class pageViewControllers {

  private final PdfFileService pdfFileService;

    public pageViewControllers(PdfFileService pdfFileService) {
        this.pdfFileService = pdfFileService;
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
  public String user_dashboardView(HttpSession session, org.springframework.ui.Model model) {

    // 🔐 protect route (only logged in users)
    if (session.getAttribute("email") == null) {
      return "redirect:/auth";
    }

    // 👇 pass session data to UI
    model.addAttribute("name", session.getAttribute("name"));
    model.addAttribute("email", session.getAttribute("email"));
    model.addAttribute("picture", session.getAttribute("picture"));

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

    Long userId = 1L; // replace with logged-in user later

    List<PdfFile> pdfList = pdfFileService.getUserFiles(userId);

    model.addAttribute("pdfList", pdfList);

    return "pdf-library";
  }


}
