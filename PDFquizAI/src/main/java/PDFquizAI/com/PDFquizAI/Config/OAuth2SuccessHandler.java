package PDFquizAI.com.PDFquizAI.Config;

import PDFquizAI.com.PDFquizAI.Entites.User;
import PDFquizAI.com.PDFquizAI.Repos.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        // 🔐 Extract Google data safely
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");
        String googleId = oauthUser.getAttribute("sub");

        // 🚨 Safety check (VERY IMPORTANT)
        if (email == null) {
            throw new RuntimeException("Google OAuth failed: email is null");
        }

        // 🔍 Find or create user
        User user = userRepo.findByEmail(email).orElseGet(User::new);

        user.setEmail(email);
        user.setName(name != null ? name : "User");
        user.setPictureUrl(picture != null ? picture : "");
        user.setGoogleId(user.getGoogleId() == null ? googleId : user.getGoogleId());
        user.setVerified(false);

        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }

        // 💾 Save user in DB
        userRepo.save(user);


        // 🔐 SESSION SETUP
        HttpSession session = request.getSession();

// IMPORTANT
        session.setAttribute("loggedUser", user);

// USER DATA
        session.setAttribute("email", user.getEmail());

        session.setAttribute("name", user.getName());

        session.setAttribute("picture", user.getPictureUrl());

// LOGIN STATUS
        session.setAttribute("loggedIn", true);

// ONBOARDING
        session.setAttribute("onboarding", false);

        // 🔥 DEBUG LOG (remove later in production)
        System.out.println("✅ OAuth SUCCESS: " + email);

        // 🚀 Redirect to auth page (Step 2 UI will load correctly now)
        response.sendRedirect("/auth");
    }
}