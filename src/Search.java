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
	private List<Integer> searches;
	private List<Integer> sizes;
	private int index;
	private JTextArea text;

	public Search(JTextArea t) {
		searches = new ArrayList<>();
		sizes = new ArrayList<>();
		text = t;
	}

	public void addSearchBehaviour(JButton search, JButton prev, JButton next, JButton regex, JTextField textSearch) {
		addSubstringBehaviour(search, textSearch);
		addRegexBehaviour(regex, textSearch);
		addSwitchingBehaviour(prev, next);
	}

	private void addSwitchingBehaviour(JButton prev, JButton next) {
		// Moves to next match
		next.addActionListener(actionEvent -> {
			if (searches.isEmpty())
				return;

			if (index >= searches.size() - 1)
				index = -1;

			highlightUniqueItem(searches.get(++index), sizes.get(index), Color.cyan);
		});

		// Moves to previous match
		prev.addActionListener(actionEvent -> {
			if (searches.isEmpty())
				return;

			if (index == 0)
				index = searches.size();

			highlightUniqueItem(searches.get(--index), sizes.get(index), Color.cyan);
		});
	}

	// Highlights every substring that matches the search
	private void addSubstringBehaviour(JButton search, JTextField textSearch) {
		search.addActionListener(actionEvent -> {
			text.getHighlighter().removeAllHighlights();
			searches.clear();
			sizes.clear();

			if (textSearch.getText().isEmpty())
				return;

			int index = -1;
			while ((index = text.getText().indexOf(textSearch.getText(), index + 1)) != -1) {
				highlightItem(index, textSearch.getText().length(), Color.pink);
				searches.add(index);
				sizes.add(textSearch.getText().length());
			}
		});
	}

	private void addRegexBehaviour(JButton regex, JTextField textSearch) {
		// Highlights every word that matches the regex
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
						highlightItem(curr, i - curr, Color.pink);
						searches.add(curr);
						sizes.add(i - curr);
					}
					curr = i + 1;
				}
			}
		});
	}

	// Adds the CTRL F highlight onto the specified indices
	private void highlightUniqueItem(int start, int size, Color colour) {
		text.getHighlighter().removeAllHighlights();
		highlightItem(start, size, colour);
	}

	private void highlightItem(int start, int size, Color colour) {
		Highlighter highlighter = text.getHighlighter();
		HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(colour);

		try {
			highlighter.addHighlight(start, start + size, painter);
		} catch (BadLocationException e) {
			throw new AssertionError("Invalid index for highlightItem()");
		}
	}
}
