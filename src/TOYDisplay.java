package myPackage;

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

/**
 * Represents a GUI to display all the information from a {@linkplain Program}.
 * Includes methods to {@linkplain #createAndShowGUI() create and show} the GUI,
 * as well as {@linkplain #update() update} it.
 */
class TOYDisplay implements ActionListener {
	Program program;
	TOYFile loadedFile;
	JFrame window;
	JPanel wrapperPanel, otherInfoPanel, corePanel, registersDisplay, memoryDisplay, corePadding, currentValuesDisplay,
			currentValuesLabels, pcLabelPanel, currentInstructionLabelPanel, currentValuesPanel, pcValuePanel,
			currentInstructionValuePanel, currentValuesPadding, ioAndButtonsPanel, ioPanel, inputPanel, inputTextPanel,
			outputPanel, inputLabelPanel, outputLabelPanel, inputDisplay, outputDisplay, buttonsPanel, runButtonPanel,
			stopButtonPanel, resetButtonPanel;
	JLabel pcLabel, currentInstructionLabel, pcValue, currentInstructionValue, inputLabel, outputLabel;
	JScrollPane registersScrollPane, memoryScrollPane, inputScrollPane, outputScrollPane;
	JTabbedPane coreTabs;
	JTextField inputTextField;
	JButton runButton, stopButton, resetButton;
	JMenuBar menuBar;
	JMenu fileMenu, helpMenu;
	JMenuItem openMenuItem, formatMenuItem, exitMenuItem, userManualMenuItem;
	JFileChooser fileChooser;
	JLabel[] registerLabels, memoryLabels, inputLabels, outputLabels;
	String[] registers, stdinArray, stdoutArray;
	Instruction[] memory;
	String lineNumber;
	Instruction currentInstruction;
	static final String NULL_REGISTER_DISPLAY = "????", // assuming registers and instructions are always length 4
			TITLE = "TOY Emulator", USER_MANUAL_PATH = "User Manual.pdf";
	static final Font LABEL_FONT = new Font(Font.DIALOG, Font.BOLD, 14),
			CODE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);
	static Program DEFAULT_PROGRAM = new Program(null);

	/**
	 * Links the display to a given {@linkplain Program}. This allows the GUI to
	 * display information specific to the program, and the program to update the
	 * GUI when required.
	 * 
	 * @param program program object to link to
	 * @see Program#setDisplay(TOYDisplay)
	 * @see #setProgram(Program)
	 */
	private void link(Program program) {
		Program programToUse = program == null ? DEFAULT_PROGRAM : program; // use DEFAULT_PROGRAM if none is specified
		// DEFAULT_PROGRAM being loaded indicates that the user has not yet loaded their
		// own, and will disable some features of the GUI (helps avoid some errors).

		programToUse.setDisplay(this); // sets the program's display to this
		setProgram(programToUse); // sets this's program to the program
	}

	/**
	 * Sets the {@link #program} of a display.
	 * 
	 * @param program program to set
	 * @see #link(Program)
	 */
	private void setProgram(Program program) {
		this.program = program;
	}

	/**
	 * Initialise all variables and objects relating to the GUI other than the
	 * window. This method contains all the code to format how all the
	 * {@linkplain JPanel}s are laid out on the screen.
	 * 
	 * @see #link(Program)
	 * @see #createAndShowGUI()
	 */
	private void init() {
		link(null); // link to nothing (will use DEFAULT_PROGRAM)

		registers = program.getRegisters(); // get program's registers
		memory = program.getMemory(); // get program's memory
		lineNumber = program.getLineNumber(); // get program's current line number (hex string)
		currentInstruction = program.getCurrentInstruction(); // get program's current instruction
		stdinArray = program.stdin.toArray(new String[0]); // get program's stdin as a string array
		stdoutArray = program.stdout.toArray(new String[0]); // get program's stdout as a string array
		final String pcLabelText = "Program Counter", currentInstructionLabelText = "Current Instruction",
				inputLabelText = "Standard Input", outputLabelText = "Standard Output", runButtonText = "Run",
				stopButtonText = "Stop", resetButtonText = "Reset", registersText = "Registers", memoryText = "Memory",
				fileMenuText = "File", openMenuItemText = "Open", formatMenuItemText = "Format",
				exitMenuItemText = "Exit", helpMenuText = "Help", userManualMenuItemText = "Open User Manual";
		final Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 2); // used occasionally

		openMenuItem = new JMenuItem(openMenuItemText);
		openMenuItem.setMnemonic(KeyEvent.VK_O); // selected when alt+O is pressed
		openMenuItem.addActionListener(this);

		formatMenuItem = new JMenuItem(formatMenuItemText);
		formatMenuItem.setMnemonic(KeyEvent.VK_R); // selected when alt+R is pressed
		formatMenuItem.addActionListener(this);

		exitMenuItem = new JMenuItem(exitMenuItemText);
		exitMenuItem.setMnemonic(KeyEvent.VK_X); // selected when alt+X is pressed
		exitMenuItem.addActionListener(this);

		fileMenu = new JMenu(fileMenuText);
		fileMenu.setMnemonic(KeyEvent.VK_F); // selected when alt+F is pressed
		fileMenu.add(openMenuItem);
		fileMenu.add(formatMenuItem);
		fileMenu.addSeparator(); // separating line
		fileMenu.add(exitMenuItem);

		userManualMenuItem = new JMenuItem(userManualMenuItemText);
		userManualMenuItem.setMnemonic(KeyEvent.VK_U); // selected when alt+U is pressed
		userManualMenuItem.addActionListener(this);

		helpMenu = new JMenu(helpMenuText);
		helpMenu.setMnemonic(KeyEvent.VK_H); // selected when alt+H is pressed
		helpMenu.add(userManualMenuItem);

		menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		pcLabel = new JLabel(pcLabelText);
		pcLabel.setFont(LABEL_FONT);

		pcLabelPanel = new JPanel();
		pcLabelPanel.add(Box.createHorizontalGlue()); // align pcLabel to the right
		pcLabelPanel.add(pcLabel);
		pcLabelPanel.setLayout(new BoxLayout(pcLabelPanel, BoxLayout.X_AXIS));
		pcLabelPanel.setOpaque(false);

		currentInstructionLabel = new JLabel(currentInstructionLabelText);
		currentInstructionLabel.setFont(LABEL_FONT);

		currentInstructionLabelPanel = new JPanel();
		currentInstructionLabelPanel.add(Box.createHorizontalGlue()); // align currentInstructionLabel to the right
		currentInstructionLabelPanel.add(currentInstructionLabel);
		currentInstructionLabelPanel.setLayout(new BoxLayout(currentInstructionLabelPanel, BoxLayout.X_AXIS));
		currentInstructionLabelPanel.setOpaque(false);

		currentValuesLabels = new JPanel();
		currentValuesLabels.add(pcLabelPanel);
		currentValuesLabels.add(currentInstructionLabelPanel);
		currentValuesLabels.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 5)); // padding
		currentValuesLabels.setLayout(new BoxLayout(currentValuesLabels, BoxLayout.Y_AXIS));
		currentValuesLabels.setOpaque(false);

		pcValue = new JLabel(lineNumber);
		pcValue.setFont(CODE_FONT);

		pcValuePanel = new JPanel();
		pcValuePanel.add(pcValue);
		pcValuePanel.add(Box.createHorizontalGlue()); // align pcValue to the left
		pcValuePanel.setLayout(new BoxLayout(pcValuePanel, BoxLayout.X_AXIS));
		pcValuePanel.setOpaque(false);

		currentInstructionValue = new JLabel(getCurrentInstructionDisplay());
		currentInstructionValue.setFont(CODE_FONT);

		currentInstructionValuePanel = new JPanel();
		currentInstructionValuePanel.add(currentInstructionValue);
		currentInstructionValuePanel.add(Box.createHorizontalGlue()); // align currentInstructionValue to the left
		currentInstructionValuePanel.setLayout(new BoxLayout(currentInstructionValuePanel, BoxLayout.X_AXIS));
		currentInstructionValuePanel.setOpaque(false);

		currentValuesPanel = new JPanel();
		currentValuesPanel.add(pcValuePanel);
		currentValuesPanel.add(currentInstructionValuePanel);
		currentValuesPanel.setPreferredSize(new Dimension(300, 50));
		currentValuesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // padding
		currentValuesPanel.setLayout(new BoxLayout(currentValuesPanel, BoxLayout.Y_AXIS));
		currentValuesPanel.setOpaque(false);

		currentValuesPadding = new JPanel();
		currentValuesPadding.add(currentValuesLabels);
		currentValuesPadding.add(currentValuesPanel);
		currentValuesPadding.setBorder(lineBorder); // surround in black box
		currentValuesPadding.setLayout(new BoxLayout(currentValuesPadding, BoxLayout.X_AXIS));
		currentValuesPadding.setOpaque(false);

		currentValuesDisplay = new JPanel();
		currentValuesDisplay.add(currentValuesPadding);
		currentValuesDisplay.add(Box.createHorizontalGlue()); // attempt to align currentValuesDisplay to the left
		currentValuesDisplay.setLayout(new BoxLayout(currentValuesDisplay, BoxLayout.X_AXIS));
		currentValuesDisplay.setOpaque(false);

		inputLabel = new JLabel(inputLabelText);
		inputLabel.setFont(LABEL_FONT);

		inputLabelPanel = new JPanel();
		inputLabelPanel.add(inputLabel);
		inputLabelPanel.add(Box.createHorizontalGlue()); // align inputLabel to the left
		inputLabelPanel.setLayout(new BoxLayout(inputLabelPanel, BoxLayout.X_AXIS));
		inputLabelPanel.setOpaque(false);

		inputTextField = new JTextField(20); // 20 is column width of the text field
		inputTextField.addActionListener(this);
		inputTextField.setMaximumSize(new Dimension(100, 100)); // don't allow to get too big
		inputTextField.setOpaque(false);

		inputTextPanel = new JPanel();
		inputTextPanel.add(inputTextField);
		inputTextPanel.add(Box.createHorizontalGlue()); // align inputTextField to the left
		inputTextPanel.setLayout(new BoxLayout(inputTextPanel, BoxLayout.X_AXIS));
		inputTextPanel.setOpaque(false);

		inputDisplay = new JPanel();
		inputLabels = arrayToLabels(stdinArray, 0); // convert stdin to JLabel[] array
		labelsOntoPanel(inputLabels, inputDisplay); // add stdin labels to panel
		inputDisplay.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5)); // padding
		inputDisplay.setLayout(new BoxLayout(inputDisplay, BoxLayout.Y_AXIS));
		inputDisplay.setOpaque(false);

		inputScrollPane = new JScrollPane(inputDisplay, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // add vertical scrollbar if needed
		inputScrollPane.setBorder(lineBorder); // surroung in black box
		inputScrollPane.setPreferredSize(new Dimension(100, 100));
		inputScrollPane.setOpaque(false);

		inputPanel = new JPanel();
		inputPanel.add(inputLabelPanel);
		inputPanel.add(inputTextPanel);
		inputPanel.add(inputScrollPane);
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setOpaque(false);

		outputLabel = new JLabel(outputLabelText);
		outputLabel.setFont(LABEL_FONT);

		outputLabelPanel = new JPanel();
		outputLabelPanel.add(outputLabel);
		outputLabelPanel.add(Box.createHorizontalGlue()); // align outputLabel to the left
		outputLabelPanel.setLayout(new BoxLayout(outputLabelPanel, BoxLayout.X_AXIS));
		outputLabelPanel.setOpaque(false);

		outputDisplay = new JPanel();
		outputLabels = arrayToLabels(stdoutArray, 0); // convert stdout to JLabel[] array
		labelsOntoPanel(outputLabels, outputDisplay); // add stdout labels to panel
		outputDisplay.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5)); // padding
		outputDisplay.setLayout(new BoxLayout(outputDisplay, BoxLayout.Y_AXIS));
		outputDisplay.setOpaque(false);

		outputScrollPane = new JScrollPane(outputDisplay, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // add vertical scrollbar if needed
		outputScrollPane.setPreferredSize(new Dimension(100, 100));
		outputScrollPane.setBorder(lineBorder); // surround in black box
		outputScrollPane.setOpaque(false);

		outputPanel = new JPanel();
		outputPanel.add(outputLabelPanel);
		outputPanel.add(outputScrollPane);
		outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
		outputPanel.setOpaque(false);

		ioPanel = new JPanel();
		ioPanel.add(inputPanel);
		ioPanel.add(outputPanel);
		ioPanel.setLayout(new BoxLayout(ioPanel, BoxLayout.Y_AXIS));
		ioPanel.setOpaque(false);

		runButton = new JButton(runButtonText);
		runButton.addActionListener(this);

		runButtonPanel = new JPanel();
		runButtonPanel.add(runButton);
		runButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding
		runButtonPanel.setLayout(new BoxLayout(runButtonPanel, BoxLayout.X_AXIS));
		runButtonPanel.setOpaque(false);

		stopButton = new JButton(stopButtonText);
		stopButton.addActionListener(this);

		stopButtonPanel = new JPanel();
		stopButtonPanel.add(stopButton);
		stopButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding
		stopButtonPanel.setLayout(new BoxLayout(stopButtonPanel, BoxLayout.X_AXIS));
		stopButtonPanel.setOpaque(false);

		resetButton = new JButton(resetButtonText);
		resetButton.addActionListener(this);

		resetButtonPanel = new JPanel();
		resetButtonPanel.add(resetButton);
		resetButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding
		resetButtonPanel.setLayout(new BoxLayout(resetButtonPanel, BoxLayout.X_AXIS));
		resetButtonPanel.setOpaque(false);

		buttonsPanel = new JPanel();
		buttonsPanel.add(runButtonPanel);
		buttonsPanel.add(stopButtonPanel);
		buttonsPanel.add(resetButtonPanel);
		buttonsPanel.add(Box.createVerticalGlue()); // align all buttons to top
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // padding
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
		buttonsPanel.setOpaque(false);

		ioAndButtonsPanel = new JPanel();
		ioAndButtonsPanel.add(ioPanel);
		ioAndButtonsPanel.add(buttonsPanel);
		ioAndButtonsPanel.setLayout(new BoxLayout(ioAndButtonsPanel, BoxLayout.X_AXIS));
		ioAndButtonsPanel.setOpaque(false);

		otherInfoPanel = new JPanel();
		otherInfoPanel.add(currentValuesDisplay);
		otherInfoPanel.add(ioAndButtonsPanel);
		otherInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding
		otherInfoPanel.setLayout(new BoxLayout(otherInfoPanel, BoxLayout.Y_AXIS));
		otherInfoPanel.setOpaque(false);

		registersDisplay = new JPanel();
		registerLabels = arrayToLabels(registers, 1); // convert registers to JLabel[] array
		labelsOntoPanel(registerLabels, registersDisplay); // add register labels to panel
		registersDisplay.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 5)); // padding
		registersDisplay.setLayout(new BoxLayout(registersDisplay, BoxLayout.Y_AXIS));
		registersDisplay.setOpaque(false);

		registersScrollPane = new JScrollPane(registersDisplay, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // add vertical scrollbar if needed
		registersScrollPane.setOpaque(false);

		memoryDisplay = new JPanel();
		memoryLabels = arrayToLabels(memoryToStringArray(memory), 2); // convert memory to JLabel[] array
		labelsOntoPanel(memoryLabels, memoryDisplay); // add memory labels to panel
		memoryDisplay.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 5)); // padding
		memoryDisplay.setLayout(new BoxLayout(memoryDisplay, BoxLayout.Y_AXIS));
		memoryDisplay.setOpaque(false);

		memoryScrollPane = new JScrollPane(memoryDisplay, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // vertical scrollbar if needed
		memoryScrollPane.setOpaque(false);

		coreTabs = new JTabbedPane(); // allows clicking to switch between viewing registers and memory
		coreTabs.addTab(registersText, registersScrollPane);
		coreTabs.addTab(memoryText, memoryScrollPane);
		coreTabs.setPreferredSize(new Dimension(155, 350)); // allow minimum width when possible
		coreTabs.setOpaque(false);

		corePadding = new JPanel();
		corePadding.add(coreTabs);
		corePadding.setBorder(lineBorder); // surround in black box
		corePadding.setLayout(new BoxLayout(corePadding, BoxLayout.X_AXIS));
		corePadding.setOpaque(false);

		corePanel = new JPanel();
		corePanel.add(corePadding);
		corePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding
		corePanel.setLayout(new BoxLayout(corePanel, BoxLayout.X_AXIS));
		corePanel.setOpaque(false);

		wrapperPanel = new JPanel();
		wrapperPanel.add(otherInfoPanel);
		wrapperPanel.add(corePanel);
		wrapperPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // pad whole screen
		wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
		wrapperPanel.setOpaque(false);
	}

	/**
	 * Adds all the labels in an array to a given panel. Will first remove all the
	 * existing components of the panel.
	 * 
	 * @param labels array of labels to add to panel
	 * @param panel  panel to add labels to
	 * @see JPanel#removeAll()
	 */
	private static void labelsOntoPanel(JLabel[] labels, JPanel panel) {
		panel.removeAll(); // remove all the components of panel

		for (JLabel label : labels) { // iterate over labels
			label.setFont(CODE_FONT); // set font to CODE_FONT
			panel.add(label); // add label to panel
		}
	}

	/**
	 * Converts a string array to an array of {@link JLabel}s.
	 * 
	 * @param arr            array to convert
	 * @param hexIndexLength digits of hex index to show (passed to
	 *                       {@link #arrayToDisplay(String[], int)})
	 * @return array of {@link JLabel}s
	 * @see #arrayToDisplay(String[], int)
	 */
	private static JLabel[] arrayToLabels(String[] arr, int hexIndexLength) {
		String[] arrayToDisplay = arrayToDisplay(arr, hexIndexLength); // make arr displayable
		JLabel[] labels = new JLabel[arrayToDisplay.length]; // initialise JLabel array same length as input

		String arrItem;
		for (int i = 0; i < arrayToDisplay.length; i++) { // iterate over arrayToDisplay
			arrItem = arrayToDisplay[i]; // temporary variable: current item of arrayToDisplay
			labels[i] = new JLabel(arrItem); // convert arrItem to JLabel, add to label array
		}

		return labels;
	}

	/**
	 * Transforms a string array into a form displayable in the GUI. If an item is
	 * null, it will be replaced with {@value #NULL_REGISTER_DISPLAY}. Optionally,
	 * hex digits representing the index of each item will be appended to the front,
	 * specified by the {@code hexIndexLength} parameter.
	 * 
	 * @param arr            array to format
	 * @param hexIndexLength how many digits of hex index to show
	 * @return formatted array
	 */
	private static String[] arrayToDisplay(String[] arr, int hexIndexLength) {
		String[] arrayToDisplay = arr.clone(); // initialise arrayToDisplay to be the same as input array

		String item, hexIndex, itemToAdd;
		for (int i = 0; i < arr.length; i++) { // iterate over input array
			item = arr[i]; // temporary variable: current item of input array

			item = item == null ? NULL_REGISTER_DISPLAY : item; // set item to "????" instead of null
			if (hexIndexLength == 0) { // hexIndexLength is 0, do not add hex index
				itemToAdd = item; // just add item
			} else {
				// i converted to a hex string, padded with "0"s until it is hexIndexLength
				hexIndex = String.format("%0" + hexIndexLength + "X", i);
				itemToAdd = hexIndex + " " + item; // prefix item with hex index and a space
			}

			arrayToDisplay[i] = itemToAdd; // add item to array to be returned
		}

		return arrayToDisplay;
	}

	/**
	 * Converts a memory ({@code Instruction[]}) array to a {@code String[]} array,
	 * for use in {@link #arrayToLabels(String[], int)}.
	 * 
	 * @param memory array to convert
	 * @return array as {@code String[]} array
	 * @see #arrayToLabels(String[], int)
	 */
	private static String[] memoryToStringArray(Instruction[] memory) {
		String[] stringArray = new String[memory.length]; // create new string array with the same size as memory

		for (int i = 0; i < memory.length; i++) { // iterate over memory
			Instruction instr = memory[i]; // temporary variable representing current item in memory
			// convert memory to hex (or "????" if null) and add to string array
			stringArray[i] = instr == null ? NULL_REGISTER_DISPLAY : instr.asHex();
		}

		return stringArray;
	}

	/**
	 * Loads a file using {@link JFileChooser}. Allows the file chooser interface to
	 * display using the system's look and feel by setting it before opening the
	 * chooser, then resetting it back to the cross-platform look and feel
	 * afterwards.
	 * 
	 * @return user-chosen file
	 * @see UIManager#setLookAndFeel(String)
	 */
	private TOYFile loadFileFromChooser() {
		TOYFile fileToLoad = null; // instantiate the file as null, in case user cancels chooser

		try { // change to system LAF (windows)
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		fileChooser = new JFileChooser(); // create JFileChooser
		if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) { // if user opens a file
			try {
				File selectedFile = fileChooser.getSelectedFile(); // get the file they selected
				// create a TOYFile object from selected file, with the file's filename
				fileToLoad = new TOYFile(selectedFile, selectedFile.getName());
			} catch (FileNotFoundException e) { // Scanner could not read file properly
				showErrorMessage("File not found"); // display error dialog box
			}
		}

		try { // reset to default LAF (JLF)
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		return fileToLoad;
	}

	/**
	 * Displays a given error message as a popup on the screen. Executed
	 * asynchronously (not in the event dispatch thread).
	 * 
	 * @param errorMessage error message to display
	 */
	void showErrorMessage(String errorMessage) {
		final String errorTitle = "Error"; // title of error message popup
		new SwingWorker<Void, Void>() { // same as runButton code
			public Void doInBackground() {
				// show error dialog box with given title, message, and using the built-in icon
				JOptionPane.showMessageDialog(window, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}.execute();
	}

	/**
	 * Gets the current instruction, formatted for display in the GUI (instruction
	 * in hex format followed by its generated comment). If the current instruction
	 * instruction is null, will show {@value #NULL_REGISTER_DISPLAY} with comment
	 * of "Uninitialised Instruction".
	 * 
	 * @return formatted instruction display
	 * @see Instruction#asHex()
	 * @see Instruction#generateComment()
	 */
	private String getCurrentInstructionDisplay() {
		Instruction instr = program.getCurrentInstruction(); // get program's current instruction
		final String uninitialisedComment = "Uninitialised Instruction";
		String instructionHex, comment;

		if (instr == null) { // instruction is null (uninitialised)
			instructionHex = NULL_REGISTER_DISPLAY; // set hex to "????"
			comment = uninitialisedComment; // set comment to "Uninitialised Instruction"
		} else {
			instructionHex = instr.asHex(); // generate hex for instruction
			comment = instr.generateComment(); // generate comment for instruction
		}

		return instructionHex + " (" + comment + ")"; // hex (comment)
	}

	/**
	 * Creates and shows the GUI window with all its components and menu bar. Also
	 * sets the window's title, background colour, size, icon and behaviour when
	 * exited.
	 * 
	 * @see #init()
	 * @see #update()
	 */
	public void createAndShowGUI() {
		init(); // initialise display

		window = new JFrame(); // create window
		Container contentPane = window.getContentPane(); // save window's content pane to a variable

		window.setTitle(TITLE); // set initial window title
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(150, 150, 800, 460); // set default size of the window
		window.setMinimumSize(new Dimension(900, 460)); // set minimum size of the window
		contentPane.setBackground(Color.WHITE); // set background to white

		try {
			String iconPath = "/img/icon.png"; // path to icon image
			URL imageurl = getClass().getResource(iconPath);
			if (imageurl != null) {
				Image icon = ImageIO.read(imageurl); // get icon from path
				window.setIconImage(icon); // set window icon image to icon
			}
		} catch (IOException e) { // something went wrong setting icon
			e.printStackTrace();
		}

		window.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS)); // layout of the frame

		window.setJMenuBar(menuBar); // add menu bar
		window.add(wrapperPanel); // add wrapper panel (contains all other components)

		update(); // update GUI just in case
		window.setVisible(true); // show window
	}

	/**
	 * {@inheritDoc} Handles the functionality of all buttons as well as updating
	 * the GUI's displayed values while the program is running.
	 * 
	 * @see #update()
	 */
	public void actionPerformed(ActionEvent event) {

		if (event != null) { // avoid NullPointerException
			Object source = event.getSource(); // get source of ActionEvent

			if (source.equals(inputTextField)) { // text is entered into stdin textfield
				String text = inputTextField.getText(); // get input text
				Instruction.inputToStdin(text, program); // add input text to stdin
				inputTextField.setText(""); // empty textfield

				if (program.stdin.size() != 0) { // if there is now something in stdin
					program.setReset(false); // program is no longer in reset state
				}

			} else if (source.equals(runButton)) { // "Run" button is pressed
				if (!program.isRunning()) { // program is not running
					new SwingWorker<Void, Void>() { // create a new worker thread
						// this is done so GUI will still be able to update while program is running
						// otherwise the Event Dispatch Thread will be blocked
						public Void doInBackground() { // execute on new thread (in background)
							program.run(); // run program
							return null; // do not return anything (only option for Void type)
						}
					}.execute(); // execute thread
				}

			} else if (source.equals(stopButton)) { // "Stop" button is pressed
				program.stop(); // stop program execution (temporarily)

			} else if (source.equals(resetButton)) { // "Reset" button is pressed
				program.reset(); // reset program

			} else if (source.equals(openMenuItem)) { // "Open" menu item pressed

				loadedFile = loadFileFromChooser(); // get loadedFile from file chooser

				if (loadedFile != null) {
					Program programToLoad = loadedFile.toProgram(); // convert loadedFile to new program object
					boolean programIsValid = programToLoad.isValid(this); // will display any errors
					if (programIsValid) { // if new program is a valid TOY program
						program = programToLoad; // set program to new program
						program.loadToMemory(); // load new program to memory
						link(program); // link display and new program
						window.setTitle(TITLE + " - " + loadedFile.getFilename()); // add current file to window title
					}
				}

			} else if (source.equals(formatMenuItem)) { // "Format" menu item pressed
				TOYFile formattedFile = loadedFile.format(); // format loadedFile

				try {
					formattedFile.writeToFile(loadedFile.getFile()); // save loadedFile

					final String msg = loadedFile.getFilename() + " was successfully formatted";
					// show info dialog box with msg
					JOptionPane.showMessageDialog(window, msg, "Formatter", JOptionPane.INFORMATION_MESSAGE);

				} catch (IOException e) { // problem writing to file
					showErrorMessage(e.getMessage()); // display dialog box for any errors
				}

			} else if (source.equals(exitMenuItem)) { // "Exit" menu item pressed
				System.exit(0); // exit application

			} else if (source.equals(userManualMenuItem)) { // "User Manual" menu item pressed
				try {
					final File userManualFile = new File(USER_MANUAL_PATH); // create File object from USER_MANUAL_PATH
					Desktop.getDesktop().open(userManualFile); // open user manual using system default application

				} catch (IOException | IllegalArgumentException e) { // problem opening user manual
					showErrorMessage(e.getMessage()); // display dialog box for any errors
				}
			}
		}

		if (program.isRunning()) { // program is running
			runButton.setEnabled(false);
			stopButton.setEnabled(true);
			resetButton.setEnabled(false);
		} else { // program is not running
			runButton.setEnabled(true);
			stopButton.setEnabled(false);
		}

		if (program.isReset()) { // program has been reset
			resetButton.setEnabled(false);
		} else if (!program.isRunning()) { // program has not been reset and program is not running
			resetButton.setEnabled(true);
		}

		if (program.isFinished()) { // program has finished running
			runButton.setEnabled(false);
		}

		if (program == DEFAULT_PROGRAM) { // no program is loaded
			runButton.setEnabled(false);
			stopButton.setEnabled(false);
			resetButton.setEnabled(false);
			inputTextField.setEnabled(false);
			formatMenuItem.setEnabled(false);
		} else { // program is loaded
			inputTextField.setEnabled(true);
			formatMenuItem.setEnabled(true);
		}

		if (program.errorOccurred()) { // program threw an exception
			showErrorMessage(program.getErrorMessage()); // display dialog box explaining error
			program.setErrorOccurred(false); // so this code will not be run again on subsequent update() call
		}

		pcValue.setText(program.getLineNumber()); // set pcValue to current program counter (as hex)
		currentInstructionValue.setText(getCurrentInstructionDisplay()); // set to current instruction (with comment)

		registers = program.getRegisters(); // get program's register array
		memory = program.getMemory(); // get program's memory array
		stdinArray = program.stdin.toArray(new String[0]); // get stdin as String array
		stdoutArray = program.stdout.toArray(new String[0]); // get stdout as String array

		registerLabels = arrayToLabels(registers, 1); // registers to label array
		labelsOntoPanel(registerLabels, registersDisplay); // add onto registersDisplay
		memoryLabels = arrayToLabels(memoryToStringArray(memory), 2); // memory to label array
		labelsOntoPanel(memoryLabels, memoryDisplay); // add onto memoryDisplay
		inputLabels = arrayToLabels(stdinArray, 0); // stdin to label array
		labelsOntoPanel(inputLabels, inputDisplay); // add onto inputDisplay
		outputLabels = arrayToLabels(stdoutArray, 0); // stdout to label array
		labelsOntoPanel(outputLabels, outputDisplay); // add onto outputDisplay

		inputDisplay.repaint();
		outputDisplay.repaint();
		window.validate(); // redraw all GUI components in window
	}

	/**
	 * Updates the display and all its GUI components.
	 * 
	 * @see #actionPerformed(ActionEvent)
	 */
	public void update() {
		ActionEvent programEvent = new ActionEvent(program, 0, null); // new ActionEvent with source of program
		actionPerformed(programEvent); // perform action on display
	}
}