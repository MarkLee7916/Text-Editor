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
	JTextArea text;
	// Caches searches between calls to regex and search
	List<Integer> searches;
	// Helper data structures for searching and regex searching
	List<Integer> sizes;
	int index;

	public void makeComponents() {
		// Create components
		JFrame frame = new JFrame("Text Editor");
		JPanel panel = new JPanel();
		text = new JTextArea(20, 40);
		JTextField textSearch = new JTextField(20);
		JScrollPane scroller = new JScrollPane(text);
		JButton search = new JButton("Search");
		JButton regex = new JButton("Search(Regex)");
		JButton prev = new JButton("<");
		JButton next = new JButton(">");
		searches = new ArrayList<>();
		sizes = new ArrayList<>();

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
		addFileMenuBehaviour(save, exit, openAppend, open);
		addSearchBehaviour(search, prev, next, regex, textSearch);

		// Configure scroller
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

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

		// Configure textArea
		text.requestFocusInWindow();
		text.setLineWrap(true);
	}

	private void addSearchBehaviour(JButton search, JButton prev, JButton next, JButton regex, JTextField textSearch) {
		// Iterate though textArea, adding matching indexes to the searches List and
		// highlighting the matching words
		search.addActionListener(actionEvent -> {
			text.getHighlighter().removeAllHighlights();
			searches.clear();

			int index = -1;
			while ((index = text.getText().indexOf(textSearch.getText(), index + 1)) != -1) {
				highlightItem(index, textSearch.getText().length(), Color.pink, false);
				searches.add(index);
				sizes.add(textSearch.getText().length());
			}
		});

		// Searches using a regex on a word by word basis
		regex.addActionListener(actionEvent -> {
			text.getHighlighter().removeAllHighlights();
			searches.clear();

			Pattern pattern;
			try {
				pattern = Pattern.compile(textSearch.getText());
			} catch (PatternSyntaxException ex) {
				JOptionPane.showMessageDialog(null, "Invalid regex");
				return;
			}

			int curr = 0;
			int len = text.getText().length();

			for (int i = 1; i < len + 1; i++) {
				if (i == len || text.getText().charAt(i) == ' ') {
					Matcher match = pattern.matcher(text.getText().substring(curr, i));
					if (match.find()) {
						highlightItem(curr, i - curr, Color.pink, false);
						searches.add(curr);
						sizes.add(i - curr);
					}
					curr = i + 1;
				}
			}
		});

		// Moves to next match
		next.addActionListener(actionEvent -> {
			if (searches.isEmpty())
				return;

			if (index >= searches.size() - 1)
				index = -1;

			highlightItem(searches.get(++index), sizes.get(index), Color.cyan, true);
		});

		// Moves to previous match
		prev.addActionListener(actionEvent -> {
			if (searches.isEmpty())
				return;

			if (index == 0)
				index = searches.size();

			highlightItem(searches.get(--index), sizes.get(index), Color.cyan, true);
		});
	}

	private void addFileMenuBehaviour(JMenuItem save, JMenuItem exit, JMenuItem openAppend, JMenuItem open) {
		// Uses JFileChooser to let the user navigate to the file they want, then
		// save the text into it
		save.addActionListener(actionEvent -> {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				saveText(text.getText(), jfc.getSelectedFile());
		});

		exit.addActionListener(actionEvent -> {
			System.exit(0);
		});

		// Uses JFileChooser to let the user to navigate to the file they want, then
		// loads the text from it. Appends the files contents onto the texts contents
		openAppend.addActionListener(actionEvent -> {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				loadText(jfc.getSelectedFile());
		});

		// Uses JFileChooser to let the user to navigate to the file they want, then
		// loads the text from it. Replaces the texts contents with the file contents
		open.addActionListener(actionEvent -> {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				text.setText("");
				loadText(jfc.getSelectedFile());
			}
		});
	}

	// Adds the CTRL F highlight onto the specified indices
	private void highlightItem(int start, int size, Color c, boolean unique) {
		Highlighter highlighter = text.getHighlighter();

		if (unique)
			highlighter.removeAllHighlights();

		HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(c);
		try {
			highlighter.addHighlight(start, start + size, painter);
		} catch (BadLocationException e) {
			JOptionPane.showMessageDialog(null, "Invalid index");
		}
	}

	private void loadText(File file) {
		try (BufferedReader b = new BufferedReader(new FileReader(file))) {
			String line;

			while ((line = b.readLine()) != null)
				text.append(line + "\n");

		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Didn't work, you've most likely entered a file that doesn't exist");
		}
	}

	private void saveText(String str, File file) {
		try (BufferedWriter b = new BufferedWriter(new FileWriter(file))) {
			b.write(str);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Didn't work, you've most likely entered a file that doesn't exist");
		}
	}
}
