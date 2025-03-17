package com.Series.SeriesAPI.Controllers;

import com.Series.SeriesAPI.Auth.Entities.ForgotPassword;
import com.Series.SeriesAPI.Auth.Entities.User;
import com.Series.SeriesAPI.Auth.Repository.ForgotPasswordRepository;
import com.Series.SeriesAPI.Auth.Repository.UserRepository;
import com.Series.SeriesAPI.Auth.Utils.ChangePassword;
import com.Series.SeriesAPI.DTO.MailBody;
import com.Series.SeriesAPI.Service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Integer otpGenerator(){

        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }

    @PostMapping("verifyMail/{email}")
    public ResponseEntity<String> verifyMail(@PathVariable String email){

        // get user object associated with the given mail
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid mail"));

        // extract the otp from the otp generator and build the mailBody object
        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your forgot password request: " + otp)
                .subject("Forgot Password OTP")
                .build();

        // if the user want to request for another otp, we check if the user already has an otp before,
        // so that we can generate a new one and update the db, else we get a sql duplicate entry exception
        Optional<ForgotPassword> existingOtp = forgotPasswordRepository.findByUser(user);
        if(existingOtp.isPresent()){

            // Update existing OTP and expiration time
            ForgotPassword forgotPassword = existingOtp.get();
            forgotPassword.setOtp(otp);
            forgotPassword.setExpirationTime(new Date(System.currentTimeMillis() + 70 * 10000));
            forgotPasswordRepository.save(forgotPassword);
        }
        else {

            // Since we are sending the otp, we need to build it and save the otp in the forgot password db
            ForgotPassword fp = ForgotPassword.builder()
                    .otp(otp)
                    .expirationTime(new Date(System.currentTimeMillis() + 70 * 10000)) // (70 * 1000 is 1 min and 10 sec(1000mills = 1 sec. 1 sec * 70 sec = 70 sec, = 1 min and 10 sec))
                    .user(user)
                    .build();
            // save the forgot password object(fp) into the db
            forgotPasswordRepository.save(fp);
        }



        // add emailService and send mail
        emailService.sendSimpleMessage(mailBody);

        return ResponseEntity.ok("Email sent for verification");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp,
                                            @PathVariable String email) {

        // check if user exist or not
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid mail"));

        // check if the given user and the otp exist in the db
        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email " + email));

        // check if the otp is valid(expired or not)
        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getFpId());
            return new ResponseEntity<>("OTP has Expired", HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("OTP Verified");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@PathVariable String email,
                                                        @RequestBody ChangePassword changePassword) {

        // validate that the password and repeatPassword are equal
        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword())){
            return new ResponseEntity<>("Password does not match..", HttpStatus.EXPECTATION_FAILED);
        }

        // If both are equal, we need to encode the password and then save it to the user object related to the email passed
        String encodedPassword = passwordEncoder.encode(changePassword.password());

        // Now we can call the update method from the userRepository and save the encoded password.
        userRepository.updatePassword(email, encodedPassword);
        return ResponseEntity.ok("Password changed successfully");
    }
}
