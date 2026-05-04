package PDFquizAI.com.PDFquizAI.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class pageViewControllers {

  @GetMapping("/")
  public String  homeView(){
      return "home";
  }

  @GetMapping("/gateway")
    public String  quizgatewayView(){
      return "quizgateway";
    }

  @GetMapping("/student-dashbaord")
  public String  user_dashboardView(){
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


}
