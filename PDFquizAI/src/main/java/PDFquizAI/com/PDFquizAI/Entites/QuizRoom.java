package PDFquizAI.com.PDFquizAI.Entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Getter
@Setter
@Table(name = "quiz_rooms")
public class QuizRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================================
    // UNIQUE ROOM ID
    // EXAMPLE: QUIZ-A7F9K2
    // =========================================
    @Column(unique = true, nullable = false)
    private String roomId;

    // =========================================
    // MANY ROOMS -> ONE USER
    // =========================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String fileName;

    private String filePath;

    private String difficulty;

    private String level;

    @Column(length = 1000)
    private String categories;

    private String roomType;

    // ACTIVE / DESTROYED
    private boolean active = true;

    private String status = "PENDING";

    private LocalDateTime createdAt;

    // =========================================
    // AUTO CREATE ROOM ID
    // =========================================
    @PrePersist
    public void beforeSave() {

        createdAt = LocalDateTime.now();

        if (roomId == null || roomId.isEmpty()) {

            roomId = generateRoomId();
        }
    }

    // =========================================
    // RANDOM ALPHANUMERIC ROOM ID
    // =========================================
    private String generateRoomId() {

        String chars =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder builder =
                new StringBuilder("QUIZ-");

        Random random =
                new Random();

        for (int i = 0; i < 6; i++) {

            builder.append(
                    chars.charAt(
                            random.nextInt(chars.length())
                    )
            );
        }

        return builder.toString();
    }
}