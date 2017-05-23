package resolver.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import resolver.annotation.ExternalProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by viacheslav on 5/22/17.
 */
@ConditionalOnProperty(prefix = "service", name = "enabled")
@ConfigurationProperties(prefix = "service")
@Service
public class EnglishWordService implements WordService{

    @ExternalProperty
    private String words;

    public EnglishWordService() {
    }

    @Override
    public String getWord() {
        List<String> allWords = Arrays.asList(words.split(", "));
        Random random = new Random();
        int randomIndex = random.nextInt(allWords.size() - 1);
        return allWords.get(randomIndex);
    }

    public void setWords(String words) {
        this.words = words;
    }
}
