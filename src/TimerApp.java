import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerApp {
    private final JLabel timerLabel;
    private final JTextField inputField;
    private final JButton sendButton;

    public TimerApp() {
        // GUI erstellen
        JFrame frame = new JFrame("Timer App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new BorderLayout());

        // Timer-Label
        timerLabel = new JLabel("Timer: 02:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        timerLabel.setForeground(new Color(0, 128, 0)); // Grün
        frame.add(timerLabel, BorderLayout.NORTH);

        // Eingabefeld und Button
        JPanel inputPanel = new JPanel();
        inputField = new JTextField(10);
        sendButton = new JButton("Start Timer");

        inputPanel.add(inputField);
        inputPanel.add(sendButton);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Button-ActionListener
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userMessage = inputField.getText();
                if (userMessage.equalsIgnoreCase("timer 2 min")) {
                    startTimer(); // Timer starten
                    inputField.setEnabled(false);
                    sendButton.setEnabled(false);
                } else {
                    addMessage("Bitte 'timer 2 min' eingeben.", false);
                }
            }
        });

        // Fenster sichtbar machen
        frame.setVisible(true);
    }

    private void startTimer() {
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
    }

    private void addMessage(String message, boolean isError) {
        JOptionPane.showMessageDialog(null, message, isError ? "Fehler" : "Info", isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TimerApp::new);
    }
}