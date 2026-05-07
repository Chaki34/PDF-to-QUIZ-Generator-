package PDFquizAI.com.PDFquizAI.Services;

import PDFquizAI.com.PDFquizAI.Entites.*;
import PDFquizAI.com.PDFquizAI.Repos.QuizAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private static final Logger log =
            LoggerFactory.getLogger(QuizAttemptService.class);

    private final QuizAttemptRepository attemptRepository;

    private final QuizAttemptRepository  quizAttemptRepository;

    public QuizAttempt submitQuiz(

            User user,
            QuizRoom room,

            int totalQuestions,
            int correctAnswers,
            long timeTaken,

            String submittedAnswers,
            String analyticsJson
    ) {

        log.info("=== QUIZ SUBMIT DEBUG START ===");
        log.info("User: {}", user.getId());
        log.info("Room: {}", room.getRoomId());
        log.info("Total Questions: {}", totalQuestions);
        log.info("Correct Answers: {}", correctAnswers);
        log.info("Time Taken: {}", timeTaken);

        int wrongAnswers = totalQuestions - correctAnswers;

        double accuracy = totalQuestions == 0
                ? 0
                : ((double) correctAnswers / totalQuestions) * 100;

        double score = correctAnswers * 10;

        log.info("Wrong Answers: {}", wrongAnswers);
        log.info("Accuracy: {}", accuracy);
        log.info("Score: {}", score);

        QuizAttempt attempt = new QuizAttempt();

        attempt.setUser(user);
        attempt.setRoom(room);
        attempt.setTotalQuestions(totalQuestions);
        attempt.setCorrectAnswers(correctAnswers);
        attempt.setWrongAnswers(wrongAnswers);
        attempt.setAccuracy(accuracy);
        attempt.setScore(score);
        attempt.setTimeTakenSeconds(timeTaken);
        attempt.setSubmittedAnswers(submittedAnswers);
        attempt.setAnalyticsJson(analyticsJson);

        QuizAttempt saved = attemptRepository.save(attempt);

        log.info("QuizAttempt SAVED with ID: {}", saved.getId());
        log.info("=== QUIZ SUBMIT DEBUG END ===");



        return saved;
    }

    public QuizAttempt getById(Long id) {
        return attemptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz Attempt not found: " + id));
    }

    public List<QuizAttempt> getRecentByUser(Long userId) {
        return quizAttemptRepository.findTop10ByUserIdOrderBySubmittedAtDesc(userId);
    }

    public int calculateStreak(List<QuizAttempt> attempts) {

        if (attempts.isEmpty()) return 0;

        attempts.sort((a, b) -> b.getSubmittedAt().compareTo(a.getSubmittedAt()));

        int streak = 1;

        LocalDate prevDate = attempts.get(0).getSubmittedAt().toLocalDate();

        for (int i = 1; i < attempts.size(); i++) {

            LocalDate current = attempts.get(i).getSubmittedAt().toLocalDate();

            if (prevDate.minusDays(1).equals(current) ||
                    prevDate.equals(current)) {
                streak++;
                prevDate = current;
            } else {
                break;
            }
        }

        return streak;
    }
}