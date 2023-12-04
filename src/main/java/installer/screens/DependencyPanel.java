package installer.screens;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;

import installer.Settings;
import installer.screens.scanner.FileSearcher;
import installer.screens.scanner.Scanner;

import javax.swing.*;

public class DependencyPanel extends Box implements ActionListener, MouseListener, FileSearcher {

	private static final Color foundColor = new Color(160,255,160), 
		notFoundColor = new Color(255,150,150);

	private boolean[] found; 

	private class DependencyCellRenderer implements ListCellRenderer {
		private ListCellRenderer delegate;
		DependencyCellRenderer(ListCellRenderer defaultRenderer){
			delegate = defaultRenderer;
		}		
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Component c = delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (!isSelected){
				c.setBackground(found[index] ? foundColor : notFoundColor);	
			}	
			return c;
		}		
	}

	private JButton startButton;
	private JList jList;	


	public DependencyPanel(){
		this(true);
	}

	public DependencyPanel(boolean withStartButton){
		super(SwingConstants.VERTICAL);

		startButton = new JButton(Settings.getString("depends.start"));
		if (withStartButton){
			startButton.setOpaque(false);
			startButton.addActionListener(this);
			Box startButtonBox = Box.createHorizontalBox();

			startButtonBox.add(startButton);
			startButtonBox.add(Box.createHorizontalGlue());

			this.add(Box.createVerticalStrut(2));
			this.add(startButtonBox);
		}

		found = new boolean[Settings.dependencyFiles.size()];
		for (int i = 0 ; i < found.length; i++){
			found[i] = false;
		}

		jList = new JList(Settings.dependencyFiles.keySet().toArray());
		// wrap this renderer around default renderer;
		jList.setCellRenderer(new DependencyCellRenderer(jList.getCellRenderer()));
		jList.addMouseListener(this);
		jList.setToolTipText(Settings.getString("depends.tooltip"));
		jList.setBackground(Settings.getColor("textfield.back"));
		//jList.setForeground(Settings.getColor("textfield.text"));

		Box listBox = Box.createHorizontalBox();		
		JScrollPane listScroll = new JScrollPane();		
		listScroll.getViewport().add(jList);
		listBox.add(listScroll);
		//listBox.add(Box.createHorizontalGlue());

		this.add(Box.createVerticalStrut(2));
		this.add(listBox);	
		this.add(Box.createVerticalStrut(2));
		
		Box labelBox = Box.createHorizontalBox();
		labelBox.add(Box.createRigidArea(new Dimension(2,2)));
		labelBox.add(new JLabel(Settings.getString("depends.label")));
		labelBox.add(Box.createHorizontalGlue());
		
		this.add(labelBox);
		//this.add(Box.createVerticalGlue());

	}

	private Scanner scanner;

	@Override
	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource().equals(startButton)){
			if (ev.getActionCommand().equals(Settings.getString("depends.start"))){
				Set<String> files = Settings.dependencyFiles.keySet();
				scanner = new Scanner(new ArrayList<String>(files), this);
				scanner.startScan(new File(Settings.PLUGINS_FOLDER));
			} else if (ev.getActionCommand().equals(Settings.getString("depends.stop"))){
				if (scanner != null){
					scanner.stopScan();
				}
			}
		}	
	}


	// -- listen to jList clicks -- //
	@Override
	public void mouseClicked(MouseEvent ev) {
		if (ev.getClickCount() == 2){
			String key = (String) jList.getModel().getElementAt(jList.locationToIndex(ev.getPoint()));
			String url = Settings.dependencyFiles.get(key);
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void found(File f){
		for (int i = 0; i < found.length; i++){
			if (jList.getModel().getElementAt(i).equals(f.getName())){
				found[i] = true;
				jList.repaint();
				return;
			}
		}
	}

	@Override
	public void searchAborted() {
		startButton.setText(Settings.getString("depends.start"));

	}

	@Override
	public void searchFinished() {
		startButton.setEnabled(false);
		startButton.setText(Settings.getString("depends.done"));

	}

	@Override
	public void searchStarted() {
		startButton.setText(Settings.getString("depends.stop"));

	}





}
