package PDFquizAI.com.PDFquizAI.Entites;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // USER
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // ROOM
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private QuizRoom room;

    private int totalQuestions;

    private int correctAnswers;

    private int wrongAnswers;

    private double score;

    private double accuracy;

    private long timeTakenSeconds;

    // ANALYTICS
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String analyticsJson;

    // USER ANSWERS
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String submittedAnswers;

    private LocalDateTime submittedAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdf_file_id")
    private PdfFile pdfFile;

    @PrePersist
    public void beforeSave() {

        submittedAt = LocalDateTime.now();
    }
}