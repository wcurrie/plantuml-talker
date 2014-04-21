package x;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

@Controller
@ComponentScan
@EnableAutoConfiguration
public class Main {

    @Autowired
    WordSmith wordSmith;

    Random random = new Random();

    List<String> lines = new ArrayList<>();

    private boolean bobToAlice = true;

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Main.class, args);
    }

    @RequestMapping(value = "/svg")
    public void svg(HttpServletResponse response) throws IOException, InterruptedException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream writer = response.getOutputStream();

        while (true) {
            int snoozeTime = random.nextInt(5) + 3;
            String chatLines = randomChatLines();
            chatLines += ("\nnote right: see you in " + snoozeTime + " seconds\n");
            String svg = makeSvg(chatLines, false);
            writer.println("event: update");
            writer.println("data: " + svg + "\n");
            writer.flush();
            Thread.sleep(snoozeTime * 1000);
        }
    }

    private String randomChatLines() {
        int moreLines = random.nextInt(10);
        for (int i = 0; i < moreLines; i++) {
            String talkers = bobToAlice ? "Bob -> Alice" : "Alice -> Bob";
            bobToAlice = !bobToAlice;
            lines.add(talkers + ": " + wordSmith.pickAName());
        }
        StringJoiner joiner = new StringJoiner("\n");
        lines.forEach(joiner::add);
        return joiner.toString();
    }

    private static String makeSvg(String source, String fileName) throws IOException {
        String svg = makeSvg(source, false);
        Files.write(new File(fileName).toPath(), svg.getBytes());
        return svg;
    }

    private static String makeSvg(String source, boolean pretty) throws IOException {
        source = "@startuml\n" + source + "\n@enduml\n";
        System.out.println("source = " + source);
        SourceStringReader reader = new SourceStringReader(source);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
        os.close();

        String svg = new String(os.toByteArray());
        if (pretty) {
            svg = svg.replaceAll(">", ">\n");
        }
        return svg;
    }
}
