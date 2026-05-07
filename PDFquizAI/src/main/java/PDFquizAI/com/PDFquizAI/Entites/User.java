package PDFquizAI.com.PDFquizAI.Entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String phone;

    private String password;

    private String googleId;

    private String pictureUrl;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    private LocalDateTime createdAt;

    // ONE USER -> MANY QUIZ ROOMS
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<QuizRoom> quizRooms = new ArrayList<>();
}