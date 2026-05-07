package PDFquizAI.com.PDFquizAI.Controllers;

import PDFquizAI.com.PDFquizAI.Entites.GeneratedQuiz;
import PDFquizAI.com.PDFquizAI.Entites.QuizAttempt;
import PDFquizAI.com.PDFquizAI.Entites.QuizRoom;
import PDFquizAI.com.PDFquizAI.Entites.User;
import PDFquizAI.com.PDFquizAI.Repos.GeneratedQuizRepository;
import PDFquizAI.com.PDFquizAI.Services.AIQuizService;
import PDFquizAI.com.PDFquizAI.Services.QuizAttemptService;
import PDFquizAI.com.PDFquizAI.Services.QuizRoomService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AIQuizController {

    private final AIQuizService aiQuizService;
    private final QuizRoomService quizRoomService;
    private final GeneratedQuizRepository generatedQuizRepository;
    private final QuizAttemptService quizAttemptService;

    // =========================
    // START GENERATION ONLY
    // =========================
    @GetMapping("/start-ai-quiz/{roomId}")
    public String startAIQuiz(@PathVariable String roomId,
                              HttpSession session,
                              Model model) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/auth";

        QuizRoom room = quizRoomService.getRoom(roomId).orElse(null);
        if (room == null) return "redirect:/dashboard";

        if (!room.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }

        // START GENERATION ONLY
        aiQuizService.generateQuizAsync(roomId);

        // DO NOT REDIRECT TO PLAY PAGE
        model.addAttribute("roomId", roomId);
        return "quiz-loading";
    }

    // =========================
    // PLAY PAGE
    // =========================
    @GetMapping("/play-quiz/{roomId}")
    public String playQuiz(@PathVariable String roomId,
                           HttpSession session,
                           Model model) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/auth";

        QuizRoom room = quizRoomService.getRoom(roomId).orElse(null);
        if (room == null) return "redirect:/dashboard";

        GeneratedQuiz quiz = generatedQuizRepository.findByRoom(room).orElse(null);

        // NOT READY → stay in loading
        if (quiz == null) {
            model.addAttribute("roomId", roomId);
            return "quiz-loading";
        }

        model.addAttribute("room", room);
        model.addAttribute("quizJson", quiz.getQuizJson());

        return "quiz-page";
    }

    // =========================
    // STATUS API (IMPORTANT)
    // =========================
    @GetMapping("/api/quiz/status/{roomId}")
    @ResponseBody
    public Map<String, String> status(@PathVariable String roomId) {

        QuizRoom room = quizRoomService.getRoom(roomId).orElse(null);

        if (room == null) {
            return Map.of("status", "NOT_FOUND");
        }

        boolean ready = generatedQuizRepository.findByRoom(room).isPresent();

        return Map.of(
                "status",
                ready ? "READY" : "GENERATING"
        );
    }


    @PostMapping("/submit-ai-quiz/{roomId}")
    @ResponseBody
    public Map<String, Object> submitQuiz(@PathVariable String roomId,
                                          @RequestBody Map<String, Object> payload,
                                          HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return Map.of("status", "FAILED");

        try {
            QuizRoom room = quizRoomService.getRoom(roomId).orElseThrow();

            GeneratedQuiz quiz = generatedQuizRepository.findByRoom(room).orElseThrow();

            Map<String, String> userAnswers =
                    (Map<String, String>) payload.get("answers");

            long timeTaken = ((Number) payload.getOrDefault("timeTaken", 0)).longValue();

            // parse quiz JSON
            JSONArray questions = new JSONArray(quiz.getQuizJson());

            int correct = 0;

            JSONArray review = new JSONArray();

            for (int i = 0; i < questions.length(); i++) {

                JSONObject q = questions.getJSONObject(i);

                String correctAnswer = q.getString("answer");
                String userAnswer = userAnswers.get(String.valueOf(i));

                boolean isCorrect = correctAnswer.equals(userAnswer);

                if (isCorrect) correct++;

                JSONObject r = new JSONObject();
                r.put("question", q.getString("question"));
                r.put("userAnswer", userAnswer);
                r.put("correctAnswer", correctAnswer);
                r.put("explanation", q.optString("explanation"));
                r.put("isCorrect", isCorrect);

                review.put(r);
            }

            int total = questions.length();
            int wrong = total - correct;
            double score = (correct * 100.0) / total;

            // save attempt
            QuizAttempt attempt = quizAttemptService.submitQuiz(
                    user,
                    room,
                    total,
                    correct,
                    timeTaken,
                    userAnswers.toString(),
                    review.toString()
            );

// return result to frontend
            return Map.of(
                    "status", "SUCCESS",
                    "score", score,
                    "correct", correct,
                    "wrong", wrong,
                    "review", review.toList(),
                    "attemptId", attempt.getId()   // 🔥 THIS IS THE FIX
            );

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("status", "FAILED");
        }
    }

    @GetMapping("/quiz-result/{attemptId}")
    public String quizResult(@PathVariable Long attemptId,
                             Model model,
                             HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/auth";

        QuizAttempt attempt = quizAttemptService.getById(attemptId);

        JSONArray reviewArray = new JSONArray(attempt.getAnalyticsJson());

        model.addAttribute("attempt", attempt);
        model.addAttribute("room", attempt.getRoom());
        model.addAttribute("review", reviewArray.toList()); // ✅ IMPORTANT FIX

        return "quiz-result";
    }


    // =========================
// CLOSE ROOM (DELETE ONLY QUESTIONS)
// =========================
    @GetMapping("/quiz/store/{roomId}")
    public String store(@PathVariable String roomId) {
        // DO NOTHING (keep data)
        return "redirect:/dashboard";
    }


    @GetMapping("/quiz/close/{roomId}")
    public String close(@PathVariable String roomId,
                        HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/auth";

        QuizRoom room = quizRoomService.getRoom(roomId).orElse(null);
        if (room == null) return "redirect:/dashboard";

        if (!room.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }

        // 🔥 DELETE ONLY QUESTIONS (generated_quizzes)
        generatedQuizRepository.deleteByRoom(room);

        return "redirect:/dashboard";
    }




}