package PDFquizAI.com.PDFquizAI.Services;

import PDFquizAI.com.PDFquizAI.Entites.GeneratedQuiz;
import PDFquizAI.com.PDFquizAI.Entites.QuizRoom;
import PDFquizAI.com.PDFquizAI.Repos.GeneratedQuizRepository;
import PDFquizAI.com.PDFquizAI.Repos.QuizRoomRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AIQuizService {

    private static final Logger log =
            LoggerFactory.getLogger(AIQuizService.class);

    private final ChatClient chatClient;
    private final QuizRoomRepository quizRoomRepository;
    private final GeneratedQuizRepository generatedQuizRepository;

    // ===============================
    // ASYNC QUIZ GENERATION
    // ===============================
    @Async
    @Transactional
    public void generateQuizAsync(String roomId) {

        log.info("🔥 AI QUIZ GENERATION START roomId={}", roomId);

        try {
            // ===============================
            // 1. LOAD ROOM SAFELY
            // ===============================
            QuizRoom room = quizRoomRepository.findByRoomId(roomId)
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            log.info("📦 ROOM LOADED id={} file={}",
                    room.getRoomId(), room.getFileName());

            // ===============================
            // 2. UPDATE STATUS (IMPORTANT)
            // ===============================
            room.setStatus("GENERATING");
            quizRoomRepository.saveAndFlush(room);

            // ===============================
            // 3. BUILD PROMPT
            // ===============================
            String prompt = buildPrompt(room);

            log.info("📤 PROMPT SENT TO AI");

            // ===============================
            // 4. CALL AI (OPENROUTER via ChatClient)
            // ===============================
            String aiResponse = chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();

            if (aiResponse == null || aiResponse.isBlank()) {
                throw new RuntimeException("Empty AI response");
            }

            log.info("📥 AI RESPONSE RECEIVED");

            // ===============================
            // 5. SAVE QUIZ
            // ===============================
            GeneratedQuiz quiz = new GeneratedQuiz();

            quiz.setRoom(room);
            quiz.setQuizJson(aiResponse);
            quiz.setAiResponse(aiResponse);
            quiz.setTotalQuestions(15);

            generatedQuizRepository.save(quiz);

            log.info("💾 QUIZ SAVED SUCCESSFULLY");

            // ===============================
            // 6. MARK READY
            // ===============================
            room.setStatus("READY");
            quizRoomRepository.save(room);

            log.info("✅ QUIZ GENERATION COMPLETE roomId={}", roomId);

        } catch (Exception e) {

            log.error("❌ AI GENERATION FAILED roomId={}", roomId, e);

            try {
                quizRoomRepository.findByRoomId(roomId).ifPresent(room -> {
                    room.setStatus("FAILED");
                    quizRoomRepository.save(room);
                });
            } catch (Exception ex) {
                log.error("❌ FAILED TO UPDATE ROOM STATUS", ex);
            }
        }
    }

    // ===============================
    // PROMPT BUILDER (CLEAN)
    // ===============================
    private String buildPrompt(QuizRoom room) {

        return """
    You are a strict JSON generator.

    RULES (VERY IMPORTANT):
    - Return ONLY valid JSON array
    - NO markdown
    - NO explanation
    - NO text before or after JSON
    - NO ``` fences
    - MUST be valid parsable JSON

    OUTPUT FORMAT:
    [
      {
        "question": "string",
        "options": ["A", "B", "C", "D"],
        "answer": "A",
        "explanation": "short explanation"
      }
    ]

    GENERATE EXACTLY 5 QUESTIONS.

    CONTEXT:
    File: %s
    Difficulty: %s
    Level: %s
    Categories: %s
    """.formatted(
                room.getFileName(),
                room.getDifficulty(),
                room.getLevel(),
                room.getCategories()
        );
    }
}