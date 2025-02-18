import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Stack;

public class Calculator_JavaSwing extends JFrame implements ActionListener {
    private JTextField textField;
    private JTextArea historyArea;
    private String currentInput = "";

    public Calculator_JavaSwing() {
        setTitle("Calculator");
        setSize(300, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        historyArea = new JTextArea();
        historyArea.setFont(new Font("Arial", Font.PLAIN, 16));
        historyArea.setEditable(false);
        historyArea.setBackground(new Color(230, 230, 230));
        add(new JScrollPane(historyArea), BorderLayout.CENTER);

        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.BOLD, 30));
        textField.setHorizontalAlignment(JTextField.RIGHT);
        textField.setEditable(false);
        textField.setBackground(new Color(245, 245, 245));
        add(textField, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 5, 5));

        String[] buttons = {
            "C", "<<", "%", "^",
            "7", "8", "9", "+",
            "4", "5", "6", "-",
            "1", "2", "3", "*",
            ".", "0", "=", "/"
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.addActionListener(this);

            if (text.equals("=")) {
                button.setFont(new Font("Arial", Font.PLAIN, 28));
                button.setBackground(new Color(255, 127, 80));
                button.setForeground(Color.white);
            } else if (text.matches("[0-9.]")) {
                button.setBackground(Color.lightGray);
            } else {
                button.setBackground(new Color(5, 153, 140));
                button.setForeground(Color.black);
            }

            button.setOpaque(true);
            button.setBorderPainted(false);
            panel.add(button);
        }

        add(panel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        try {
            if (command.matches("[0-9.]")) {
                currentInput += command;
                textField.setText(currentInput);
            } else if (command.matches("[+\\-*/^]")) {
                if (!currentInput.isEmpty() && !isLastCharOperator()) {
                    currentInput += command;
                    textField.setText(currentInput);
                }
            } else if (command.equals("%")) {
                if (!currentInput.isEmpty() && !isLastCharOperator()) {
                    currentInput += "/100";
                    textField.setText(currentInput);
                }
            } else if (command.equals("=")) {
                evaluateExpression();
            } else if (command.equals("C")) {
                currentInput = "";
                textField.setText("");
                historyArea.setText(""); // Clear history
            } else if (command.equals("<<")) { // Undo functionality
                if (!currentInput.isEmpty()) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                    textField.setText(currentInput);
                }
            }
        } catch (Exception ex) {
            textField.setText("Error");
            currentInput = "";
        }
    }

    private void evaluateExpression() {
        try {
            double result = evaluate(currentInput);
            if (Double.isInfinite(result)) { // Check for division by zero
                textField.setText("Error");
                currentInput = "";
            } else {
                updateHistory(currentInput + " = " + format(result));
                currentInput = String.valueOf(result);
                textField.setText(currentInput);
            }
        } catch (Exception e) {
            textField.setText("Error");
            currentInput = "";
        }
    }

    private void updateHistory(String calculation) {
        historyArea.append(calculation + "\n");
    }

    private String format(double value) {
        DecimalFormat df = new DecimalFormat("#.######");
        return df.format(value);
    }

    private boolean isLastCharOperator() {
        return !currentInput.isEmpty() && "+-*/^".indexOf(currentInput.charAt(currentInput.length() - 1)) != -1;
    }

    // Custom expression evaluator using Stacks
    private double evaluate(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        char[] tokens = expression.toCharArray();
        StringBuilder numberBuffer = new StringBuilder();

        for (int i = 0; i < tokens.length; i++) {
            char token = tokens[i];

            if (Character.isDigit(token) || token == '.') {
                numberBuffer.append(token);
            } else {
                if (numberBuffer.length() > 0) {
                    numbers.push(Double.parseDouble(numberBuffer.toString()));
                    numberBuffer.setLength(0);
                }

                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(token);
            }
        }

        if (numberBuffer.length() > 0) {
            numbers.push(Double.parseDouble(numberBuffer.toString()));
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
        }
        return -1;
    }

    private double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return (b == 0) ? Double.POSITIVE_INFINITY : a / b; // Handle division by zero
            case '^': return Math.pow(a, b);
        }
        return 0;
    }

    public static void main(String[] args) { 
        SwingUtilities.invokeLater(() -> new Calculator_JavaSwing().setVisible(true));
    }
}