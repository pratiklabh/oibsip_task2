package com.prtk.task2_numguessing;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class GuessController {
    
    @GetMapping("/")
    public String getIndex() {
        return "Index";
    }

    @GetMapping("/newGame")
    public String newGame(HttpSession session) {
        // start a new game and resets score
        session.invalidate(); 
        return "redirect:/";
    }

    @PostMapping("/")
    public String postIndex(@RequestParam Integer number, Model model, HttpSession session) {
        if (number == null) {
            return "Index";
        }

        Random random = new Random();
        Integer num = (Integer) session.getAttribute("randomNumber");
        Integer attempts = (Integer) session.getAttribute("attempts");
        Integer score = (Integer) session.getAttribute("score");

        // Start a new game if one isn't already in progress or the previous one ended
        if (num == null || attempts == null || attempts <= 0) {
            num = random.nextInt(100);
            attempts = 6; // Reset attempts to 6
            session.setAttribute("randomNumber", num);
            session.setAttribute("attempts", attempts);
            if (score == null) {
                score = 0; // Initialize score if not set
                session.setAttribute("score", score);
            }
        }

        // Process of guessing
        if (num.equals(number)) {
            int points = attempts * 20; // Calculate score based on remaining attempts
            model.addAttribute("message", "Eureka! You've cracked it!! The number was " + num + ".");
            model.addAttribute("score", "Your score is " + points + " points!");
            session.invalidate(); // Game won, reset session
        } else {
            attempts--;
            session.setAttribute("attempts", attempts);
            if (attempts <= 0) {
                model.addAttribute("error", "And... cut! That's a wrap on your attempts. The number was " + num);
                session.invalidate(); // No attempts left, reset session
            } else {
                model.addAttribute("error", num > number ? "The secret number is greater than " + number : "The secret number is less than " + number);
                model.addAttribute("attempts", ". You have " + attempts + " attempts left.");
            }
        }

        return "Index";
    }
}
