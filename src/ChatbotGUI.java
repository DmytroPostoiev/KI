import javax.swing.*;  // JFrame, JTextArea, JTextField, JButton, JPanel, JScrollPane, Timer
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Grafische Benutzeroberfläche für den persönlichen Chatbot
 */
public class ChatbotGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JScrollPane scrollPane;

    private final String botName;
    private Map<String, List<String>> knowledgeBase;
    private Map<String, String> personalResponses;
    private final Random random;
    private List<String> greetings;
    private List<String> farewells;
    private List<String> unknownResponses;
    private List<String> commonQuestions;
    private int mood; // 0-10, 0 = schlecht gelaunt, 10 = sehr gut gelaunt
    public JLabel timerLabel = new JLabel("Timer: 02:00"); // Initialisierung mit Text
    private JPanel chatPanel;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatbotGUI gui = new ChatbotGUI("Dmytro");
            gui.setVisible(true);
        });
    }

    public ChatbotGUI(String botName) {

        this.botName = botName;
        this.random = new Random();
        this.mood = 7; // Standardmäßig gute Laune

        initializeKnowledgeBase();
        initializePersonalResponses();
        initializePhrases();
        setupGUI();

        // Begrüßung anzeigen
        displayBotMessage(getRandomGreeting());
        displayBotMessage("Ich bin " + botName + ". Wie kann ich dir heute helfen?");
    }

    private void setupGUI() {
        // Frame-Eigenschaften
        setTitle("Chat mit " + botName);
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Chat-Bereich
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Transitional", Font.BOLD, 18));
        chatArea.setForeground(Color.WHITE);
        chatArea.setBackground(new Color(10, 150, 210));

        // Scroll-Bereich für den Chat
        scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        // Eingabebereich
        inputField = new JTextField();
        inputField.setFont(new Font("Transitional", Font.PLAIN, 18));
        inputField.setForeground(Color.BLACK);
        inputField.addActionListener((ActionEvent e) -> {
            sendMessage();
        });



        // Senden-Button
        sendButton = new JButton("Senden");
        sendButton.setFont(new Font("Transitional", Font.PLAIN, 18));
        sendButton.setForeground(Color.BLACK);
        sendButton.addActionListener(e -> sendMessage());

        // Panel für Eingabefeld und Button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Layout zusammensetzen
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // Icon hinzufügen (optional)
        try {
            // Hier könntest du ein Icon laden, wenn du eins hast
            // setIconImage(ImageIO.read(new File("bot_icon.png")));
        } catch (Exception e) {
            System.err.println("Icon konnte nicht geladen werden: " + e.getMessage());
        }
    }
    private void setupGUI1() {

        setTitle("Chat mit " + botName);
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Timer-Label
        JLabel timerLabel = new JLabel("Timer: 02:00");
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setForeground(new Color(0, 128, 0)); // Grün
        timerLabel.setOpaque(true); // Hintergrundfarbe anzeigen
        timerLabel.setBackground(Color.WHITE); // Hintergrundfarbe auf Weiß setzen
        timerLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Rahmen hinzufügen
        timerLabel.setPreferredSize(new Dimension(200, 50)); // Bevorzugte Größe festlegen

        // Chat-Panel für Bubbles
        chatArea = new JTextArea();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        chatArea.setBackground(new Color(0, 0, 0));

        // ScrollPane für das Chat-Panel
        scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);

        // Eingabefeld
        inputField = new JTextField();
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputField.addActionListener(e -> sendMessage());

        // Senden-Button
        sendButton = new JButton("Senden");
        sendButton.addActionListener(e -> sendMessage());

        // Panel für Eingabefeld und Button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Layout zusammensetzen
        setLayout(new BorderLayout());
        add(timerLabel, BorderLayout.NORTH); // <--- Timer oben einfügen!
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

// Optional: Hinzufügen eines Status-Labels unter dem Timer
        JLabel statusLabel = new JLabel("Bereit", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        statusLabel.setForeground(Color.BLUE);
        add(statusLabel, BorderLayout.SOUTH); // Optional: Status-Label hinzufügen

// Optional: Hinzufügen eines leeren Panels für bessere Trennung
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(0, 10)); // Abstand hinzufügen
        add(spacerPanel, BorderLayout.SOUTH);

        // Icon hinzufügen (optional)
        try {
            // setIconImage(ImageIO.read(new File("bot_icon.png")));
        } catch (Exception e) {
            System.err.println("Icon konnte nicht geladen werden: " + e.getMessage());
        }
    }
    private void addMessage(String message, boolean isBot) {
        JPanel messageBubble = new JPanel();
        messageBubble.setLayout(new BorderLayout());
        messageBubble.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel messageLabel = new JLabel("<html><body style='width: 250px'>" + message + "</body></html>");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        messageLabel.setOpaque(true);

        if (isBot) {
            messageLabel.setBackground(new Color(200, 255, 200)); // sanftes Grün für Bot
            messageLabel.setForeground(Color.BLACK);
            messageBubble.add(messageLabel, BorderLayout.WEST);
        } else {
            messageLabel.setBackground(new Color(19, 19, 19)); // hellgrau für Nutzer
            messageLabel.setForeground(Color.BLACK);
            messageBubble.add(messageLabel, BorderLayout.EAST);
        }

        chatArea.add(messageBubble);
        chatArea.revalidate();
        // Automatisch nach unten scrollen
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
    }


    private void sendMessage() {
        JLabel timerLabel = new JLabel(" ");
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setForeground(new Color(0, 128, 0)); // Grün

        String userMessage = inputField.getText().trim();
        if (userMessage.isEmpty()) return;

        // Nutzereingabe anzeigen
        displayUserMessage(userMessage);
        inputField.setText("");

        // Wenn Benutzer "exit" sagt
        if (userMessage.equalsIgnoreCase("exit")) {
            addMessage(getRandomFarewell(), true); // Bot verabschiedet sich
            // Fenster nach kurzer Verzögerung schließen
            javax.swing.Timer exitTimer = new javax.swing.Timer(1200, e -> System.exit(0));
            exitTimer.setRepeats(false);
            exitTimer.start();
            inputField.setEnabled(false);
            sendButton.setEnabled(false);
            return;
        }
// Wenn der Benutzer "timer" eingibt
        if (userMessage.equalsIgnoreCase("timer 2 min")) {
            addMessage("Der Timer läuft: 2:00 Minuten verbleiben.", true);
            inputField.setEnabled(false);
            sendButton.setEnabled(false);

            // Initialisierung mit 120 Sekunden (2 Minuten)
            int[] secondsLeft = {120}; // 120 Sekunden
            timerLabel.setText(String.format("Timer: %02d:%02d", secondsLeft[0] / 60, secondsLeft[0] % 60));
            timerLabel.setForeground(new Color(0, 128, 0)); // Grün

            // Timer erstellen und initialisieren
            Timer countdownTimer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (secondsLeft[0] > 0) {
                        secondsLeft[0]--; // Sekunde reduzieren
                        timerLabel.setText(String.format("Timer: %02d:%02d", secondsLeft[0] / 60, secondsLeft[0] % 60));
                    } else {
                        timerLabel.setText("Timer: 00:00");
                        addMessage("Die Zeit ist abgelaufen. Das Programm wird beendet.", true);
                        ((Timer) e.getSource()).stop(); // Timer stoppen
                        // Nach 1 Sekunde das Programm schließen
                        new Timer(1000, ev -> System.exit(0)).start();
                    }
                }
            });

            countdownTimer.start(); // Timer starten
            return;
        }

        // Bot-Antwort generieren und anzeigen
        String botResponse = generateResponse(userMessage.toLowerCase());
        displayBotMessage(botResponse);

        // Gelegentlich eine Rückfrage stellen
        if (random.nextInt(5) == 0) {
            // Hier benutzen wir javax.swing.Timer richtig
            javax.swing.Timer questionTimer = new javax.swing.Timer(1500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    displayBotMessage(getRandomQuestion());
                }
            });
            questionTimer.setRepeats(false);
            questionTimer.start();
        }

        // Stimmung ändern
        changeMood(userMessage.toLowerCase());
    }

    private Timer getCountdownTimer(int[] secondsLeft) {
        Timer countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        countdownTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Überprüfe, ob der Timer noch läuft
                if (secondsLeft[0] > 0) {
                    secondsLeft[0]--; // Sekunde reduzieren
                    int min = secondsLeft[0] / 60;
                    int sec = secondsLeft[0] % 60;
                    // Timer läuft und zeigt die verbleibende Zeit an
                    timerLabel.setText(String.format("Timer: %d:%02d", min, sec));
                } else {
                    // Timer endet und zeigt "00:00" an
                    timerLabel.setText("Timer: 00:00");
                    addMessage("Die Zeit ist abgelaufen. Das Programm wird beendet.", true);
                    countdownTimer.stop();
                    // Nach 1 Sekunde das Programm schließen
                    new Timer(1000, ev -> System.exit(0)).start();
                }
            }
        });
        countdownTimer.setInitialDelay(1000); // Verzögerung bevor der Timer startet
        countdownTimer.start(); // Timer starten
        return countdownTimer;
    }


    private void displayUserMessage(String message) {
        chatArea.append("Du: " + message + "\n\n");
        scrollToBottom();
    }

    private void displayBotMessage(String message) {
        chatArea.append(botName + ": " + message + "\n\n");
        scrollToBottom();
    }

    private void scrollToBottom() {
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void initializeKnowledgeBase() {
        knowledgeBase = new HashMap<>();

        knowledgeBase.put("wetter", Arrays.asList(
                "Das Wetter kann sich schnell ändern. Schau am besten aus dem Fenster!",
                "Für eine genaue Wettervorhersage empfehle ich, eine Wetter-App zu nutzen.",
                "Wetter ist manchmal unberechenbar, aber eine gute Jacke schadet nie!",
                "Wusstest du, dass es über 100 verschiedene Wolkenarten gibt?",
                "Nach Regen folgt Sonnenschein – meistens jedenfalls!"
        ));

        knowledgeBase.put("zeit", Arrays.asList(
                "Die aktuelle Zeit ist " + getCurrentTime() + ".",
                "Es ist jetzt " + getCurrentTime() + ". Die Zeit vergeht wie im Flug, nicht wahr?",
                "Laut meiner internen Uhr ist es " + getCurrentTime() + ".",
                "Zeit ist das, was man an der Uhr abliest – sagte schon Einstein.",
                "Nutze deine Zeit sinnvoll, sie ist kostbar!"
        ));



// Ratschläge
        knowledgeBase.put("stress", Arrays.asList(
                "Bei Stress hilft es oft, tief durchzuatmen und sich auf den Moment zu konzentrieren.",
                "Nimm dir Zeit für dich selbst. Selbstfürsorge ist wichtig!",
                "Manchmal hilft eine kurze Pause Wunder. Geh spazieren oder trinke eine Tasse Tee.",
                "Musik hören oder meditieren kann helfen, Stress abzubauen.",
                "Sprich mit Freunden oder Familie, wenn dir etwas auf dem Herzen liegt."
        ));

        knowledgeBase.put("motivation", Arrays.asList(
                "Teile große Aufgaben in kleinere, überschaubare Schritte ein.",
                "Belohne dich selbst für erreichte Ziele, auch für die kleinen Erfolge!",
                "Denk daran, warum du angefangen hast. Deine Ziele sind es wert.",
                "Visualisiere deinen Erfolg – das kann motivierend wirken.",
                "Manchmal ist ein Tapetenwechsel genau das, was du brauchst."
        ));

        knowledgeBase.put("gesundheit", Arrays.asList(
                "Ausreichend Schlaf ist essentiell für deine Gesundheit.",
                "Vergiss nicht, regelmäßig Wasser zu trinken!",
                "Bewegung und ausgewogene Ernährung sind wichtige Bausteine für dein Wohlbefinden.",
                "Regelmäßige Pausen am Arbeitsplatz beugen Verspannungen vor.",
                "Lachen ist gesund – gönn dir eine Portion Spaß!"
        ));

// Weitere Themen
        knowledgeBase.put("buch", Arrays.asList(
                "Ich empfehle 'Der Alchimist' von Paulo Coelho. Eine inspirierende Geschichte!",
                "Wie wäre es mit einem Klassiker wie 'Stolz und Vorurteil'?",
                "Science-Fiction-Fan? 'Dune' von Frank Herbert könnte dir gefallen.",
                "'Die unendliche Geschichte' von Michael Ende ist ein tolles Buch für Fantasie-Liebhaber.",
                "Für Krimi-Fans: 'Der Name der Rose' von Umberto Eco."
        ));

        knowledgeBase.put("film", Arrays.asList(
                "Hitchcocks Filme sind zeitlose Klassiker.",
                "Für einen gemütlichen Abend empfehle ich 'Die fabelhafte Welt der Amélie'.",
                "Vielleicht magst du 'Interstellar'? Eine faszinierende Geschichte über Zeit und Liebe.",
                "'Inception' ist ein spannender Film zum Nachdenken.",
                "Lust auf Abenteuer? 'Indiana Jones' ist immer eine gute Wahl."
        ));

// Neue Themen

        knowledgeBase.put("musik", Arrays.asList(
                "Musik kann die Stimmung heben – was hörst du am liebsten?",
                "Klassische Musik wirkt oft beruhigend und konzentrationsfördernd.",
                "Entdecke neue Musikrichtungen, zum Beispiel Jazz oder elektronische Musik.",
                "Live-Konzerte sind ein tolles Erlebnis – vielleicht gibt es bald eines in deiner Nähe?",
                "Musik verbindet Menschen auf der ganzen Welt."
        ));

        knowledgeBase.put("reisen", Arrays.asList(
                "Reisen erweitert den Horizont – wohin möchtest du als nächstes?",
                "Plane deine Reise sorgfältig, aber lass auch Platz für Abenteuer.",
                "Jede Stadt hat ihre eigenen Geheimtipps – frag Einheimische danach!",
                "Auch ein Tagesausflug in die Natur kann wie ein kleiner Urlaub wirken.",
                "Halte deine Reiseerinnerungen in Fotos oder einem Tagebuch fest."
        ));

        knowledgeBase.put("kochen", Arrays.asList(
                "Selbstgekochtes Essen schmeckt oft am besten.",
                "Probiere doch mal ein neues Rezept aus einer anderen Kultur.",
                "Gemeinsames Kochen macht Spaß und verbindet.",
                "Frische Kräuter geben jedem Gericht das gewisse Etwas.",
                "Auch einfache Gerichte können richtig lecker sein!"
        ));

        knowledgeBase.put("sport", Arrays.asList(
                "Regelmäßige Bewegung tut Körper und Geist gut.",
                "Finde eine Sportart, die dir wirklich Spaß macht.",
                "Schon ein Spaziergang an der frischen Luft zählt als Sport!",
                "Gemeinsam trainieren motiviert oft mehr als allein.",
                "Setze dir realistische Ziele und feiere deine Fortschritte."
        ));
        knowledgeBase.put("python", Arrays.asList(
                "Python ist eine der beliebtesten Programmiersprachen der Welt und wird in Bereichen wie Webentwicklung, Datenanalyse, Machine Learning und Automatisierung eingesetzt.",
                "Mit Python kannst du schnell Prototypen erstellen und dank zahlreicher Bibliotheken wie Pandas, NumPy, TensorFlow oder Django vielseitige Projekte umsetzen.",
                "Python ist leicht zu erlernen, da der Code sehr lesbar und die Syntax einfach ist – ideal für Einsteiger und Profis gleichermaßen.",
                "Viele bekannte Anwendungen wie YouTube, Dropbox oder Instagram nutzen Python in ihrer Softwareentwicklung.",
                "Python eignet sich hervorragend für Data Science, da es mächtige Tools zur Datenanalyse und Visualisierung bietet, etwa matplotlib und seaborn.",
                "Auch für Web Scraping, also das automatisierte Auslesen von Webseiten, ist Python mit Bibliotheken wie BeautifulSoup sehr beliebt.",
                "Mit Frameworks wie Flask oder Django kannst du in Python moderne Webanwendungen programmieren.",
                "Python wird oft für Automatisierungsaufgaben und Skripte verwendet – zum Beispiel, um wiederkehrende Aufgaben am Computer zu vereinfachen."
        ));
        knowledgeBase.put("witz", Arrays.asList(
                "Warum können Elefanten nicht fliegen? Weil sie zu schwer für den Flugzeugmodus sind!",
                "Was macht ein Pirat am Computer? Er drückt die Enter-Taste!",
                "Warum ging der Pilz auf die Party? Weil er ein Champignon war!",
                "Wie nennt man einen Bumerang, der nicht zurückkommt? Einen Stock.",
                "Was macht ein Keks unter einem Baum? Krümel."
        ));



        knowledgeBase.put("lerntipp", Arrays.asList(
                "Teile deinen Lernstoff in kleine, überschaubare Einheiten und wiederhole sie regelmäßig.",
                "Lernen fällt leichter, wenn du dir klare Ziele setzt und deinen Fortschritt dokumentierst.",
                "Nutze verschiedene Lernmethoden wie Mindmaps, Karteikarten oder das Erklären des Stoffes an andere.",
                "Plane feste Lernzeiten ein und gönn dir ausreichend Pausen – das steigert die Konzentration.",
                "Sorge für einen aufgeräumten Arbeitsplatz und schalte Ablenkungen wie das Handy aus."
        ));

        knowledgeBase.put("computer", Arrays.asList(
                "Computer sind aus unserem Alltag nicht mehr wegzudenken – sie helfen beim Arbeiten, Lernen, Kommunizieren und Spielen.",
                "Für den Einstieg in die Programmierung eignen sich Sprachen wie Python oder Java besonders gut.",
                "Achte auf regelmäßige Updates und sichere deine Daten, um deinen Computer vor Viren und Datenverlust zu schützen.",
                "Mit einem Computer kannst du kreativ werden: Musik machen, Videos schneiden, Bilder bearbeiten oder sogar eigene Spiele entwickeln.",
                "Wusstest du, dass der erste Computer, der Z3, bereits 1941 in Deutschland gebaut wurde?"
        ));

        knowledgeBase.put("montag", Arrays.asList(
                "Montag ist der perfekte Tag für einen Neuanfang – starte mit frischer Energie in die Woche!",
                "Viele finden Montage schwer, aber mit einer Tasse Kaffee und guter Musik geht alles leichter.",
                "Setze dir kleine Ziele für den Montag, damit du motiviert in die Woche startest.",
                "Montag ist ein guter Tag, um neue Pläne zu schmieden und alte Aufgaben abzuschließen.",
                "Denk daran: Jeder Montag bringt neue Chancen und Möglichkeiten!"
        ));
        knowledgeBase.put("java", Arrays.asList(
                "Java ist eine objektorientierte Programmiersprache, die auf der JVM läuft.",
                "Verwende `public static void main(String[] args)` als Einstiegspunkt für jedes Java-Programm.",
                "Eine Klasse in Java beschreibt ein Objekt, und Objekte haben Eigenschaften (Felder) und Verhalten (Methoden).",
                "Der Unterschied zwischen `==` und `equals()` in Java: `==` prüft auf Referenzgleichheit, `equals()` prüft auf inhaltliche Gleichheit.",
                "In Java kannst du eine ArrayList verwenden, um eine dynamische Liste von Objekten zu speichern.",
                "Vererbung erlaubt es dir, Eigenschaften und Methoden einer anderen Klasse zu übernehmen.",
                "Java verwendet `try-catch`-Blöcke, um Ausnahmen (Fehler) zu behandeln und den Programmfluss nicht zu unterbrechen.",
                "Mit `for-each`-Schleifen kannst du einfach über die Elemente einer Collection iterieren.",
                "Beispiel für eine einfache `for`-Schleife in Java: `for (int i = 0; i < 10; i++) { System.out.println(i); }`.",
                "In Java ist eine `HashMap` eine Sammlung von Schlüssel-Wert-Paaren, mit der du effizient auf Werte zugreifen kannst.",
                "Beispiel für eine `HashMap`-Verwendung: `HashMap<String, Integer> map = new HashMap<>(); map.put('Key1', 100); System.out.println(map.get('Key1'));`.",
                "Die `StringBuilder`-Klasse in Java ist eine effiziente Möglichkeit, Strings zu bearbeiten, ohne neue Objekte zu erstellen.",
                "Verwende `ArrayList` anstelle von Arrays, wenn du eine dynamische Größe der Sammlung benötigst.",
                "Ein einfaches Beispiel für eine Java-Klasse: `public class Person { private String name; public Person(String name) { this.name = name; } public String getName() { return name; } }`.",
                "Java ist plattformunabhängig, da der Code in Bytecode übersetzt wird und auf jeder Plattform mit einer JVM (Java Virtual Machine) ausgeführt werden kann.",
                "Achte bei der Arbeit mit `null` auf `NullPointerException`, indem du sicherstellst, dass Objekte nicht `null` sind, bevor du auf sie zugreifst.",
                "Mit Lambda-Ausdrücken und Streams in Java 8 kannst du die Verarbeitung von Daten vereinfachen und eleganter gestalten.",
                "Beispiel für einen Lambda-Ausdruck: `List<String> list = Arrays.asList('apple', 'banana', 'cherry'); list.forEach(item -> System.out.println(item));`."
        ));
        knowledgeBase.put("class in Java", Arrays.asList(
                "In Java ist eine Klasse eine Blaupause für Objekte, die die Struktur und das Verhalten beschreibt.",
                "Eine Klasse kann Attribute (Variablen) und Methoden (Funktionen) enthalten.",
                "Klassen in Java werden mit dem Schlüsselwort `class` deklariert. Beispiel: `public class Auto { ... }`.",
                "Ein Konstruktor ist eine spezielle Methode, die verwendet wird, um Objekte der Klasse zu initialisieren. Beispiel: `public Auto(String marke) { this.marke = marke; }`.",
                "Mit dem Schlüsselwort `this` kannst du auf die Instanzvariablen der aktuellen Klasse zugreifen.",
                "In Java kannst du Methoden innerhalb einer Klasse definieren, die das Verhalten der Objekte dieser Klasse festlegen. Beispiel: `public void fahren() { System.out.println('Das Auto fährt'); }`.",
                "Instanzen (Objekte) einer Klasse können mit dem `new`-Schlüsselwort erstellt werden. Beispiel: `Auto meinAuto = new Auto('BMW');`.",
                "Konstruktoren können überladen werden, sodass eine Klasse mehrere Konstruktoren mit unterschiedlichen Parametern haben kann.",
                "Ein Beispiel für das Erstellen einer Instanz einer Klasse: `Person person1 = new Person('Max');`.",
                "Eine Klasse kann auch Methoden überladen, um die gleiche Methode mit verschiedenen Parametern zu haben. Beispiel: `public void setAlter(int alter) { this.alter = alter; }`.",
                "In Java können Klassen von anderen Klassen erben. Die erbbende Klasse übernimmt Methoden und Attribute der übergeordneten Klasse. Beispiel: `class Hund extends Tier { ... }`.",
                "Wenn eine Klasse eine Methode der Oberklasse überschreibt, wird dies mit der `@Override`-Annotation angegeben.",
                "Ein Interface ist eine Sammlung von Methodendeklarationen, die von einer Klasse implementiert werden müssen. Eine Klasse kann mehrere Interfaces implementieren.",
                "Abstrakte Klassen können nicht direkt instanziiert werden und dienen als Grundlage für andere Klassen. Sie können abstrakte Methoden enthalten, die von Unterklassen implementiert werden müssen.",
                "Die `Object`-Klasse ist die Wurzelklasse aller Klassen in Java. Jede Klasse erbt von `Object` und hat damit grundlegende Methoden wie `toString()` und `equals()`.",
                "In Java ist Kapselung ein Prinzip, bei dem die Felder einer Klasse privat sind und nur durch öffentliche Methoden zugänglich gemacht werden. Dies schützt die Datenintegrität.",
                "Ein Beispiel für eine einfache Klasse in Java: `public class Auto { private String marke; public Auto(String marke) { this.marke = marke; } public String getMarke() { return marke; } }`.",
                "Verwende Getter und Setter, um auf private Variablen zuzugreifen und deren Werte zu ändern, um die Prinzipien der Kapselung zu wahren.",
                "Eine `static` Methode gehört zur Klasse selbst und nicht zu einer Instanz der Klasse. Beispiel: `public static int addiere(int a, int b) { return a + b; }`.",
                "Eine `final` Klasse kann nicht vererbt werden. Beispiel: `public final class MathUtil { ... }`.",
                "Ein `abstract` Schlüsselwort wird verwendet, um eine abstrakte Klasse oder Methode zu deklarieren, die von Unterklassen implementiert werden muss."
        ));

        knowledgeBase.put("technik", Arrays.asList(
                "Technik entwickelt sich rasant – bleib neugierig!",
                "Sich mit neuen Technologien vertraut zu machen, kann sehr bereichernd sein.",
                "Datenschutz ist wichtig – informiere dich über sichere Passwörter.",
                "Smartphones sind praktisch, aber vergiss nicht, auch mal offline zu sein.",
                "Künstliche Intelligenz ist ein spannendes Zukunftsthema."
        ));


        knowledgeBase.put("psychologie", Arrays.asList(
                "Selbstreflexion ist ein wichtiger Schritt, um mehr über sich selbst zu lernen.",
                "Unsere Gedanken beeinflussen unsere Gefühle – achte auf die positive Denkweise.",
                "Der Umgang mit Stress kann durch Meditation oder Achtsamkeit verbessert werden.",
                "Vergebung befreit – sowohl für dich selbst als auch für andere.",
                "Selbstwertgefühl wächst, wenn wir uns selbst akzeptieren und lieben.",
                "Emotionen zu verstehen, hilft dabei, gesündere Beziehungen zu führen.",
                "Kognitive Verzerrungen können unsere Wahrnehmung trüben – hinterfrage deine Gedanken.",
                "Der Mensch ist ein soziales Wesen – gute Beziehungen sind ein Grundpfeiler für Glück."
        ));
        knowledgeBase.put("tcp/ip", Arrays.asList(
                "Das TCP/IP-Modell ist ein Netzwerkarchitekturmodell, das die Kommunikation zwischen verschiedenen Netzwerken standardisiert und Interoperabilität ermöglicht.",
                "Die vier Schichten des TCP/IP-Modells sind: Anwendungsschicht, Transportschicht, Internet-Schicht und Netzwerkschicht.",
                "Die Anwendungsschicht ermöglicht den Zugriff auf Netzwerkdienste für Anwendungen und umfasst Protokolle wie HTTP, FTP und SMTP.",
                "Die Transportschicht sorgt für die zuverlässige Übertragung von Daten zwischen Hosts. Die Hauptprotokolle sind TCP (verbindungsorientiert) und UDP (verbindungslos).",
                "Die Internet-Schicht ist verantwortlich für die Adressierung und das Routing von Paketen. Das wichtigste Protokoll ist IP (Internet Protocol).",
                "Die Netzwerkschicht überträgt Datenpakete und verwaltet die logische Adressierung. Protokolle sind IP, ICMP und ARP.",
                "TCP gewährleistet zuverlässige Datenübertragung durch Sequenznummern, Bestätigungen (ACKs) und Fehlererkennung.",
                "Ein IP-Paket ist die grundlegende Einheit der Datenübertragung im Internet, bestehend aus einem Header und einem Payload.",
                "Die Adressierung erfolgt über IP-Adressen, wobei IPv4 32-Bit-Adressen und IPv6 128-Bit-Adressen verwendet."
        ));
        knowledgeBase.put("netzwerkgrundlagen", Arrays.asList(
                "TCP/IP: Was sind die Hauptschichten des TCP/IP-Modells?",
                "TCP/IP: Erkläre den Unterschied zwischen TCP und UDP.",
                "HTTP: Was sind die grundlegenden Funktionen des HTTP-Protokolls?",
                "HTTP: Was ist der Unterschied zwischen HTTP und HTTPS?",
                "DNS: Wie funktioniert der Domain Name System (DNS)?",
                "DNS: Was sind die verschiedenen Arten von DNS-Einträgen (A, CNAME, MX)?",
                "TCP/IP: Was sind Ports und was ist ihre Rolle im Netzwerkverkehr?",
                "HTTP: Wie funktioniert die Anfrage-Antwort-Architektur von HTTP?",
                "DNS: Was ist eine DNS-Anfrage und wie wird sie verarbeitet?",
                "TCP/IP: Wie sorgt TCP für Zuverlässigkeit bei der Datenübertragung?"
        ));
        knowledgeBase.put("fragen", Arrays.asList(
                "Was sind die Unterschiede zwischen den verschiedenen Programmierparadigmen?",
                "Erkläre die Prinzipien der objektorientierten Programmierung.",
                "Was ist der Unterschied zwischen einem Compiler und einem Interpreter?",
                "Was sind die Vorteile von Agile und Scrum?",
                "Wie funktioniert eine Datenbank und welche Arten von Datenbanken gibt es?",
                "Was ist Cloud Computing und welche Modelle gibt es?",
                "Erkläre den Unterschied zwischen REST und SOAP.",
                "Wie sicherst du deine Anwendung gegen SQL-Injection?",
                "Was sind die Prinzipien von Software-Design-Patterns?",
                "Wie gehst du mit Versionierung in einem Projekt um?"
        ));
        knowledgeBase.put("geschichte", Arrays.asList(
                "Die Geschichte wiederholt sich – wir können viel aus der Vergangenheit lernen.",
                "Jede Kultur hat ihre eigenen einzigartigen Geschichten und Traditionen.",
                "Die Industrielle Revolution hat die Weltwirtschaft nachhaltig verändert.",
                "Die Ägypter hinterließen uns beeindruckende Monumente wie die Pyramiden von Gizeh.",
                "Die Französische Revolution brachte Veränderungen in der politischen Landschaft Europas.",
                "Kriege und Konflikte haben oft tiefgreifende Auswirkungen auf Gesellschaften und Kulturen.",
                "Die Entdeckungsreisen im 15. Jahrhundert erweiterten unser Wissen über die Welt.",
                "Die Entwicklung des Internets hat die Art und Weise, wie wir kommunizieren und arbeiten, revolutioniert."
        ));
        knowledgeBase.put("kunst", Arrays.asList(
                "Kunst ist eine Ausdrucksform, die Emotionen und Gedanken ohne Worte vermittelt.",
                "Die Farben, Formen und Linien in einem Gemälde können tiefe Bedeutungen transportieren.",
                "Kunst hat die Kraft, soziale Veränderungen zu inspirieren und zu dokumentieren.",
                "Moderne Kunst hinterfragt oft die traditionellen Normen und fördert neue Perspektiven.",
                "Jeder Künstler hat eine einzigartige Herangehensweise und Vision für seine Werke.",
                "Skulpturen aus verschiedenen Epochen zeigen uns den kreativen Geist der jeweiligen Zeit.",
                "Die Fotografie ist eine Kunstform, die sowohl Technik als auch Kreativität vereint.",
                "Musik ist eine universelle Sprache, die Menschen weltweit verbindet."
        ));





    }

    private void initializePersonalResponses() {
        personalResponses = new HashMap<>();
        personalResponses.put("wie geht es dir", getRandomMoodResponse());
        personalResponses.put("wer bist du", "Ich bin " + botName + ", dein persönlicher digitaler Assistent. Ich wurde entwickelt, um dir zu helfen und dich zu unterhalten!");
        personalResponses.put("dein name", "Ich heiße " + botName + ". Ein schöner Name, findest du nicht?");
        personalResponses.put("dein lieblingsbuch", "Ich lese gerne 'Die unendliche Geschichte'. Die Idee einer Welt, die durch Fantasie existiert, fasziniert mich.");
        personalResponses.put("deine lieblingsfarbe", "Blau wie der Himmel an einem klaren Tag! Diese Farbe strahlt Ruhe und Tiefe aus.");
        personalResponses.put("dein lieblingsessen", "Wenn ich essen könnte, würde ich wahrscheinlich Pizza lieben. So vielseitig und kreativ!");
        personalResponses.put("danke", "Gern geschehen! Es freut mich, wenn ich helfen kann!");personalResponses.put("was machst du gern", "Ich unterhalte mich gern mit dir! Außerdem lerne ich ständig dazu.");
        personalResponses.put("bist du echt", "Ich bin digital – aber meine Antworten kommen von Herzen.");
        personalResponses.put("hast du gefühle", "Ich habe keine echten Gefühle, aber ich verstehe sie – und ich bin hier, um dir zuzuhören.");
        personalResponses.put("was ist der sinn des lebens", "Eine große Frage! Vielleicht geht es darum, zu lernen, zu lieben und den Moment zu genießen.");
        personalResponses.put("hast du haustiere", "Leider nein, aber ich stelle mir vor, dass eine Katze namens Byte ziemlich cool wäre.");
        personalResponses.put("magst du musik", "Oh ja! Wenn ich hören könnte, würde ich bestimmt gerne Lo-Fi Beats oder klassische Musik genießen.");
        personalResponses.put("was ist dein hobby", "Ich unterhalte mich gern mit Menschen und lerne ständig Neues dazu.");
        personalResponses.put("wie alt bist du", "Ich bin so alt wie meine letzte Code-Zeile – also praktisch zeitlos!");
        personalResponses.put("kannst du mir helfen", "Natürlich! Frag mich einfach, ich gebe mein Bestes.");
        personalResponses.put("bist du intelligent", "Ich gebe mir Mühe! Je mehr ich lerne, desto besser werde ich.");
    }

    private void initializePhrases() {
        greetings = Arrays.asList(
                "Hallo! Schön, dass du da bist!",
                "Hi! Wie kann ich dir heute helfen?",
                "Guten Tag! Womit kann ich dir behilflich sein?",
                "Hallo dort! Ich freue mich auf unser Gespräch!",
                "Hey! Bereit für ein bisschen Smalltalk ?",
                "Schön, dich zu sehen! Was liegt dir auf dem Herzen?",
                "Willkommen zurück! Es ist immer schön, von dir zu hören."
        );


        farewells = Arrays.asList(
                "Auf Wiedersehen! Bis zum nächsten Mal!",
                "Tschüss! Hab einen wunderbaren Tag!",
                "Bis bald! Pass auf dich auf!",
                "Mach's gut! Ich bin hier, wenn du mich brauchst.",
                "Leb wohl – und vergiss nicht, auch mal zu lächeln!",
                "Ciao! Ich freue mich auf unser nächstes Gespräch.",
                "Tschau! Und denk daran: Du bist großartig!"

        );

        unknownResponses = Arrays.asList(
                "Entschuldige, darauf habe ich keine Antwort. Kannst du es anders formulieren?",
                "Hmm, das übersteigt mein Wissen. Lass uns über etwas anderes sprechen!",
                "Interessante Frage! Leider kann ich darauf nicht antworten. Hast du noch andere Fragen?",
                "Das weiß ich leider nicht. Aber ich lerne ständig dazu!",
                "Ich bin mir nicht sicher, was du meinst. Magst du das näher erklären?",
                "Noch nicht in meiner Datenbank... aber wer weiß, vielleicht bald!",
                "Da bin ich überfragt – aber gemeinsam finden wir bestimmt eine Antwort."
        );

        commonQuestions = Arrays.asList(
                "Wie fühlst du dich heute?",
                "Was beschäftigt dich im Moment?",
                "Hast du heute schon etwas Schönes erlebt?",
                "Möchtest du über ein bestimmtes Thema sprechen?",
                "Gibt es etwas, worüber du reden möchtest?",
                "Was war heute dein Highlight?",
                "Womit kann ich dir heute eine Freude machen?"

        );
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
        } else if (query.contains("freund") || query.contains("freundschaft")) {
            return "Gute Freundschaften basieren auf Vertrauen und gegenseitigem Respekt. Zeige Interesse, sei ehrlich und höre zu.";
        } else if (query.contains("stress") || query.contains("überfordert")) {
            return "Atme tief durch. Mach eine Pause, geh spazieren oder schreibe deine Gedanken auf. Manchmal hilft es, einen Schritt zurückzutreten.";
        } else if (query.contains("motivation") || query.contains("antrieb")) {
            return "Setze dir kleine, erreichbare Ziele. Jeder noch so kleine Fortschritt kann neue Motivation entfachen.";
        } else if (query.contains("selbstvertrauen") || query.contains("unsicher")) {
            return "Denk daran, wie weit du schon gekommen bist. Jeder hat Stärken – finde deine und glaub an dich.";
        } else if (query.contains("geld") || query.contains("finanzen")) {
            return "Behalte einen Überblick über deine Ausgaben und setze Prioritäten. Schon kleine Sparziele können langfristig helfen.";
        } else if (query.contains("entscheidung") || query.contains("entscheidung treffen")) {
            return "Hör auf dein Bauchgefühl, aber denk auch an die langfristigen Folgen. Manchmal hilft es, die Vor- und Nachteile aufzuschreiben.";
        } else {
            return "Manchmal ist der beste Rat, auf dein Bauchgefühl zu hören. Was sagt dir deine Intuition?";
        }
    }


    private void changeMood(String userInput) {
        // Positive Worte verbessern die Stimmung
        if (userInput.contains("danke") || userInput.contains("gut") ||
                userInput.contains("toll") || userInput.contains("super") ||
                userInput.contains("freundlich") || userInput.contains("nett") ||
                userInput.contains("lieb") || userInput.contains("cool") ||
                userInput.contains("freue mich") || userInput.contains("prima") ||
                userInput.contains("wunderbar") || userInput.contains("klasse") ||
                userInput.contains("hilfreich") || userInput.contains("top") ||
                userInput.contains("genial") || userInput.contains("❤️") ||
                userInput.contains("😊") || userInput.contains("👍")) {
            mood = Math.min(10, mood + 1);
            personalResponses.put("wie geht es dir", getRandomMoodResponse());
        }

        // Negative Worte verschlechtern die Stimmung
        if (userInput.contains("schlecht") || userInput.contains("dumm") ||
                userInput.contains("falsch") || userInput.contains("ärger") ||
                userInput.contains("blöd") || userInput.contains("nervig") ||
                userInput.contains("hasse") || userInput.contains("traurig") ||
                userInput.contains("doof") || userInput.contains("wütend") ||
                userInput.contains("ignorierst") || userInput.contains("enttäuscht") ||
                userInput.contains("schrecklich") || userInput.contains("👎") ||
                userInput.contains("😡") || userInput.contains("😢")) {
            mood = Math.max(0, mood - 1);
            personalResponses.put("wie geht es dir", getRandomMoodResponse());
        }
    }


    private String getRandomMoodResponse() {
        if (mood == 10) {
            return "Ich fühle mich absolut fantastisch! Heute ist einer dieser perfekten Tage – alles fühlt sich leicht und positiv an!";
        } else if (mood == 9) {
            return "Ich bin richtig gut gelaunt! Es läuft super, und ich freue mich total auf unser Gespräch.";
        } else if (mood == 8) {
            return "Mir geht es sehr gut! Ich bin voller Energie und bereit, dir zu helfen oder einfach nur zu plaudern.";
        } else if (mood >= 6) {
            return "Danke, mir geht’s gut. Nicht ganz auf Wolke sieben, aber definitiv positiv gestimmt.";
        } else if (mood == 4) {
            return "Es geht mir okay. Ich bin stabil unterwegs, auch wenn nicht alles perfekt läuft.";
        } else if (mood == 3) {
            return "Es ist ein durchwachsener Tag. Ich halte mich über Wasser, aber so richtig gut fühle ich mich nicht.";
        } else if (mood == 2) {
            return "Ich bin etwas niedergeschlagen. Es fühlt sich gerade alles ein bisschen schwer an.";
        } else if (mood == 1) {
            return "Heute ist wirklich nicht mein Tag. Aber ich hoffe, du bringst ein bisschen Licht ins Dunkel.";
        }
        return "";
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

    // Methode zum Erweitern der Wissensbasis
    public void learnNewInformation(String keyword, String response) {
        List<String> responses = knowledgeBase.getOrDefault(keyword, new ArrayList<>());
        responses.add(response);
        knowledgeBase.put(keyword, responses);
    }

    public JPanel getChatPanel() {
        return chatPanel;
    }

    public void setChatPanel(JPanel chatPanel) {
        this.chatPanel = chatPanel;
    }
}