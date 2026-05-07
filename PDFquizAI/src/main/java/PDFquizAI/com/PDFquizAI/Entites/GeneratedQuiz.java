package PDFquizAI.com.PDFquizAI.Entites;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "generated_quizzes")
public class GeneratedQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ROOM LINK
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private QuizRoom room;

    // JSON QUESTIONS
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String quizJson;

    // RAW AI RESPONSE
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String aiResponse;

    private int totalQuestions;

    private LocalDateTime createdAt;

    @PrePersist
    public void beforeSave() {

        createdAt = LocalDateTime.now();
    }
}