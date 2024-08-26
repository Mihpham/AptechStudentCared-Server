package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmailGeneratorService {


    private final UserRepository userRepository;

    private String normalizeString(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String noDiacriticalMarks = pattern.matcher(normalized).replaceAll("");
        noDiacriticalMarks = noDiacriticalMarks.replaceAll("đ", "d");
        return noDiacriticalMarks;
    }

    private String generateEmail(String fullName, int randomNumber) {
        String normalizedFullName = normalizeString(fullName.toLowerCase());
        String[] nameParts = normalizedFullName.split("\\s+");
        if (nameParts.length < 2) {
            throw new IllegalArgumentException("Full name must contain at least first name and last name.");
        }

        String lastName = nameParts[nameParts.length - 1];
        String firstName = nameParts[0];
        String middleNameInitials = "";
        for (int i = 1; i < nameParts.length - 1; i++) {
            middleNameInitials += nameParts[i].substring(0, 1);
        }

        return String.format("%s.%s%s.%04d@aptechlearning.edu.vn",
                lastName,
                firstName.substring(0, 1),
                middleNameInitials,
                randomNumber);
    }

    public String generateUniqueEmail(String fullName) {
        String email;
        boolean unique = false;
        Random random = new Random();

        do {
            int randomNumber = random.nextInt(10000); // Sinh số từ 0 đến 9999
            email = generateEmail(fullName, randomNumber);
            unique = userRepository.findByEmail(email) == null; // Kiểm tra trùng lặp trong cơ sở dữ liệu
        } while (!unique);

        return email;
    }
}
