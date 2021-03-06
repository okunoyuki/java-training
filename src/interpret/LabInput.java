package interpret;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JTextField;

public class LabInput {
    private static final int DEFAULT_TEXT_FIELD_COLUMN = 5;
    private static final Boolean[] BOOLEAN_OPTIONS = {true, false};
    private static final String USE_LITERAL_OPTION = "New literal";
    private static final String REGEX_VALID_VARIABLE_NAME = "[a-zA-Z_\\p{Sc}]+[a-zA-Z0-9_\\p{Sc}]*";
    private static final String JAVA_KEYWORDS[] = { "abstract", "assert", "boolean",
            "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "extends", "false",
            "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native",
            "new", "null", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true",
            "try", "void", "volatile", "while" };

    enum InputType{ 
        VARIABLE_NAME, INDEX, BOOLEAN, CHAR, BYTE, SHORT, INT, 
        LONG, FLOAT, DOUBLE, STRING, COLOR, OTHER_OBJECTS
    }
    private Class<?> type;
    private Variable[] variableOptions;
    private InputType inputType;
    private Component[] inputComponents;
    private Object validatedInput;
    
    private LabInput() {
    }
    
    public static LabInput create(InputType inputType) {
        if (inputType != InputType.VARIABLE_NAME && inputType != InputType.INDEX) {
            throw new IllegalArgumentException("Use this method only for InputType.VARIABLE_NAME or InputType.INDEX.");
        }
        LabInput labInput = new LabInput();
        labInput.inputType = inputType;
        labInput.inputComponents = createComponents(labInput.inputType);
        return labInput;
    }
    
    public static LabInput create(Class<?> type, Variable[] variableOptions) {
        LabInput labInput = new LabInput();
        labInput.type = type;
        labInput.variableOptions = variableOptions;
        if (type == boolean.class) {
            labInput.inputType = InputType.BOOLEAN;
            labInput.inputComponents = createBooleanComponents(variableOptions);
        } else if (type == char.class) {
            labInput.inputType = InputType.CHAR;
            labInput.inputComponents = createTextFieldAndComboBox(variableOptions);
        } else if (type == byte.class) {
            labInput.inputType = InputType.BYTE;
            labInput.inputComponents = createTextFieldAndComboBox(variableOptions);
        } else if (type == short.class) {
            labInput.inputType = InputType.SHORT;
            labInput.inputComponents = createTextFieldAndComboBox(variableOptions);
        } else if (type == int.class) {
            labInput.inputType = InputType.INT;
            labInput.inputComponents = createTextFieldAndComboBox(variableOptions);
        } else if (type == long.class) {
            labInput.inputType = InputType.LONG;
            labInput.inputComponents = createTextFieldAndComboBox(variableOptions);
        } else if (type == float.class) {
            labInput.inputType = InputType.FLOAT;
            labInput.inputComponents = createTextFieldAndComboBox(variableOptions);
        } else if (type == double.class) {
            labInput.inputType = InputType.DOUBLE;
            labInput.inputComponents = createTextFieldAndComboBox(variableOptions);
        } else if (type == String.class) {
            labInput.inputType = InputType.STRING;
            labInput.inputComponents = createTextFieldAndComboBox(variableOptions);
        } else if (type == Color.class) {
            labInput.inputType = InputType.COLOR;
            labInput.inputComponents = createVariableComboBoxInArray(variableOptions);
        } else {
            labInput.inputType = InputType.OTHER_OBJECTS;
            labInput.inputComponents = createVariableComboBoxInArray(variableOptions);
        }
        return labInput;
    }
    
    private static Component[] createComponents(InputType inputType) {
        switch (inputType) {
        case VARIABLE_NAME:
            Component[] variableNameCompos = {new JTextField(DEFAULT_TEXT_FIELD_COLUMN)};
            return variableNameCompos;
        case INDEX:
            Component[] indexCompos = {new JTextField(DEFAULT_TEXT_FIELD_COLUMN)};
            return indexCompos;
        default:
            throw new IllegalArgumentException("Use this method only for InputType.VARIABLE_NAME or InputType.INDEX.");
        }
    }
    private static Component[] createBooleanComponents(Variable[] variableOptions) {
        JComboBox<Boolean> literalComboBox = new JComboBox<>(BOOLEAN_OPTIONS);
        JComboBox<Object> variableComboBox = createVariableComboBox(variableOptions, true);
        setVariableComboBoxActionListener(variableComboBox, literalComboBox);
        Component[] booleanCompos = {variableComboBox, literalComboBox};
        return booleanCompos;
    }
    private static Component[] createTextFieldAndComboBox(Variable[] variableOptions) {
        JTextField textField = new JTextField(DEFAULT_TEXT_FIELD_COLUMN);
        JComboBox<Object> variableComboBox = createVariableComboBox(variableOptions, true);
        setVariableComboBoxActionListener(variableComboBox, textField);
        Component[] compos = {variableComboBox, textField};
        return compos;
    }
    private static Component[] createVariableComboBoxInArray(Variable[] variableOptions) {
        Component[] array = {createVariableComboBox(variableOptions, false)};
        return array;
    }
    private static JComboBox<Object> createVariableComboBox(Variable[] variableOptions, boolean withLiteralOption) {
        Object[] objects = null;
	if (withLiteralOption) {
	        objects = new Object[variableOptions.length + 1];
	        objects[0] = USE_LITERAL_OPTION;
	        System.arraycopy(variableOptions, 0, objects, 1, variableOptions.length);	    
	} else {
	        objects = new Object[variableOptions.length];
	        System.arraycopy(variableOptions, 0, objects, 0, variableOptions.length);	    
	}
        JComboBox<Object> variableComboBox = new JComboBox<>(objects);
        return variableComboBox;
    }
    private static void setVariableComboBoxActionListener(JComboBox<Object> variableComboBox, JComboBox<Boolean> literalComboBox) {
        variableComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<Object> comboBox = (JComboBox<Object>)e.getSource();
                if (comboBox.getSelectedItem().equals(USE_LITERAL_OPTION)) {
                    literalComboBox.setVisible(true);
                } else {
                    literalComboBox.setVisible(false);
                }
                literalComboBox.getParent().validate();
                literalComboBox.getParent().repaint();
            }
        });        
    }
    private static void setVariableComboBoxActionListener(JComboBox<Object> variableComboBox, JTextField literalTextField) {
        variableComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<Object> comboBox = (JComboBox<Object>)e.getSource();
                if (comboBox.getSelectedItem().equals(USE_LITERAL_OPTION)) {
                    literalTextField.setVisible(true);
                } else {
                    literalTextField.setVisible(false);
                }
                literalTextField.getParent().validate();
                literalTextField.getParent().repaint();
            }
        });        
    }
    public Class<?> getType() {
        return type;
    }
    public String getSimpleTypeName() {
        return ReflectionTools.getSimpleName(type);
    }
    public Variable[] getVariableOptions() {
        return variableOptions;
    }
    public InputType getInputType() {
        return inputType;
    }
    public Component[] getInputComponents() {
        return inputComponents;
    }
    public Object getValidatedInput() {
        return validatedInput;
    }
    
    public boolean validate() {
        switch (inputType) {
        case VARIABLE_NAME:
            return validateVariableName();
        case INDEX:
            return validateIndex();
        case BOOLEAN:
            return validateBoolean();
        case CHAR:
            return validateChar();
        case BYTE:
            return validateByte();
        case SHORT:
            return validateShort();
        case INT:
            return validateInt();
        case LONG:
            return validateLong();
        case FLOAT:
            return validateFloat();
        case DOUBLE:
            return validateDouble();
        case STRING:
            return validateString();
        case COLOR:
        case OTHER_OBJECTS:
            return validateOtherObject();
        default:
            throw new IllegalStateException("Something wrong happened");
        }
    }
    private boolean validateVariableName() {
        String input = ((JTextField)inputComponents[0]).getText();
        boolean result = true;
        if (!Pattern.matches(REGEX_VALID_VARIABLE_NAME, input)) {
            result = false;
        }
        if (isJavaKeyword(input)) {
            result = false;
        }
        if (result) {
            validatedInput = input;
        }
        return result;
    }
    private boolean validateIndex() {
        String input = ((JTextField)inputComponents[0]).getText();
        int inputInt;
        try {
            inputInt = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        if (inputInt < 0) {
            return false;
        }
        validatedInput = inputInt;
        return true;
    }
    private boolean validateBoolean() {
        @SuppressWarnings("unchecked")
        JComboBox<Object> variableComboBox = (JComboBox<Object>)inputComponents[0];
        if (variableComboBox.getSelectedIndex() == 0) {
            @SuppressWarnings("unchecked")
            JComboBox<Boolean> literalComboBox = (JComboBox<Boolean>)inputComponents[1];
            validatedInput = literalComboBox.getItemAt(literalComboBox.getSelectedIndex()).booleanValue();
        } else {
            validatedInput = ((Variable)variableComboBox.getSelectedItem()).getValue();
        }
        return true;
    }
    private boolean validateChar() {
        @SuppressWarnings("unchecked")
        JComboBox<Object> variableComboBox = (JComboBox<Object>)inputComponents[0];
        if (variableComboBox.getSelectedIndex() != 0) {
            validatedInput = ((Variable)variableComboBox.getSelectedItem()).getValue();
            return true;
        }
        
        String input = ((JTextField)inputComponents[1]).getText();
        if (input.length() == 1) {
            validatedInput = input.charAt(0);                
        } else if (input.length() == 6 && input.charAt(0) == '\\' && input.charAt(1) == 'u') {
            validatedInput = (char)Integer.parseInt(input.substring(2, 6), 16);
        } else {
            return false;
        }
        return true;
    }
    private boolean validateByte() {
        @SuppressWarnings("unchecked")
        JComboBox<Object> variableComboBox = (JComboBox<Object>)inputComponents[0];
        if (variableComboBox.getSelectedIndex() != 0) {
            validatedInput = ((Variable)variableComboBox.getSelectedItem()).getValue();
            return true;
        }
        String input = ((JTextField)inputComponents[1]).getText();
        try {
            validatedInput = Byte.parseByte(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean validateShort() {
        @SuppressWarnings("unchecked")
        JComboBox<Object> variableComboBox = (JComboBox<Object>)inputComponents[0];
        if (variableComboBox.getSelectedIndex() != 0) {
            validatedInput = ((Variable)variableComboBox.getSelectedItem()).getValue();
            return true;
        }
        String input = ((JTextField)inputComponents[1]).getText();
        try {
            validatedInput = Short.parseShort(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean validateInt() {
        @SuppressWarnings("unchecked")
        JComboBox<Object> variableComboBox = (JComboBox<Object>)inputComponents[0];
        if (variableComboBox.getSelectedIndex() != 0) {
            validatedInput = ((Variable)variableComboBox.getSelectedItem()).getValue();
            return true;
        }
        String input = ((JTextField)inputComponents[1]).getText();
        try {
            validatedInput = Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean validateLong() {
        @SuppressWarnings("unchecked")
        JComboBox<Object> variableComboBox = (JComboBox<Object>)inputComponents[0];
        if (variableComboBox.getSelectedIndex() != 0) {
            validatedInput = ((Variable)variableComboBox.getSelectedItem()).getValue();
            return true;
        }
        String input = ((JTextField)inputComponents[1]).getText();
        try {
            validatedInput = Long.parseLong(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean validateFloat() {
        @SuppressWarnings("unchecked")
        JComboBox<Object> variableComboBox = (JComboBox<Object>)inputComponents[0];
        if (variableComboBox.getSelectedIndex() != 0) {
            validatedInput = ((Variable)variableComboBox.getSelectedItem()).getValue();
            return true;
        }
        String input = ((JTextField)inputComponents[1]).getText();
        try {
            validatedInput = Float.parseFloat(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean validateDouble() {
        @SuppressWarnings("unchecked")
        JComboBox<Object> variableComboBox = (JComboBox<Object>)inputComponents[0];
        if (variableComboBox.getSelectedIndex() != 0) {
            validatedInput = ((Variable)variableComboBox.getSelectedItem()).getValue();
            return true;
        }
        String input = ((JTextField)inputComponents[1]).getText();
        try {
            validatedInput = Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean validateString() {
        @SuppressWarnings("unchecked")
        JComboBox<Object> variableComboBox = (JComboBox<Object>)inputComponents[0];
        if (variableComboBox.getSelectedIndex() != 0) {
            validatedInput = ((Variable)variableComboBox.getSelectedItem()).getValue();
            return true;
        }
        validatedInput = ((JTextField)inputComponents[1]).getText();
        return true;
    }
    private boolean validateOtherObject() {
        @SuppressWarnings("unchecked")
        JComboBox<Object> variableComboBox = (JComboBox<Object>)inputComponents[0];
        validatedInput = ((Variable)variableComboBox.getSelectedItem()).getValue();
        return true;
    }
    private static boolean isJavaKeyword(String word) {
        return Arrays.binarySearch(JAVA_KEYWORDS, word) >= 0;
    }
    
}
