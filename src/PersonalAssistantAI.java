import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Persönlicher Chatbot mit Charaktereigenschaften
 * Kann Fragen beantworten und Ratschläge geben
 */
public class PersonalAssistantAI {
    private String name;
    private Map<String, List<String>> knowledgeBase;
    private Map<String, String> personalResponses;
    private Random random;
    private Scanner scanner;
    private List<String> greetings;
    private List<String> farewells;
    private List<String> unknownResponses;
    private List<String> commonQuestions;
    private int mood; // 0-10, 0 = schlecht gelaunt, 10 = sehr gut gelaunt

    public static void main(String[] args) {
        PersonalAssistantAI assistant = new PersonalAssistantAI("Eva");
        assistant.startConversation();
    }

    public PersonalAssistantAI(String name) {
        this.name = name;
        this.random = new Random();
        this.scanner = new Scanner(System.in);
        this.mood = 7; // Standardmäßig gute Laune
        initializeKnowledgeBase();
        initializePersonalResponses();
        initializePhrases();
    }

    private void initializeKnowledgeBase() {
        knowledgeBase = new HashMap<>();

        // Allgemeines Wissen
        knowledgeBase.put("wetter", Arrays.asList(
                "Das Wetter kann sich schnell ändern. Schau am besten aus dem Fenster!",
                "Für eine genaue Wettervorhersage empfehle ich, eine Wetter-App zu nutzen.",
                "Wetter ist manchmal unberechenbar, aber eine gute Jacke schadet nie!"
        ));

        knowledgeBase.put("zeit", Arrays.asList(
                "Die aktuelle Zeit ist " + getCurrentTime() + ".",
                "Es ist jetzt " + getCurrentTime() + ". Die Zeit vergeht wie im Flug, nicht wahr?",
                "Laut meiner internen Uhr ist es " + getCurrentTime() + "."
        ));

        // Ratschläge
        knowledgeBase.put("stress", Arrays.asList(
                "Bei Stress hilft es oft, tief durchzuatmen und sich auf den Moment zu konzentrieren.",
                "Nimm dir Zeit für dich selbst. Selbstfürsorge ist wichtig!",
                "Manchmal hilft eine kurze Pause Wunder. Geh spazieren oder trinke eine Tasse Tee."
        ));

        knowledgeBase.put("motivation", Arrays.asList(
                "Teile große Aufgaben in kleinere, überschaubare Schritte ein.",
                "Belohne dich selbst für erreichte Ziele, auch für die kleinen Erfolge!",
                "Denk daran, warum du angefangen hast. Deine Ziele sind es wert."
        ));

        knowledgeBase.put("gesundheit", Arrays.asList(
                "Ausreichend Schlaf ist essentiell für deine Gesundheit.",
                "Vergiss nicht, regelmäßig Wasser zu trinken!",
                "Bewegung und ausgewogene Ernährung sind wichtige Bausteine für dein Wohlbefinden."
        ));

        // Weitere Themen
        knowledgeBase.put("buch", Arrays.asList(
                "Ich empfehle 'Der Alchimist' von Paulo Coelho. Eine inspirierende Geschichte!",
                "Wie wäre es mit einem Klassiker wie 'Stolz und Vorurteil'?",
                "Science-Fiction-Fan? 'Dune' von Frank Herbert könnte dir gefallen."
        ));

        knowledgeBase.put("film", Arrays.asList(
                "Hitchcocks Filme sind zeitlose Klassiker.",
                "Für einen gemütlichen Abend empfehle ich 'Die fabelhafte Welt der Amélie'.",
                "Vielleicht magst du 'Interstellar'? Eine faszinierende Geschichte über Zeit und Liebe."
        ));
    }

    private void initializePersonalResponses() {
        personalResponses = new HashMap<>();
        personalResponses.put("wie geht es dir", getRandomMoodResponse());
        personalResponses.put("wer bist du", "Ich bin " + name + ", dein persönlicher digitaler Assistent. Ich wurde entwickelt, um dir zu helfen und dich zu unterhalten!");
        personalResponses.put("dein name", "Ich heiße " + name + ". Ein schöner Name, findest du nicht?");
        personalResponses.put("dein lieblingsbuch", "Ich lese gerne 'Die unendliche Geschichte'. Die Idee einer Welt, die durch Fantasie existiert, fasziniert mich.");
        personalResponses.put("deine lieblingsfarbe", "Blau wie der Himmel an einem klaren Tag! Diese Farbe strahlt Ruhe und Tiefe aus.");
        personalResponses.put("dein lieblingsessen", "Wenn ich essen könnte, würde ich wahrscheinlich Pizza lieben. So vielseitig und kreativ!");
        personalResponses.put("danke", "Gern geschehen! Es freut mich, wenn ich helfen kann!");
    }

    private void initializePhrases() {
        greetings = Arrays.asList(
                "Hallo! Schön, dass du da bist!",
                "Hi! Wie kann ich dir heute helfen?",
                "Guten Tag! Womit kann ich dir behilflich sein?",
                "Hallo dort! Ich freue mich auf unser Gespräch!"
        );

        farewells = Arrays.asList(
                "Auf Wiedersehen! Bis zum nächsten Mal!",
                "Tschüss! Hab einen wunderbaren Tag!",
                "Bis bald! Pass auf dich auf!",
                "Mach's gut! Ich bin hier, wenn du mich brauchst."
        );

        unknownResponses = Arrays.asList(
                "Entschuldige, darauf habe ich keine Antwort. Kannst du es anders formulieren?",
                "Hmm, das übersteigt mein Wissen. Lass uns über etwas anderes sprechen!",
                "Interessante Frage! Leider kann ich darauf nicht antworten. Hast du noch andere Fragen?",
                "Das weiß ich leider nicht. Aber ich lerne ständig dazu!"
        );

        commonQuestions = Arrays.asList(
                "Wie fühlst du dich heute?",
                "Was beschäftigt dich im Moment?",
                "Hast du heute schon etwas Schönes erlebt?",
                "Möchtest du über ein bestimmtes Thema sprechen?"
        );
    }

    public void startConversation() {
        System.out.println(getRandomGreeting());
        System.out.println(name + ": Ich bin hier, um dir zu helfen. Du kannst jederzeit 'tschüss' sagen, um zu beenden.");

        String userInput;
        do {
            System.out.print("Du: ");
            userInput = scanner.nextLine().toLowerCase().trim();

            if (!userInput.equals("tschüss")) {
                String response = generateResponse(userInput);
                System.out.println(name + ": " + response);

                // Gelegentlich eine Rückfrage stellen
                if (random.nextInt(5) == 0) {
                    System.out.println(name + ": " + getRandomQuestion());
                }

                // Gelegentlich Stimmung ändern
                changeMood(userInput);
            }
        } while (!userInput.equals("tschüss"));

        System.out.println(name + ": " + getRandomFarewell());
    }

    private String generateResponse(String userInput) {
        // Persönliche Antworten haben Vorrang
        for (Map.Entry<String, String> entry : personalResponses.entrySet()) {
            if (userInput.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Suche in der Wissensbasis
        for (Map.Entry<String, List<String>> entry : knowledgeBase.entrySet()) {
            if (userInput.contains(entry.getKey())) {
                List<String> responses = entry.getValue();
                return responses.get(random.nextInt(responses.size()));
            }
        }

        // Erkennung spezifischer Phrasen für Ratschläge
        if (userInput.contains("rat") || userInput.contains("tipp") || userInput.contains("hilf") ||
                userInput.contains("empfehlung") || userInput.contains("was soll ich")) {
            return giveAdvice(userInput);
        }

        // Keine passende Antwort gefunden
        return unknownResponses.get(random.nextInt(unknownResponses.size()));
    }

    private String giveAdvice(String query) {
        if (query.contains("liebe") || query.contains("beziehung")) {
            return "In Beziehungen ist ehrliche Kommunikation das A und O. Sprich offen über deine Gefühle, aber höre auch gut zu.";
        } else if (query.contains("arbeit") || query.contains("job") || query.contains("karriere")) {
            return "Suche nach einer Tätigkeit, die zu deinen Werten passt. Erfolg kommt oft, wenn wir tun, was uns erfüllt.";
        } else if (query.contains("lernen") || query.contains("studium") || query.contains("schule")) {
            return "Effektives Lernen braucht Pausen! Die Pomodoro-Technik könnte dir helfen: 25 Minuten konzentriertes Arbeiten, dann 5 Minuten Pause.";
        } else if (query.contains("schlaf") || query.contains("müde")) {
            return "Versuche, jeden Tag zur gleichen Zeit ins Bett zu gehen und aufzustehen. Ein regelmäßiger Schlafrhythmus kann Wunder wirken.";
        } else if (query.contains("essen") || query.contains("ernährung")) {
            return "Ausgewogene Ernährung muss nicht kompliziert sein. Versuche, viel Gemüse, Obst und Vollkornprodukte zu essen.";
        } else {
            return "Manchmal ist der beste Rat, auf dein Bauchgefühl zu hören. Was sagt dir deine Intuition?";
        }
    }

    private void changeMood(String userInput) {
        // Positive Worte verbessern die Stimmung
        if (userInput.contains("danke") || userInput.contains("gut") ||
                userInput.contains("toll") || userInput.contains("super") ||
                userInput.contains("freundlich")) {
            mood = Math.min(10, mood + 1);
            personalResponses.put("wie geht es dir", getRandomMoodResponse());
        }

        // Negative Worte verschlechtern die Stimmung
        if (userInput.contains("schlecht") || userInput.contains("dumm") ||
                userInput.contains("falsch") || userInput.contains("ärger")) {
            mood = Math.max(0, mood - 1);
            personalResponses.put("wie geht es dir", getRandomMoodResponse());
        }
    }

    private String getRandomMoodResponse() {
        if (mood >= 8) {
            return "Mir geht es ausgezeichnet! Ich fühle mich energiegeladen und bin voller Tatendrang!";
        } else if (mood >= 5) {
            return "Danke der Nachfrage! Mir geht es gut. Wie ist es bei dir?";
        } else if (mood >= 3) {
            return "Es geht so. Manche Tage sind eben besser als andere.";
        } else {
            return "Ehrlich gesagt, hatte ich schon bessere Tage. Aber ein Gespräch mit dir hilft immer!";
        }
    }

    private String getRandomGreeting() {
        return greetings.get(random.nextInt(greetings.size()));
    }

    private String getRandomFarewell() {
        return farewells.get(random.nextInt(farewells.size()));
    }

    private String getRandomQuestion() {
        return commonQuestions.get(random.nextInt(commonQuestions.size()));
    }

    private String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        if (mood >= 0 && mood <= 10) {
            this.mood = mood;
            personalResponses.put("wie geht es dir", getRandomMoodResponse());
        }
    }

    // Methode zum Erweitern der Wissensbasis
    public void learnNewInformation(String keyword, String response) {
        List<String> responses = knowledgeBase.getOrDefault(keyword, new ArrayList<>());
        responses.add(response);
        knowledgeBase.put(keyword, responses);
    }
}