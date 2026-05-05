package PDFquizAI.com.PDFquizAI.Services;

import PDFquizAI.com.PDFquizAI.Entites.User;
import PDFquizAI.com.PDFquizAI.Repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ GOOGLE LOGIN (CREATE OR UPDATE USER)
    public User saveGoogleUser(String email, String name, String googleId, String picture) {

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // 🔁 Update safe fields
            user.setName(name);
            user.setPictureUrl(picture);

            // only set googleId if not already set
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
            }

            return userRepository.save(user);
        }

        // 🆕 New user
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setGoogleId(googleId);
        user.setPictureUrl(picture);
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // ✅ STEP 2: COMPLETE PROFILE (PHONE + PASSWORD)
    public void completeProfile(String email, String phone, String rawPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 📱 Save phone
        user.setPhone(phone);

        // 🔐 Hash password
        String hashedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(hashedPassword);

        // ✅ Mark verified
        user.setVerified(true);

        userRepository.save(user);
    }

    // ==========================
    // LOGIN (EMAIL + PASSWORD)
    // ==========================
    public boolean login(String email, String rawPassword) {

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) return false;

        if (!user.isVerified()) return false;

        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}