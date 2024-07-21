package myPackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

/**
 * Represents each of the lines in a TOY file as a {@code String}. Includes
 * methods to {@linkplain #isValid() check} if a line is valid TOY code and to
 * {@linkplain #toLine() parse} it into a proper {@link Line} object.
 */
class RawLine {
	String content;
	private static final String validLineRegex = "^(([\\da-fA-F]){2}): (([\\da-fA-F]){4})(.*)";

	/**
	 * Constructs a new {@code RawLine} object with provided {@code content}.
	 * 
	 * @param content Content of the line
	 */
	public RawLine(String content) {
		this.content = content;
	}

	/**
	 * Creates a {@code Matcher} object using provided {@code regex} String against
	 * {@code stringToMatch}.
	 * 
	 * @param regex         Regular expression to match
	 * @param stringToMatch String to match {@code regex} against
	 * @return compiled {@code Matcher} object
	 */
	private static Matcher createMatch(String regex, String stringToMatch) {
		Pattern pattern = Pattern.compile(regex); // create Pattern object from regex String passed as input
		Matcher matcher = pattern.matcher(stringToMatch); // create Matcher object from regex matched against input
		return matcher;
	}

	/**
	 * Checks whether a {@code RawLine} is valid. Matches {@link #validLineRegex}
	 * ({@value #validLineRegex}).
	 * 
	 * @return whether line is valid
	 */
	public boolean isValid() {
		Matcher validLineMatch = createMatch(validLineRegex, content); // Matcher from valid line regex and content
		return validLineMatch.find(); // see if regex matches content
	}

	/**
	 * Converts {@link #content} to {@code Line} object.
	 * 
	 * @return new {@code Line} object
	 */
	public Line toLine() {
		Line line;
		Matcher validLineMatch = createMatch(validLineRegex, content); // Matcher from valid line regex and content
		validLineMatch.find(); // see if regex matches content
		try {
			int lineNumber = Integer.parseInt(validLineMatch.group(1), 16); // line number capture group converted from
																			// hexadecimal string to int
			String instruction = validLineMatch.group(3); // instruction capture group
			String comment = validLineMatch.group(5); // comment capture group
			line = new Line(lineNumber, instruction, comment); // create new Line object from components
			return line;
		} catch (Exception e) { // method was called on invalid line
			if (TOYEmulator.INTERACT_WITH_CONSOLE) {
				e.printStackTrace(); // print error to sysout
			}
			return null;
		}
	}
}

/**
 * Represents each of the parts of a valid line of code in a TOY file. Includes
 * methods to {@linkplain #generateComment() generate} comments for lines and
 * {@linkplain #toInstruction convert} them to {@code Instruction} objects.
 */
class Line {
	int lineNumber;
	String instruction, comment;

	/**
	 * Constructs a new {@code Line} object.
	 * 
	 * @param lineNumber  Line number of the line (as decimal not hex)
	 * @param instruction Instruction of the line
	 * @param comment     Any text after the instruction (does not have to be
	 *                    correctly formatted)
	 */
	public Line(int lineNumber, String instruction, String comment) {
		this.lineNumber = lineNumber;
		this.instruction = instruction;
		this.comment = comment;
	}

	/**
	 * Gets the character at the specified index of a string (and converts it to
	 * upper case).
	 * 
	 * @param string String to get character from
	 * @param index  Index of the character in the {@code string}
	 * @return character as an upper case String
	 * @throws IndexOutOfBoundsException if the {@code index} argument is negative
	 *                                   or not less than the length of this string.
	 * @see String#charAt(int)
	 */
	private static String charAtIndex(String string, int index) {
		return Character.toString(string.charAt(index)).toUpperCase();
	}

	/**
	 * Converts {@link #instruction} to {@linkplain Instruction} object. Format of
	 * {@code Instruction} is determined based on the {@code opcode}. Note that all
	 * components of the instruction will be passed as upper case strings.
	 * 
	 * @return new {@code Instruction} object
	 */
	public Instruction toInstruction() {
		int format;
		String opcode, d, s, t, addr;

		opcode = charAtIndex(instruction, 0); // set opcode to first character of instruction
		if (opcode.matches("[1-6AB]")) { // 1, 2, 3, 4, 5, 6, A, B opcodes are all format 1
			if (opcode.matches("[AB]")) { // A and B work differently so give them their own format
				format = 3;
			} else {
				format = 1;
			}
		} else if (opcode.matches("[7-9CDF]")) { // 7, 8, 9, C, D, F opcodes are all format 2
			format = 2;
		} else { // E and 0 opcodes have "no format" (treat as format 4 and 5)
			format = (opcode.equals("E") ? 4 : 5); // format = 4 if opcode is E, else 5 (opcode is 0)
		}

		d = charAtIndex(instruction, 1); // set d to second character of instruction
		s = charAtIndex(instruction, 2); // set s to third character of instruction
		t = charAtIndex(instruction, 3); // set t to fourth character of instruction
		addr = charAtIndex(instruction, 2) + charAtIndex(instruction, 3); // set addr to third and fourth characters

		return new Instruction(format, opcode, d, s, t, addr); // create new instruction object from component strings
	}

	/**
	 * Generates a {@code comment} for the {@code Line} based on the
	 * {@code instruction}. Note that if {@code lineNumber} is less than 16 it is a
	 * constant, not code.
	 * 
	 * @return generated comment
	 * @see Instruction#generateComment()
	 * @see TOYFile#format()
	 */
	public String generateComment() {
		if (this.lineNumber < 0x10) { // line number before 16 so not code
			return "constant 0x" + instruction.toUpperCase();
		} else {
			return toInstruction().generateComment(); // generate comment based on instruction
		}
	}

	/**
	 * Gets {@link #lineNumber} from a {@code Line} object.
	 * 
	 * @return {@code lineNumber}
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Gets {@link #instruction} from a {@code Line} object.
	 * 
	 * @return {@code instruction}
	 */
	public String getInstruction() {
		return instruction;
	}
}

/**
 * Represents a TOY instruction with {@code String}s as the parts of the
 * instruction. Includes methods to {@linkplain #execute(Program) execute} a
 * given instruction as well as {@linkplain #generateComment() generate} a
 * comment for it.
 */
class Instruction {
	int format;
	String opcode, d, s, t, addr;
	private static final short INPUT_OUTPUT_ADDRESS = 0xFF; // 255

	/**
	 * Constructs a new {@code Instruction} object.
	 * 
	 * @param format Format of the instruction
	 * @param opcode Opcode of the instruction (first character)
	 * @param d      "d" of the instruction (second character)
	 * @param s      "s" of the instruction (third character)
	 * @param t      "t" of the intsruction (fourth character)
	 * @param addr   Address of the instruction (third and fourth characters)
	 */
	public Instruction(int format, String opcode, String d, String s, String t, String addr) {
		this.format = format;
		this.opcode = opcode;
		this.d = d;
		this.s = s;
		this.t = t;
		this.addr = addr;
	}

	/**
	 * Generates a comment string based on the components of the given
	 * {@code Instruction}. Format of the comment is based on the {@code opcode}.
	 * <br>
	 * Special cases ({@code □} can be any hexdigit, {@code x} will be the same
	 * hexdigit):
	 * <ul>
	 * <code>
	 * <li>10□□</li><li>1□0□</li><li>1□00</li><li>1□□0</li>
	 * <li>20□□</li><li>2□0□</li><li>2□00</li><li>2□□0</li>
	 * <li>30□□</li><li>3□0□</li><li>3□□0</li><li>3□xx</li><li>3xxx</li>
	 * <li>40□□</li><li>4□0□</li><li>4□00</li><li>4□□0</li>
	 * <li>50□□</li><li>5□0□</li><li>5□□0</li><li>5xx0</li>
	 * <li>60□□</li><li>6□0□</li><li>6□□0</li><li>6xx0</li>
	 * <li>70□□</li>
	 * <li>8□FF</li><li>80□□</li>
	 * <li>9□FF</li>
	 * <li>A0□□</li>
	 * <li>C0□□</li>
	 * <li>D0□□</li>
	 * <li>F0□□</li>
	 * </code>
	 * </ul>
	 * 
	 * @return generated comment
	 * @see Line#generateComment()
	 */
	public String generateComment() {
		String comment;

		switch (opcode) {
		case "0": // halt
			comment = "halt";
			break;
		case "1": // add
			if (d.equals("0")) { // d is 0
				comment = "no-op";
			} else if (s.equals("0")) { // only s is 0
				comment = "R[" + d + "] <- R[" + t + "]";
				if (t.equals("0")) { // s and t are 0
					comment = "R[" + d + "] <- 0000";
				}
			} else if (t.equals("0")) { // only t is 0
				comment = "R[" + d + "] <- R[" + s + "]";
			} else {
				comment = "R[" + d + "] <- R[" + s + "] + R[" + t + "]";
			}
			break;
		case "2": // subtract
			if (d.equals("0")) { // d is 0
				comment = "no-op";
			} else if (s.equals("0")) { // only s is 0
				comment = "R[" + d + "] <- -R[" + t + "]";
				if (t.equals("0")) { // s and t are 0
					comment = "R[" + d + "] <- 0000";
				}
			} else if (t.equals("0")) { // only t is 0
				comment = "R[" + d + "] <- R[" + s + "]";
			} else {
				comment = "R[" + d + "] <- R[" + s + "] - R[" + t + "]";
			}
			break;
		case "3": // and
			if (d.equals("0")) { // d is 0
				comment = "no-op";
			} else if (s.equals("0") || t.equals("0")) { // s or t are 0
				comment = "R[" + d + "] <- 0000";
			} else if (s.equals(t)) { // s and t are the same
				comment = "R[" + d + "] <- R[" + s + "]"; // or R[t], does not matter
				if (d.equals(s)) { // d and s and t are the same
					comment = "no-op";
				}
			} else {
				comment = "R[" + d + "] <- R[" + s + "] & R[" + t + "]";
			}
			break;
		case "4": // xor
			if (d.equals("0")) { // d is 0
				comment = "no-op";
			} else if (s.equals("0")) { // only s is 0
				comment = "R[" + d + "] <- R[" + t + "]";
				if (t.equals("0")) { // s and t are 0
					comment = "R[" + d + "] <- 0000";
				}
			} else if (t.equals("0")) { // only t is 0
				comment = "R[" + d + "] <- R[" + s + "]";
			} else {
				comment = "R[" + d + "] <- R[" + s + "] ^ R[" + t + "]";
			}
			break;
		case "5": // left shift
			if (d.equals("0")) { // d is 0
				comment = "no-op";
			} else if (s.equals("0")) { // s is 0
				comment = "R[" + d + "] <- 0000";
			} else if (t.equals("0")) { // only t is 0
				comment = "R[" + d + "] <- R[" + s + "]";
				if (d.equals(s)) { // d and s are the same, t is 0
					comment = "no-op";
				}
			} else {
				comment = "R[" + d + "] <- R[" + s + "] << R[" + t + "]";
			}
			break;
		case "6": // right shift
			if (d.equals("0")) { // d is 0
				comment = "no-op";
			} else if (s.equals("0")) { // s is 0
				comment = "R[" + d + "] <- 0000";
			} else if (t.equals("0")) { // only t is 0
				comment = "R[" + d + "] <- R[" + s + "]";
				if (d.equals(s)) { // d and s are the same, t is 0
					comment = "no-op";
				}
			} else {
				comment = "R[" + d + "] <- R[" + s + "] >> R[" + t + "]";
			}
			break;
		case "7": // load address
			if (d.equals("0")) { // d is 0
				comment = "no-op";
			} else {
				comment = "R[" + d + "] <- 00" + addr;
			}
			break;
		case "8": // load
			if (addr.equals("FF")) { // addr is stdin
				comment = "read R[" + d + "]";
			} else if (d.equals("0")) { // d is 0, addr is not stdin
				comment = "no-op";
			} else {
				comment = "R[" + d + "] <- M[" + addr + "]";
			}
			break;
		case "9": // store
			if (addr.equals("FF")) { // addr is stdout
				comment = "write R[" + d + "]";
			} else {
				comment = "M[" + addr + "] <- R[" + d + "]";
			}
			break;
		case "A": // load indirect
			if (d.equals("0")) { // d is 0
				comment = "no-op";
			} else {
				comment = "R[" + d + "] <- M[R[" + t + "]]";
			}
			break;
		case "B": // store indirect
			comment = "M[R[" + t + "]] <- R[" + d + "]";
			break;
		case "C": // branch zero
			if (d.equals("0")) { // d is 0, R[0] == 0 will always be true
				comment = "goto " + addr;
			} else {
				comment = "if (R[" + d + "] == 0) goto " + addr;
			}
			break;
		case "D": // branch positive
			if (d.equals("0")) { // d is 0, R[0] > 0 will never be true
				comment = "no-op";
			} else {
				comment = "if (R[" + d + "] > 0) goto " + addr;
			}
			break;
		case "E": // jump register
			comment = "goto R[" + d + "]";
			break;
		case "F": // jump and link
			if (d.equals("0")) { // d is 0
				comment = "goto " + addr;
			} else {
				comment = "R[" + d + "] <- PC; goto " + addr;
			}
			break;
		default:
			comment = "";
			break;
		}

		return comment;
	}

	/**
	 * Loads given user input to the {@code stdin} list of a given program. First
	 * splits the input by any non-hex characters, then splits each part into chunks
	 * of 4. Each chunks is padded with 0s then added to {@code program.stdin}. If
	 * there is nothing parseable in the input, nothing will be added to stdin.
	 * 
	 * @param input   input of user
	 * @param program {@link Program} to pass input to
	 */
	static void inputToStdin(String input, Program program) {
		String splitterRegex, allowedCharacters, nonAllowedCharsRegex;
		int chunkSize = 4; // number of characters to chunk into
		splitterRegex = "(?<=\\G.{" + chunkSize + "})"; // matches chunkSize characters from the end of last match

		allowedCharacters = "0-9a-fA-F"; // all possible hex digits
		nonAllowedCharsRegex = "[^" + allowedCharacters + "]"; // regex that matches everything except allowedCharacters

		input = input.toUpperCase(); // convert input to upper case

		String[] splitInput = input.split(nonAllowedCharsRegex); // split input by non hex characters

		for (String splitPart : splitInput) { // iterate over parts of input
			if (!splitPart.isBlank()) { // splitInput will include empty strings, ignore them
				String[] chunkedSplitPart = splitPart.split(splitterRegex); // split splitPart into chunks of chunkSize

				for (String chunk : chunkedSplitPart) { // iterate over chunks
					// get the subtring of some "0"s and chunk, starting from the index of the
					// length of chunk (this will cut off any unneeded "0"s
					chunk = ("0".repeat(chunkSize) + chunk).substring(chunk.length()); // left-pad chunk with "0"s until
																						// length is chunkSize
					program.stdin.add(chunk); // append chunk to stdin list
				}
			}
		}
	}

	/**
	 * Loads a hex value from the memory of a given {@linkplain Program}. If
	 * {@code address} is {@code 0xFF}, the value will be loaded from stdin instead,
	 * then saved to M[FF]. In this case, it will try to grab the first item from
	 * stdin, else retrieve a new value from the user.
	 * 
	 * @param address address to load the value from
	 * @param program program to access memory of
	 * @return hex value loaded from memory
	 * @throws MemoryAddressOutOfBoundsException if {@code address} is not within
	 *                                           memory range
	 * @throws MemoryUninitialisedException      if {@code memory} is not
	 *                                           initialised
	 * @see #inputToStdin(String, Program)
	 */
	private static String loadFromMemory(short address, Program program)
			throws MemoryUninitialisedException, MemoryAddressOutOfBoundsException {
		String value;
		Instruction[] memory = program.getMemory(); // get program's memory array

		if (outsideMemory(address)) { // address is not within memory range
			throw new MemoryAddressOutOfBoundsException();

		} else if (address == INPUT_OUTPUT_ADDRESS) { // addr is FF
			if (TOYEmulator.DEBUG) {
				if (TOYEmulator.INTERACT_WITH_CONSOLE) {
					System.out.println("program.stdin.size() = " + program.stdin.size());
					System.out.println("program.stdin = " + program.stdin);
				}
			}

			value = program.stdin.get(0); // value <- stdin[0]
			program.stdin.remove(0); // remove stdin[0]

		} else {
			if (memory[address] == null) { // memory at address is not initialised
				throw new MemoryUninitialisedException();
			}
			value = memory[address].asHex(); // value <- M[addr]
		}

		memory[address] = hexToInstruction(value); // save value back to M[addr] in case it was loaded from stdin

		return value;
	}

	/**
	 * Stores a hex value to the memory of a given {@linkplain Program}. If
	 * {@code address} is {@code 0xFF}, the value will be printed to stdout as well.
	 * 
	 * @param address address to store the value to
	 * @param value   value to store
	 * @param program program to access memory of
	 * @throws MemoryAddressOutOfBoundsException if {@code address} is not within
	 *                                           memory range
	 */
	private static void storeToMemory(short address, String value, Program program)
			throws MemoryAddressOutOfBoundsException {
		Instruction[] memory = program.getMemory(); // get program's memory array

		if (outsideMemory(address)) { // address is not within memory range
			throw new MemoryAddressOutOfBoundsException();

		} else if (address == INPUT_OUTPUT_ADDRESS) { // addr is FF
			program.stdout.add(value); // append value to stdout list
			if (TOYEmulator.INTERACT_WITH_CONSOLE) {
				System.out.println(value);
			}
		}

		memory[address] = hexToInstruction(value); // M[addr] <- R[d]
	}

	/**
	 * Converts a hex value to an {@linkplain Instruction}. First converts to a
	 * {@link Line} object at line 16 with an empty comment, then
	 * {@link Line#toInstruction() converts} that to an instruction.
	 * 
	 * @param hex hex value to convert
	 * @return hex converted to instruction
	 */
	private static Instruction hexToInstruction(String hex) {
		return new Line(0x10, hex, "").toInstruction();
	}

	/**
	 * Checks whether a short is outside the range of valid memory addresses in TOY
	 * - i.e. not between the inclusive range {@code 0x00} (0) to {@code 0xFF}
	 * (255).
	 * 
	 * @param x short to check
	 * @return whether x is outside the range of valid memory addresses
	 */
	private static boolean outsideMemory(short x) {
		return x < 0x00 || x > 0xFF; // x is <0 or >255
	}

	/**
	 * Checks whether an integer is within the bounds for a short in java, which is
	 * the same as the bounds for any number in TOY - between
	 * {@value Short#MIN_VALUE} and {@value Short#MAX_VALUE}, inclusive.
	 * 
	 * @param integer integer to check
	 * @throws OverflowException if {@code integer} is outside short bounds
	 */
	private static void checkInShortBounds(int integer) throws OverflowException {
		if (integer < Short.MIN_VALUE || integer > Short.MAX_VALUE) { // integer is <-32768 or >32767
			throw new OverflowException();
		}
	}

	/**
	 * Converts a hex string to a short. Initially parses {@code string} as an
	 * integer to avoid overflowing.
	 * 
	 * @param string string to convert
	 * @return {@code string} converted to a short
	 * @see #shortToHex(short)
	 */
	static short hexToShort(String string) {
		int hexAsInt = Integer.parseInt(string, 16); // parse as integer to prevent errors from overflowing here
		return (short) hexAsInt;
	}

	/**
	 * Converts a short to a hex string. Uses
	 * {@link String#format(String, Object...)} to pad it with up to four "0"s if
	 * necessary.
	 * 
	 * @param shortVal short to convert
	 * @return {@code shortVal} converted to a hex string
	 * @see #hexToShort(String)
	 */
	static String shortToHex(short shortVal) {
		return String.format("%04X", shortVal);
	}

	/**
	 * Executes an instruction. Treats the instruction differently depending on its
	 * {@link #format}. Some instructions behave differently to the default and so
	 * return a different result code.
	 * 
	 * @param program program to execute the instruction in the context of
	 * @return an integer representing the result of the execution:
	 *         <ul>
	 *         <li>{@code -1} when the program should be halted</li>
	 *         <li>{@code 0} for regular instructions</li>
	 *         <li>{@code 1} when the program counter should not be incremented</li>
	 *         </ul>
	 * @throws RegisterUninitialisedException     if a register required by the
	 *                                            instruction is uninitialised
	 * @throws RegisterIndexOutOfBoundsException  if the instruction attempted to
	 *                                            change R[0]
	 * @throws OverflowException                  if the result of an arithmetic
	 *                                            operation is outside the range of
	 *                                            valid TOY numbers
	 * @throws ShiftMagnitudeOutOfBoundsException if the magnitude used for a shift
	 *                                            operation was outside the allowed
	 *                                            range
	 * @throws MemoryUninitialisedException       if a memory address the
	 *                                            instruction accessed is
	 *                                            uninitialised
	 * @throws ProgramCounterOutOfBoundsException if the instruction attempted to
	 *                                            set the program counter to a value
	 *                                            outside the allowed range
	 * @throws MemoryAddressOutOfBoundsException  if an address is outside the
	 *                                            allowed range of memory addresses
	 */
	public int execute(Program program) throws RegisterUninitialisedException, RegisterIndexOutOfBoundsException,
			OverflowException, ShiftMagnitudeOutOfBoundsException, MemoryUninitialisedException,
			ProgramCounterOutOfBoundsException, MemoryAddressOutOfBoundsException {
		String[] registers = program.getRegisters(); // get program's registers array
		short dest, sourceS, sourceT, address;
		registers[0] = "0000"; // reset R[0]

		if (TOYEmulator.DEBUG) {
			if (TOYEmulator.INTERACT_WITH_CONSOLE) {
				System.out.println(TOYFile.lineNumberToHex(program.getProgramCounter()) + ": " + generateComment()
						+ " (" + this + ")");
			}
		}

		if (changesD() && d.equals("0")) { // instruction changes d, d is 0
			if (!(opcode.equals("1") && s.equals("0") && t.equals("0"))) { // instruction is not 1000 (no-op)
				throw new RegisterIndexOutOfBoundsException();
			}
		}

		// convert instruction components to shorts
		// allows arithmetic and use as array indices
		dest = hexToShort(d);
		sourceS = hexToShort(s);
		sourceT = hexToShort(t);
		address = hexToShort(addr);

		if (needsD() && registers[dest] == null || needsS() && registers[sourceS] == null // instruction needs
				|| needsT() && registers[sourceT] == null) { // to access R[d], R[s] or R[t] and it is null
			throw new RegisterUninitialisedException();
		}

		switch (format) {
		case 1: // 1, 2, 3, 4, 5, 6 opcodes

			short operand1, operand2;
			int result = 0;

			operand1 = hexToShort(registers[sourceS]); // get R[s]
			operand2 = hexToShort(registers[sourceT]); // get R[t]

			if (opcode.equals("5") || opcode.equals("6")) { // opcode is 5 or 6
				if (operand2 < 0x0 || operand2 > 0xF) { // R[t] is negative or greater than 15 (out of allowed range)
					throw new ShiftMagnitudeOutOfBoundsException();
				}
			}

			switch (opcode) {
			case "1": // add
				result = operand1 + operand2; // result <- R[s] + R[t]
				break;
			case "2": // subtract
				result = operand1 - operand2; // result <- R[s] - R[t]
				break;
			case "3": // and
				result = operand1 & operand2; // result <- R[s] & R[t]
				break;
			case "4": // xor
				result = operand1 ^ operand2; // result <- R[s] ^ R[t]
				break;
			case "5": // left shift
				// result is calculated as short for shift operations because otherwise the
				// calculation may go out of bounds when we do not want it to
				result = (short) (operand1 << operand2); // result <- R[s] << R[t]
				break;
			case "6": // right shift
				result = (short) (operand1 >> operand2); // result <- R[s] >> R[t]
			}

			checkInShortBounds(result); // check if result is within allowed range of numbers in TOY
			registers[dest] = shortToHex((short) result); // R[d] <- result

			return 0;

		case 2: // 7, 8, 9, C, D, F opcodes

			// used for C and D only
			short valueToCheck = 0;
			if (opcode.equals("C") || opcode.equals("D")) { // opcode is C or D
				valueToCheck = hexToShort(registers[dest]); // valueToCheck <- R[d]
			}

			switch (opcode) {
			case "7": // load address
				registers[dest] = shortToHex(address); // R[d] <- addr
				break;
			case "8": // load
				registers[dest] = loadFromMemory(address, program); // R[d] <- M[addr]
				break;
			case "9": // store
				storeToMemory(address, registers[dest], program); // M[addr] <- R[d]
				break;
			case "C": // branch zero
				if (valueToCheck == 0) { // if (valueToCheck == 0)
					program.setProgramCounter(address); // PC <- addr
					return 1; // do not increment PC
				}
				break;
			case "D": // branch positive
				if (valueToCheck > 0) { // if (valueToCheck > 0)
					program.setProgramCounter(address); // PC <- addr
					return 1; // do not increment PC
				}
				break;
			case "F": // jump and link
				String programCounter = shortToHex((short) (program.getProgramCounter() + 1)); // PC as String
				registers[dest] = programCounter; // R[d] <- PC
				program.setProgramCounter(address); // PC <- addr
				return 1; // do not increment PC
			}

			return 0;

		case 3: // A and B opcodes

			short memoryAddress = hexToShort(registers[sourceT]); // memoryAddress <- R[t]

			switch (opcode) {
			case "A": // load indirect
				registers[dest] = loadFromMemory(memoryAddress, program); // R[d] <- M[memoryAddress]
				break;
			case "B": // store indirect
				storeToMemory(memoryAddress, registers[dest], program); // M[memoryAddress] <- R[d]
			}

			return 0;

		case 4: // E opcode (jump register)

			short newPC = hexToShort(registers[dest]); // newPC <- R[d]
			if (outsideMemory(newPC)) { // newPC is not within memory range
				// note bug in visual x-toy: E checks between 1 and 256, not 0 and 255
				throw new ProgramCounterOutOfBoundsException();
			}
			program.setProgramCounter(newPC); // PC <- newPC

			return 1; // do not increment PC

		case 5: // 0 opcode (halt)

			return -1; // halt

		default:
			return 0; // do nothing
		}
	}

	/**
	 * Checks whether the instruction changes R[d].
	 * 
	 * @return whether R[d] is changed by the instruction
	 */
	private boolean changesD() {
		return opcode.matches("[1-8AF]"); // opcode is 1-8, A or F
	}

	/**
	 * Checks whether the instruction needs to access the value of R[d].
	 * 
	 * @return whether R[d] is needed by the instruction
	 */
	private boolean needsD() {
		return opcode.matches("[9BCDE]"); // opcode is 9, B, C, D or E
	}

	/**
	 * Checks whether the instruction needs to access the value of R[s].
	 * 
	 * @return whether R[s] is needed by the instruction
	 */
	private boolean needsS() {
		return opcode.matches("[1-6]"); // opcode is 1-6
	}

	/**
	 * Checks whether the instruction needs to access the value of R[t].
	 * 
	 * @return whether R[t] is needed by the instruction
	 */
	private boolean needsT() {
		return opcode.matches("[1-6AB]"); // opcode is 1-6, A or B
	}

	/**
	 * Checks whether the instruction needs to access a value from stdin. If opcode
	 * is "A", this will require calculating the value of R[t] in case it is 00FF.
	 * 
	 * @param program program to check in the context of
	 * @return whether the instruction needs input
	 */
	boolean needsInput(Program program) {
		String[] registers = program.getRegisters(); // get program's registers
		short memoryAddress;

		if (opcode.equals("A")) { // opcode is "A"
			short sourceT = hexToShort(t); // t as short
			memoryAddress = hexToShort(registers[sourceT]); // memoryAddress <- R[t]

		} else if (opcode.equals("8")) { // opcode is "8"
			memoryAddress = hexToShort(addr); // memoryAddress <- addr

		} else { // opcode is neither
			return false;
		}

		return memoryAddress == INPUT_OUTPUT_ADDRESS; // memoryAddress is 0xFF (and opcode is A or 8)
	}

	/**
	 * Creates a hex representation of the instruction by concatenating its opcode,
	 * d, s, and t components.
	 * 
	 * @return hex representation of the instruction
	 */
	String asHex() {
		return opcode + d + s + t; // construct string from instruction components
	}

	/**
	 * Returns a string representation of an {@link Instruction} (used for debugging
	 * purposes).
	 */
	public String toString() {
		return "Instruction " + asHex() + " [format=" + format + ", opcode=" + opcode + ", d=" + d + ", s=" + s + ", t="
				+ t + ", addr=" + addr + "]";
	}
}

/**
 * Represents a TOY file as an array of {@link RawLine}s. Includes methods to
 * {@linkplain #format() format} all the lines in the file and
 * {@linkplain #toProgram() convert} the file to a {@link Program} object.
 */
class TOYFile {
	RawLine[] contents;
	File file;
	String filename;

	/**
	 * Constructs a new {@link TOYFile} object from a given File object.
	 * 
	 * @param file     File to load
	 * @param filename Name of the file
	 * @throws FileNotFoundException if {@code fileReader} cannot find {@code file}
	 */
	public TOYFile(File file, String filename) throws FileNotFoundException {
		List<RawLine> contents = new ArrayList<RawLine>();

		Scanner fileReader = new Scanner(file); // set up Scanner to read file
		while (fileReader.hasNextLine()) { // while there are still lines to be read
			String fileLine = fileReader.nextLine(); // get next line in file
			contents.add(new RawLine(fileLine)); // convert line to RawLine, add to List
		}
		fileReader.close(); // close fileReader Scanner

		RawLine[] contentsArray = new RawLine[contents.size()]; // instantiate Array same length as List
		this.contents = contents.toArray(contentsArray); // convert List to Array, assign to this.contents

		this.file = file;
		this.filename = filename;
	}

	/**
	 * Converts {@link #contents} to {@link Program} object.
	 * 
	 * @return new {@link Program} object
	 */
	public Program toProgram() {
		List<Line> lineList = new ArrayList<Line>();

		for (RawLine rawLine : contents) { // iterate over RawLines in contents
			if (rawLine.isValid()) { // line is valid code
				lineList.add(rawLine.toLine()); // convert to Line, add to List
			} // discard line if not valid code
		}

		Line[] lineArray = new Line[lineList.size()]; // instantiate Array same length as List
		lineArray = lineList.toArray(lineArray); // convert List to Array

		return new Program(lineArray); // return new Program from Lines
	}

	/**
	 * Converts {@code int} to formatted 2-digit uppercase hex string. If the hex
	 * representation is less than 2 digits, it will be padded with "{@code 0}"s.
	 * 
	 * @param lineNumber {@code int} to be converted
	 * @return 2-digit uppercase hex string
	 * @see Integer#toHexString(int)
	 */
	static String lineNumberToHex(int lineNumber) {
		String hexNumber = Integer.toHexString(lineNumber); // convert lineNumber to hex string
		while (hexNumber.length() < 2) { // until length is 2
			hexNumber = "0" + hexNumber; // pad hexNumber with "0"s
		}
		return hexNumber.toUpperCase(); // convert to upper case
	}

	/**
	 * Reformats a given {@link RawLine}. Accomplishes this by first converting to a
	 * {@link Line} object then accessing its parts. The {@link Line#lineNumber} is
	 * converted to a formatted hex string. A {@link Line#comment} is
	 * {@linkplain Line#generateComment() generated}. The line is then padded with
	 * spaces until it reaches a predefined length.
	 * 
	 * @param rawLine {@link RawLine} to be formatted
	 * @return formatted {@link RawLine}
	 * @see Line#generateComment()
	 */
	private static RawLine formatLine(RawLine rawLine) {
		RawLine formattedLine;
		final int LINE_LENGTH = 41; // formatted minimum length of every line

		if (rawLine.isValid()) { // if rawline is valid code
			Line line = rawLine.toLine(); // convert to Line to get components
			String lineNumber = lineNumberToHex(line.getLineNumber()); // convert lineNumber to hex string
			String instruction = line.getInstruction(); // get instruction
			String comment = line.generateComment(); // generate comment for instruction
			String formattedLineString = lineNumber + ": " + instruction + "   " + comment; // concatenate components
			formattedLineString = String.format("%-" + LINE_LENGTH + "s", formattedLineString); // pad line with spaces
			formattedLine = new RawLine(formattedLineString); // convert back into RawLine
		} else { // line is not code (i.e. a comment) so no formatting needed
			formattedLine = rawLine;
		}
		return formattedLine;
	}

	/**
	 * Formats all {@link RawLine}s in {@link #contents}.
	 * 
	 * @see #formatLine(RawLine)
	 * @return new formatted {@code TOYFile}
	 */
	public TOYFile format() {
		TOYFile formattedFile = this; // create new TOYFile the same as this

		List<RawLine> formattedContentsList = new ArrayList<RawLine>();
		RawLine formattedLine;

		for (RawLine currentLine : formattedFile.contents) { // iterate over lines in contents
			formattedLine = formatLine(currentLine); // format each line
			formattedContentsList.add(formattedLine); // add to List of formatted lines
		}

		RawLine[] formattedContentsArray = new RawLine[formattedContentsList.size()]; // instantiate array size of List
		formattedFile.contents = formattedContentsList.toArray(formattedContentsArray); // convert List to Array, assign
																						// to contents
		return formattedFile;
	}

	/**
	 * Saves the contents of a TOYFile back to the file it was loaded from.
	 * 
	 * @param file file to write to
	 * @throws IOException if there is an error writing to the file
	 */
	public void writeToFile(File file) throws IOException {
		final Path filepath = file.toPath(); // convert file to Path object

		try (final BufferedWriter writer = Files.newBufferedWriter(filepath, StandardOpenOption.CREATE);) {
			// create BufferedWriter object to write to file at filepath
			for (RawLine currentLine : contents) { // iterate over lines in contents
				writer.write(currentLine.content + "\n"); // write current line to file
			}
			writer.flush(); // 🚽
		}
	}

	/**
	 * No-arguments version of {@link #writeToFile(File)}, uses this TOYFile's
	 * {@link #file}.
	 * 
	 * @throws IOException if there is an error writing to the file
	 */
	public void writeToFile() throws IOException {
		writeToFile(file);
	}

	/**
	 * Gets the {@link #file} of a TOYFile object.
	 * 
	 * @return {@link #file}
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Gets the {@link #filename} of a TOYFile object.
	 * 
	 * @return {@link #filename}
	 */
	public String getFilename() {
		return filename;
	}
}

/**
 * Represents a runnable TOY program as an array of {@link Line}s. Includes
 * methods to {@linkplain #isValid() validate} that the program is runnable,
 * {@linkplain #loadToMemory() load} the {@link Line} array to {@link #memory},
 * as well as {@linkplain #run() run}, {@linkplain #stop() stop} and
 * {@linkplain #reset() reset} the program.
 */
class Program {
	TOYDisplay display;
	Line[] lines;
	String[] registers;
	Instruction[] memory;
	String lineNumber, errorMessage;
	Instruction currentInstruction;
	short programCounter;
	List<String> stdin, stdout;
	private boolean isRunning, isReset, shouldStop, isFinished, errorOccurred;
	private static final int REGISTERS_SIZE = 0x10; // 16
	private static final int MEMORY_SIZE = 0x100; // 256
	private static final int INITIAL_PC = 0x10; // 16

	/**
	 * Constructs a new {@link Program} object from a given {@link Line} array.
	 * 
	 * @param lines Array of {@link Line}s of code
	 * @see #initVariables()
	 */
	public Program(Line[] lines) {
		this.lines = lines;
		isRunning = false;
		isReset = true;
		isFinished = false;
		shouldStop = false;
		errorOccurred = false;
		initVariables();
	}

	/**
	 * Initialise variables relating to the execution of the program. Will empty
	 * registers, memory, stdin, stdout as well as resetting the program counter.
	 * 
	 * @see #Program(Line[])
	 * @see #reset()
	 */
	private void initVariables() {
		registers = new String[REGISTERS_SIZE];
		registers[0] = "0000";
		memory = new Instruction[MEMORY_SIZE];
		programCounter = INITIAL_PC;
		stdin = new ArrayList<String>();
		stdout = new ArrayList<String>();
	}

	/**
	 * Checks if a {@link Program} is valid and therefore able to be executed. First
	 * checks if the program contains any valid lines, then checks for any duplicate
	 * line numbers, then checks if all the line numbers are in order.
	 * 
	 * @param display object to display any error messages on
	 * @return whether the program is runnable
	 */
	public boolean isValid(TOYDisplay display) {
		if (lines.length == 0) { // there are no lines of valid TOY code
			display.showErrorMessage("Program does not contain any valid TOY code");
			return false; // program is not valid
		}

		List<Integer> lineNumbersList = new ArrayList<Integer>();
		List<String> duplicates = new ArrayList<String>();

		for (Line currentLine : lines) { // iterate over Lines in Program
			int currentLineNumber = currentLine.getLineNumber(); // get current line number
			String currentLineNumberAsHex = TOYFile.lineNumberToHex(currentLineNumber);
			if (lineNumbersList.contains(currentLineNumber)) { // if line number has already been seen
				if (!duplicates.contains(currentLineNumberAsHex)) { // has not already been logged as duplicate
					duplicates.add(currentLineNumberAsHex); // add hex to List of duplicates
				}
			} else {
				lineNumbersList.add(currentLineNumber); // add to List of line numbers
			}
		}

		if (duplicates.size() > 0) { // if there are duplicate line numbers
			String duplicatesString = duplicates.toString().substring(1, duplicates.toString().length() - 1);
			display.showErrorMessage("Program contains duplicate line numbers: " + duplicatesString);
			return false; // program is not valid
		}

		Integer[] lineNumbers = new Integer[lineNumbersList.size()];
		lineNumbers = lineNumbersList.toArray(lineNumbers); // convert lineNumbersList to Array

		Integer[] unsortedLineNumbers = lineNumbers.clone(); // clone lineNumbers before sorting
		Arrays.sort(lineNumbers); // sort lineNumbers
		Integer[] sortedLineNumbers = lineNumbers.clone(); // clone lineNumbers after sorting

		if (!Arrays.equals(unsortedLineNumbers, sortedLineNumbers)) { // line numbers are not the same after sorting
			display.showErrorMessage("Program's lines are not in order");
			return false; // program is not valid
		}

		return true; // program is valid
	}

	/**
	 * Extracts the {@link Instruction}s from {@link lines} and adds them to the
	 * {@code memory} array.
	 */
	public void loadToMemory() {
		for (Line currentLine : lines) { // iterate over Lines in program
			int currentLineNumber = currentLine.getLineNumber(); // get current line number
			Instruction lineAsInstruction = currentLine.toInstruction(); // convert current line to instruction
			memory[currentLineNumber] = lineAsInstruction; // load instruction to memory
		}
		currentInstruction = memory[programCounter]; // sets the currentInstruction value now that memory is loaded
	}

	/**
	 * Runs the program. Gets the current instruction by accessing the memory item
	 * at the address of the {@link #programCounter}, then
	 * {@linkplain Instruction#execute(Program) executes} it, then increments the
	 * PC. Handles any exceptions thrown by the execution of the instruction.
	 * 
	 * @see Instruction#execute(Program)
	 */
	public void run() {
		isReset = false; // program is no longer in reset state
		isRunning = true; // program is running
		isFinished = false; // program has not finished execution
		shouldStop = false; // program should not stop running

		while (true) {
			currentInstruction = memory[programCounter]; // get current instruction
			lineNumber = TOYFile.lineNumberToHex(programCounter); // calculate current line number (as hex)

			try {
				if (currentInstruction != null) { // there is an instruction at the current memory location
					if (stdin.size() == 0 && currentInstruction.needsInput(this)) { // input is needed but there is none
						break; // stop execution of program before next instruction can be executed
					}

					int result = currentInstruction.execute(this); // execute instruction

					if (result == -1) { // halt result code
						if (TOYEmulator.DEBUG) {
							if (TOYEmulator.INTERACT_WITH_CONSOLE) {
								System.out.println("halted program, PC at " + lineNumber);
							}
						}
						shouldStop = true; // exit execution loop when finished
						isFinished = true; // program has finished execution (disable run button)
					}

					if (result == 0) { // not "do not increment PC" result code
						programCounter++; // increment program counter
					}

				} else { // instruction at current PC has not been initialised
					throw new CommandUninitialisedException();
				}

			} catch (TOYException te) { // exception related to the execution of the program occurred
				errorMessage = "Error at line " + lineNumber + ":\n" + te.getMessage(); // set error message
				if (TOYEmulator.INTERACT_WITH_CONSOLE) {
					// print exception error message (not stack trace)
					System.out.println("Error at line " + lineNumber + ":\n" + te.toString());
				}
				errorOccurred = true; // error has occurred
				shouldStop = true; // exit execution loop when finished
				isFinished = true; // program has finished execution (disable run button)
			}

			if (shouldStop) {
				break; // stop running program
			}

			display.update(); // update display
		}

		isRunning = false; // program is not running
		display.update(); // update display finally
	}

	/**
	 * Resets the program. Reinitialises variables and loads {@link #lines} to
	 * {@link #memory}.
	 * 
	 * @see #initVariables()
	 * @see #loadToMemory()
	 */
	public void reset() {
		if (!isRunning() && !isReset()) { // program is not running nor is it already in reset state
			initVariables(); // reinitialise variables
			loadToMemory(); // reload program to memory
			display.update(); // update GUI

			if (TOYEmulator.DEBUG) {
				if (TOYEmulator.INTERACT_WITH_CONSOLE) {
					System.out.println("\nProgram reset\n");
				}
			}

			isReset = true; // program has been reset
			isFinished = false; // program has not finished execution
			errorOccurred = false; // error has not occurred
		}
	}

	/**
	 * Stops execution of the program (temporarily). Does not immediately stop it,
	 * but rather sets the value of {@link #shouldStop} so that the execution loop
	 * will break at the end of the next cycle.
	 */
	public void stop() {
		if (isRunning()) { // program is running
			shouldStop = true; // program should stop
			// execution loop will break at the end of the next execution cycle

			if (TOYEmulator.DEBUG) {
				if (TOYEmulator.INTERACT_WITH_CONSOLE) {
					System.out.println("\nProgram stopped\n");
				}
			}
		}
	}

	/**
	 * Gets the {@link #registers} array of a program.
	 * 
	 * @return {@link #registers} array
	 */
	public String[] getRegisters() {
		return registers;
	}

	/**
	 * Gets the {@link #memory} array of a program.
	 * 
	 * @return {@link #memory} array
	 */
	public Instruction[] getMemory() {
		return memory;
	}

	/**
	 * Gets the {@link #programCounter} of a program.
	 * 
	 * @return {@link #programCounter}
	 */
	public short getProgramCounter() {
		return programCounter;
	}

	/**
	 * Gets the {@link #lineNumber} of a program. Rather than immediately returning
	 * {@code lineNumber}, it is recalculated using {@link #programCounter}, which
	 * is more likely to be up-to-date and reliable.
	 * 
	 * @return {@link #lineNumber}
	 * @see TOYFile#lineNumberToHex(int)
	 */
	public String getLineNumber() {
		return TOYFile.lineNumberToHex(programCounter); // retrieve lineNumber as a function of programCounter
	}

	/**
	 * Gets the {@link #currentInstruction} of a program.
	 * 
	 * @return {@link #currentInstruction}
	 */
	public Instruction getCurrentInstruction() {
		return currentInstruction;
	}

	/**
	 * Gets the {@link #errorMessage} of a program.
	 * 
	 * @return {@link #errorMessage}
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Gets a program's {@link #isRunning} value.
	 * 
	 * @return whether program is running
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Gets a program's {@link #isReset} value.
	 * 
	 * @return whether program has been reset
	 */
	public boolean isReset() {
		return isReset;
	}

	/**
	 * Gets a program's {@link #isFinished} value.
	 * 
	 * @return whether program has finished running
	 */
	public boolean isFinished() {
		return isFinished;
	}

	/**
	 * Gets a program's {@link #errorOccurred} value.
	 * 
	 * @return whether an error has occurred in the execution of program
	 */
	public boolean errorOccurred() {
		return errorOccurred;
	}

	/**
	 * Sets whether an error has occurred in the execution of a program.
	 * 
	 * @param errorOccurred whether an error has occurred
	 */
	public void setErrorOccurred(boolean errorOccurred) {
		this.errorOccurred = errorOccurred;
	}

	/**
	 * Sets whether a program has been reset.
	 * 
	 * @param isReset whether program has been reset
	 */
	public void setReset(boolean isReset) {
		this.isReset = isReset;
	}

	/**
	 * Sets the {@link #programCounter} of a program.
	 * 
	 * @param programCounter new value to set {@link #programCounter} to
	 */
	public void setProgramCounter(short programCounter) {
		this.programCounter = programCounter;
	}

	/**
	 * Sets the {@link #display} of a program.
	 * 
	 * @param display new display to set
	 */
	public void setDisplay(TOYDisplay display) {
		this.display = display;
	}
}

/**
 * Base class for the TOYEmulator program. Contains debug constants and the
 * {@link #main(String[])} method.
 */
public class TOYEmulator {
	static final boolean DEBUG = false, INTERACT_WITH_CONSOLE = false; // debug constants

	/**
	 * Only contains code to show the GUI of a TOY emulator.
	 * 
	 * @param args arguments passed when running the program (unused)
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() { // add to EDT queue rather than executing immediately
			public void run() { // run later in event dispatch thread
				new TOYDisplay().createAndShowGUI(); // creates an anonymous new TOYDisplay object and shows its GUI
			}
		});
	}
}
