import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileSystemView;

public class FileIO {
	private JTextArea text;

	public FileIO(JTextArea t) {
		text = t;
	}

	public void addFileMenuBehaviour(JMenuItem save, JMenuItem exit, JMenuItem openAppend, JMenuItem open) {
		addOpenBehaviour(openAppend, open);
		addExitBehaviour(exit);
		addSaveBehaviour(save);
	}

	// Lets user navigate to a file and save 
	private void addSaveBehaviour(JMenuItem save) {
		save.addActionListener(actionEvent -> {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				saveText(text.getText(), jfc.getSelectedFile());
		});
	}

	// Exits program and closes GUI
	private void addExitBehaviour(JMenuItem exit) {
		exit.addActionListener(actionEvent -> {
			System.exit(0);
		});
	}

	private void addOpenBehaviour(JMenuItem openAppend, JMenuItem open) {
		// Lets user navigate to a file and loads it, appending onto current content
		openAppend.addActionListener(actionEvent -> {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				loadText(jfc.getSelectedFile());
		});

		// Lets user navigate to a file and loads it, replacing current contents
		open.addActionListener(actionEvent -> {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				text.setText("");
				loadText(jfc.getSelectedFile());
			}
		});
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
