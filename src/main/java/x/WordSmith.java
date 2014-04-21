package x;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class WordSmith {

    private final List<String> words;
    private final Random random;

    public WordSmith() throws IOException {
        try (Stream<String> lines = Files.lines(new File("/usr/share/dict/words").toPath())) {
            words = lines.filter(l -> l.length() > 5).collect(Collectors.toList());
        }
        random = new Random();
    }

    public String pickAName() {
        StringBuilder s = new StringBuilder();
        int wordCount = random.nextInt(3) + 2;
        random.ints(wordCount, 0, words.size()).mapToObj(words::get).forEach(word -> {
            if (s.length() > 0) {
                s.append(' ');
            }
            s.append(word);
        });
        return s.toString();
    }

}
