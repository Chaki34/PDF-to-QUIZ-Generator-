package PDFquizAI.com.PDFquizAI.Controllers;

import PDFquizAI.com.PDFquizAI.Entites.DTOS.CompleteProfileRequest;
import PDFquizAI.com.PDFquizAI.Services.AuthService;
import PDFquizAI.com.PDFquizAI.Services.OtpService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    // ✅ LOAD PAGE
    @GetMapping("/auth")
    public String authView(HttpSession session, org.springframework.ui.Model model) {

        // ✅ IF ALREADY LOGGED IN → GO DASHBOARD
        if (session.getAttribute("email") != null &&
                session.getAttribute("loggedIn") != null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("email", session.getAttribute("email"));
        model.addAttribute("name", session.getAttribute("name"));
        model.addAttribute("picture", session.getAttribute("picture"));

        return "register-login";
    }


    // ✅ STEP 2 COMPLETE PROFILE
    @PostMapping("/complete-profile")
    @ResponseBody
    public String completeProfile(@RequestBody CompleteProfileRequest req,
                                  HttpSession session) {

        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "SESSION_EXPIRED";
        }

        authService.completeProfile(email, req.getPhone(), req.getPassword());

        return "SUCCESS";
    }


    @PostMapping("/send-otp")
    @ResponseBody
    public String sendOtp(HttpSession session) {

        String email = (String) session.getAttribute("email");

        if (email == null) return "SESSION_EXPIRED";

        otpService.generateAndSendOtp(email);

        return "OTP_SENT";
    }

    @PostMapping("/verify-otp")
    @ResponseBody
    public String verifyOtp(@RequestBody Map<String, String> req,
                            HttpSession session) {

        String email = (String) session.getAttribute("email");
        String otp = req.get("otp");

        if (email == null) return "SESSION_EXPIRED";

        boolean ok = otpService.verifyOtp(email, otp);

        return ok ? "VERIFIED" : "FAILED";
    }


    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestBody Map<String, String> req,
                        HttpSession session) {

        String email = req.get("email");
        String password = req.get("password");

        boolean ok = authService.login(email, password);

        if (!ok) {
            return "FAILED";
        }

        // ✅ VALID LOGIN → now user is fully authenticated
        session.setAttribute("email", email);
        session.setAttribute("loggedIn", true);

        // 🔥 IMPORTANT: onboarding completed
        session.setAttribute("onboarding", false);
        session.removeAttribute("onboardingStep");

        return "SUCCESS";
    }
}