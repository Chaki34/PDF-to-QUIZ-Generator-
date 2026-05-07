package PDFquizAI.com.PDFquizAI.Controllers;

import PDFquizAI.com.PDFquizAI.Entites.QuizRoom;
import PDFquizAI.com.PDFquizAI.Entites.User;
import PDFquizAI.com.PDFquizAI.Services.QuizRoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class QuizRoomController {

    private final QuizRoomService quizRoomService;

    public QuizRoomController(QuizRoomService quizRoomService) {
        this.quizRoomService = quizRoomService;
    }

    // =========================================
    // GENERATE SOLO QUIZ ROOM
    // =========================================
    @GetMapping("/generate-ai-quiz")
    public String generateAiQuiz(

            @RequestParam String file,
            @RequestParam String difficulty,
            @RequestParam String level,
            @RequestParam List<String> categories,

            HttpSession session
    ) {

        // LOGIN CHECK
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/auth";
        }

        User user =
                (User) session.getAttribute("loggedUser");

        // CREATE ROOM
        QuizRoom room = new QuizRoom();

        room.setUser(user);

        room.setFileName(file);

        room.setFilePath("/uploads/" + file);

        room.setDifficulty(difficulty);

        room.setLevel(level);

        room.setCategories(String.join(", ", categories));

        room.setRoomType("SOLO_AI");

        // SAVE
        QuizRoom savedRoom =
                quizRoomService.createRoom(room);

        // REDIRECT
        return "redirect:/ai-solo-quiz/" +
                savedRoom.getRoomId();
    }

    // =========================================
    // OPEN QUIZ ROOM
    // =========================================
    @GetMapping("/ai-solo-quiz/{roomId}")
    public String aiSoloQuiz(

            @PathVariable String roomId,
            Model model
    ) {

        QuizRoom room =
                quizRoomService.getRoom(roomId)
                        .orElseThrow();

        // BLOCK DESTROYED ROOM
        if (!room.isActive()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("room", room);

        return "ai-solo-quiz";
    }

    // =========================================
    // DESTROY ROOM
    // =========================================
    @GetMapping("/destroy-room/{roomId}")
    public String destroyRoom(
            @PathVariable String roomId
    ) {

        quizRoomService.destroyRoom(roomId);

        return "redirect:/dashboard";
    }
}