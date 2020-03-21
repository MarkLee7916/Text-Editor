import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

public class GUI {
	private JTextArea text;
	
	public GUI() {
		text = new JTextArea(20, 40);
	}

	public void makeComponents() {
		// Create components
		JFrame frame = new JFrame("Text Editor");
		JPanel panel = new JPanel();
		JTextField textSearch = new JTextField(20);
		JScrollPane scroller = new JScrollPane(text);
		JButton search = new JButton("Search");
		JButton regex = new JButton("Search(Regex)");
		JButton prev = new JButton("<");
		JButton next = new JButton(">");

		// Create menu bar components and add them onto each other
		JMenuBar menu = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem exit = new JMenuItem("Exit");
		JMenuItem openAppend = new JMenuItem("Open and append");
		JMenuItem open = new JMenuItem("Open");
		file.add(save);
		file.add(exit);
		file.add(open);
		file.add(openAppend);
		menu.add(file);

		// Add actionListeners to components
		FileIO f = new FileIO(text);
		f.addFileMenuBehaviour(save, exit, openAppend, open);

		Search s = new Search(text);
		s.addSearchBehaviour(search, prev, next, regex, textSearch);

		configureScroller(scroller);
		configureTextArea();

		// Add everything to panel, then configure frame
		frame.getContentPane().add(scroller);
		panel.add(menu);
		panel.add(textSearch);
		panel.add(search);
		panel.add(prev);
		panel.add(next);
		panel.add(regex);
		frame.getContentPane().add(BorderLayout.NORTH, panel);
		frame.setSize(800, 800);
		frame.setVisible(true);

	}

	private void configureScroller(JScrollPane scroller) {
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);	
	}
	
	private void configureTextArea() {
		text.requestFocusInWindow();
		text.setLineWrap(true);
	}
}