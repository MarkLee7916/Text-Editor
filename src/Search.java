import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

public class Search {
	
	// Helper data structures to cache search results
	private List<Integer> searches;
	private List<Integer> sizes;
	private int index;
	
	// Central text area for everything to operate on
	private JTextArea text;
	
	public Search(JTextArea t) {
		searches = new ArrayList<>();
		sizes = new ArrayList<>();
		text = t;
	}

	public void addSearchBehaviour(JButton search, JButton prev, JButton next, JButton regex, JTextField textSearch) {
		// Iterate though textArea, adding matching indexes to the searches List and
		// highlighting the matching words
		search.addActionListener(actionEvent -> {
			text.getHighlighter().removeAllHighlights();
			searches.clear();
			sizes.clear();

			if (textSearch.getText().isEmpty())
				return;

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
			sizes.clear();

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
				if (i == len || text.getText().charAt(i) == ' ' || text.getText().charAt(i) == '\n') {
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
}
