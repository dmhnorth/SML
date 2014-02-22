package sml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;

/*
 * The translator of a <b>S</b><b>M</b>al<b>L</b> program.
 */
public class Translator {

	// word + line is the part of the current line that's not yet processed
	// word has no whitespace
	// If word and line are not empty, line begins with whitespace
	private String line = "";
	private Labels labels; // The labels of the program being translated
	private ArrayList<Instruction> program; // The program to be created
	private String fileName; // source file of SML code

	private static final String SRC = "src/";

	public Translator(String fileName) {
		this.fileName = SRC + fileName;
	}

	// translate the small program in the file into lab (the labels) and
	// prog (the program)
	// return "no errors were detected"
	public boolean readAndTranslate(Labels lab, ArrayList<Instruction> prog) {
		Scanner sc; // Scanner attached to the file chosen by the user
		try {
			sc = new Scanner(new File(fileName));
		} catch (IOException ioE) {
			System.out.println("File: IO error to start " + ioE.getMessage());
			return false;
		}
		labels = lab;
		labels.reset();
		program = prog;
		program.clear();

		try {
			line = sc.nextLine();
		} catch (NoSuchElementException ioE) {
			sc.close();
			return false;
		}

		// Each iteration processes line and reads the next line into line
		while (line != null) {
			// Store the label in label
			String label = scan();

			if (label.length() > 0) {
				Instruction ins = getInstruction(label);
				if (ins != null) {
					labels.addLabel(label);
					program.add(ins);
				}
			}

			try {
				line = sc.nextLine();
			} catch (NoSuchElementException ioE) {
				sc.close();
				return false;
			}
		}
		sc.close();
		return true;
	}

	// line should consist of an MML instruction, with its label already
	// removed. Translate line into an instruction with label label
	// and return the instruction
	public Instruction getInstruction(String label) {

		if (line.equals(""))
			return null;

		String ins = scan();
		
		Properties props = new Properties();
		
		try {
			FileInputStream propText = new FileInputStream(SRC + "instruction.properties");
			props.load(propText);
			String className = props.getProperty(ins);
			
			Class<?> instructionClass = Class.forName(className);
			
			Constructor<?>[] constructors = instructionClass.getConstructors();
			
			//Assumes the last constructor is the one that is required.
			Constructor<?> constructor = constructors[constructors.length-1];
			
			Class<?>[] parameters = constructors[constructors.length-1].getParameterTypes();
			
			Object[] values = new Object[parameters.length];
			
			values[0] = label;
			
			for (int i = 1; i < values.length; i++) {
				if (parameters[i] == (Class.forName("java.lang.String"))) {
					values[i] = scan();
				}else if (parameters[i] == int.class){
					values[i] = scanInt();
				}
			}
			return (Instruction) constructor.newInstance(values);
			
		
		
		} catch (Exception e) {
			System.out.println("The program failed to reflect accurately.");
			e.printStackTrace();
		}
		return null;
			
			
		}
	

	/*
	 * Return the first word of line and remove it from line. If there is no
	 * word, return ""
	 */
	public String scan() {
		line = line.trim();
		if (line.length() == 0)
			return "";

		int i = 0;
		while (i < line.length() && line.charAt(i) != ' '
				&& line.charAt(i) != '\t') {
			i = i + 1;
		}
		String word = line.substring(0, i);
		line = line.substring(i);
		return word;
	}

	// Return the first word of line as an integer. If there is
	// any error, return the maximum int
	public int scanInt() {
		String word = scan();
		if (word.length() == 0) {
			return Integer.MAX_VALUE;
		}

		try {
			return Integer.parseInt(word);
		} catch (NumberFormatException e) {
			return Integer.MAX_VALUE;
		}
	}
}