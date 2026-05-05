package PDFquizAI.com.PDFquizAI.Services;

import PDFquizAI.com.PDFquizAI.Entites.OtpVerification;
import PDFquizAI.com.PDFquizAI.Repos.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;

    // =========================
    // GENERATE + SEND OTP
    // =========================
    public void generateAndSendOtp(String email) {

        String otp = generateOtp();

        OtpVerification otpEntity = otpRepository.findByEmail(email)
                .orElse(new OtpVerification());

        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(10));

        otpRepository.save(otpEntity);

        sendEmail(email, otp);
    }

    // =========================
    // 4 DIGIT OTP GENERATOR
    // =========================
    private String generateOtp() {
        return String.valueOf(1000 + new Random().nextInt(9000));
    }

    // =========================
    // SEND BEAUTIFUL HTML EMAIL
    // =========================
    private void sendEmail(String email, String otp) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("Pdf-To-Quiz AI <yourgmail@gmail.com>");
            helper.setTo(email);
            helper.setSubject("🔐 Your OTP Code - Pdf-To-Quiz AI");

            helper.setText(buildOtpEmailTemplate(otp), true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("OTP email failed", e);
        }
    }

    // =========================
    // HTML EMAIL TEMPLATE
    // =========================
    private String buildOtpEmailTemplate(String otp) {

        return """
        <div style="font-family:Arial;background:#f4f6ff;padding:30px;">
            <div style="max-width:520px;margin:auto;background:#fff;
                        border-radius:12px;overflow:hidden;
                        box-shadow:0 10px 30px rgba(0,0,0,0.1);">

                <div style="background:linear-gradient(135deg,#FF9933,#138808);
                            padding:25px;text-align:center;color:white;">

                    <h2>Pdf-To-Quiz AI</h2>
                    <p>Secure OTP Verification</p>
                </div>

                <div style="padding:30px;text-align:center;">
                    <h3 style="color:#000080;">Your OTP Code</h3>

                    <p>Use this OTP to continue. Valid for <b>10 minutes</b>.</p>

                    <div style="margin:25px 0;">
                        <span style="
                            font-size:32px;
                            font-weight:bold;
                            letter-spacing:8px;
                            padding:12px 24px;
                            background:#000080;
                            color:#fff;
                            border-radius:10px;
                            display:inline-block;">
                            """ + otp + """
                        </span>
                    </div>

                    <p style="color:red;font-size:12px;">
                        Never share this OTP with anyone.
                    </p>
                </div>

                <div style="background:#eee;padding:10px;
                            text-align:center;font-size:11px;">
                    © Pdf-To-Quiz AI
                </div>

            </div>
        </div>
        """;
    }

    // =========================
    // VERIFY OTP
    // =========================
    public boolean verifyOtp(String email, String inputOtp) {

        OtpVerification otpData = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (otpData.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!otpData.getOtp().trim().equals(inputOtp.trim())) {
            throw new RuntimeException("Invalid OTP");
        }

        otpRepository.delete(otpData);

        return true;
    }
}