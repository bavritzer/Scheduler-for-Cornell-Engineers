import javax.*;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;

public class GUI extends JFrame implements ActionListener, ChangeListener,
MouseListener, MouseMotionListener{

	public static String title = "My Major Worksheet";	//default title
	public static Path alph;
	public JFileChooser fc;
	boolean start = false;
	JWindow win;
	JPanel panel;
	JPanel p1 = new JPanel();
	JPanel p2 = new JPanel();
	JPanel p3= new JPanel(); 
	JPanel p4= new JPanel();
	JPanel p5= new JPanel();
	JPanel p6 = new JPanel();
	JScrollPane scroll;
	JScrollPane scroll1;
	JScrollPane scroll2;
	JButton finalize;
	JButton finalized;
	JButton finished;
	JButton select1;
	JButton select2;
	JButton select3;
	JLabel liblabel;
	JLabel alabel;
	JLabel ilabel;
	JLabel glabel;
	JLabel clabel;
	JComboBox<String> major;
	JComboBox<String> minor;
	JComboBox<String> libarts;
	JComboBox<Character> sems;
	JCheckBox[] abox;
	JCheckBox[] ibox;
	JProgressBar prog;
	JOptionPane popup;
	String la;
	String mj;
	String mi;
	Schedule schedule;
	Boolean programselected = false;
	Boolean timetoschedule = false;
	int ns;
	public GUI(){
		super(title);
		setup();
		addButtons();
		this.pack();
		this.setVisible(true);
	}
	//sets up the GUI window
	public void setup(){
		this.setVisible(false);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		this.setBackground(Color.RED);
		this.setSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		//panel.setBorder(new EmptyBorder(5,5,5,5));
		this.setContentPane(panel);
	}
	public void addButtons(){
		popup = new JOptionPane();
		popup.setName("Usage Notes");
		popup.setMessageType(NORMAL);
		win = new JWindow();
		int h = Toolkit.getDefaultToolkit().getScreenSize().height;
		int w = Toolkit.getDefaultToolkit().getScreenSize().width;
		win.setPreferredSize(new Dimension((int)(300/1440.0*w), (int)(300*h/900.0)));
		win.setLocation(new Point((int)(w/2.0), (int)(h/2.0)));
		win.setVisible(false);
		popup.setMessage("This app may not work well for\ncertain majors like ECE due to an abundance of restricted electives not well-defined in \nthe engineering handbook. It\nwill not schedule any unclear requirements\nsuch as 'Design Elective' which are \nundefined in the handbook - you should be able to\ntell which these are from the displayed notes. \nOtherwise, it should make a viable schedule\nand store it where you specify. \nProceed?");
		win.add(popup);
		finalize = new JButton("Get Requirements");
		select1  = new JButton("Select Major");
		select2 = new JButton("Select Minor");
		select3 = new JButton("Select Number of Semesters");
		panel.setBackground(Color.RED);
		panel.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
		try {
			Major.setTypes();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Minor.orderTypes();
		libarts = new JComboBox<String>(Major.subjects);
		major = new JComboBox<String>(Major.types);
		minor = new JComboBox<String>(Minor.types);
		sems = new JComboBox<Character>(new Character[]{'8', '7', '6', '5', '4'});
		alabel = new JLabel("Major: ");
		ilabel = new JLabel("Minor: ");
		liblabel = new JLabel("Liberal Studies: ");
		glabel = new JLabel("Number of Semesters: ");
		p1.add(alabel);
		select1.setEnabled(true);
		select2.setEnabled(true);
		//		select1.addActionListener(new ActionListener(){
		//			public void actionPerformed(ActionEvent e){
		//				if(e.getSource().equals(select1)){
		//					select1.setEnabled(false);
		//					mj = (String)major.getSelectedItem();
		//					major.setEnabled(false);
		//					//System.out.println(mj);
		//				}
		//			}
		//		});
		//		select2.addActionListener(new ActionListener(){
		//			public void actionPerformed(ActionEvent e){
		//				if(e.getSource().equals(select2)){
		//					select2.setEnabled(false);
		//					mi = (String)minor.getSelectedItem();
		//					minor.setEnabled(false);
		//					//System.out.println(mi);
		//				}
		//			}
		//		});
		finalize.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(e.getSource().equals(finalize)){
					programselected = true;
					select1.setEnabled(false);
					mj = (String)major.getSelectedItem();
					major.setEnabled(false);
					select2.setEnabled(false);
					mi = (String)minor.getSelectedItem();
					minor.setEnabled(false);
					la = (String) libarts.getSelectedItem();
					libarts.setEnabled(false);
					finalize.setEnabled(false);
					finalize.setVisible(false);
					sems.setEnabled(false);
					ns = Integer.parseInt(sems.getSelectedItem().toString());
					//					System.out.println(mj);
					//					System.out.println(mi);
					//					System.out.println(ns);
					schedule = new Schedule(mj, mi, la, ns);
					displayCourses();
				}
			}
		});
		p1.add(major);
		//panel.add(select1);
		p1.add(ilabel);
		p1.add(minor);
		p1.add(liblabel);
		p1.add(libarts);
		//panel.add(select2);
		p1.add(glabel);
		p1.add(sems);
		//panel.add(select3);
		p1.add(finalize);
		p1.setBackground(Color.RED);
		panel.add(p1, BorderLayout.PAGE_START);
		p1.setMaximumSize(p1.getPreferredSize());
		win.setVisible(true);//adds a popup displaying requirements
		int result = popup.showConfirmDialog(win, popup.getMessage(), 
			       "Usage Notes", popup.INFORMATION_MESSAGE);
		if(result == JOptionPane.OK_OPTION){
			this.setVisible(true);
		}
		else{System.exit(0);}
		win.pack();
	}
	//displays courses so that the program can see what you have credit for
	public void displayCourses(){
		if(programselected){
			int h = Toolkit.getDefaultToolkit().getScreenSize().height;
			int w = Toolkit.getDefaultToolkit().getScreenSize().width;
			abox = new JCheckBox[schedule.major.reqs.size()];
			ibox = new JCheckBox[schedule.minor.requirements.length];
			clabel = new JLabel("<html>Please check any classes you have credit for<br> Then click the button below<br>(if it comes up twice, check both)<br>When finished, click finish to save <br>your schedule to downloads.<br>Mouse over any unclear minor <br>requirements for a description.</html>");
			p4.add(clabel);
			p2.add(new JLabel("Major Courses: "));
			for(int i = 0; i <schedule.major.reqs.size(); i++){
				abox[i] = new JCheckBox(schedule.major.reqs.get(i));
				//System.out.println(schedule.major.reqs.get(i));
				abox[i].setVisible(true);
				p2.add(abox[i]);
			}
			p3.add(new JLabel("Minor Courses: "));
			for(int i = 0; i <schedule.minor.requirements.length; i++){
				if(schedule.minor.replacements[i]!=null){
					ibox[i] = new JCheckBox(schedule.minor.replacements[i]);
					ibox[i].setVisible(true);
					String l ="<html>"+schedule.minor.replacements[i]+"<br>"+"is satisfied by:"+"<br><br>";
					for(String q: schedule.minor.requirements[i].split(" or ")){
						l+=q+"<br>";
					}
					l+="</html>";
					ibox[i].setToolTipText(l);
				}
				else{
				ibox[i] = new JCheckBox(schedule.minor.requirements[i]);
				ibox[i].setVisible(true);}
				p3.add(ibox[i]);
			}
			for(int i = 0; i<schedule.major.notes.length; i++){
				String a = schedule.major.notes[i];
				String b = a.substring(0);
				int m = 0;
				int l = (int)(b.length()/70.0);
				while(m<l){//splits lines if too long
					a = a.substring(0, 70*(m+1)+4*m)+"<br>"+a.substring(70*(m+1)+4*m);//4m is for the <br>
					m++;
				}
				a = "<html>"+a.substring(0)+"</html>";
				JLabel j = new JLabel(a);
				j.setMaximumSize(new Dimension(500, 10000));
				p5.add(j);
			}
			finalized = new JButton("Compute Schedule");
			finalized.setVisible(true);
			finished = new JButton("Finish");
			finished.setVisible(false);
			finalized.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getSource().equals(finalized)){
						finalized.setEnabled(false);
						fc = new JFileChooser();
						fc.setDialogTitle("Choose a Location for your Schedule");
						FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF", "pdf");
						fc.setFileFilter(filter);
						fc.setSelectedFile(new File("Schedule.pdf"));
						int q = fc.showSaveDialog(panel);
						if(q==fc.APPROVE_OPTION){
							Path a = fc.getSelectedFile().toPath();
							if(!a.toString().endsWith(".pdf")){
								a = a.resolveSibling(a+".pdf");
							}
						alph = a;}
						if(q==fc.CANCEL_OPTION){
							System.exit(0);
						}
						timetoschedule = true;
						ArrayList<String> a = (ArrayList<String>) schedule.major.reqs.clone();
						String[] b = schedule.minor.requirements.clone();
						for(int i  = 0; i<abox.length; i++){
							if(abox[i].isSelected()){
								//a.set(i,  null);
								a.set(i, a.get(i)+"#^^");
							}
							abox[i].setEnabled(false);
						}
						for(int i  = 0; i<ibox.length; i++){
							if(ibox[i].isSelected()){
								//b[i] = null;
								b[i]+="#^^";
							}
							ibox[i].setEnabled(false);
						}
						ArrayList<String> c = new ArrayList<String>();
						for(String s: a){
							c.add(s);
						}
						schedule.major.reqs = (ArrayList<String>) c.clone();
						c.clear();
						for(String s: b){
							c.add(s);
						}
						schedule.minor.requirements = c.toArray(schedule.minor.requirements);
					}
					recalculate();
					finished.setVisible(true);
					finished.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(e.getSource().equals(finished)){
								finished.setEnabled(false);
								schedule.detClass(schedule.major.reqs, schedule.minor.requirements, schedule.minor.reqType);
								schedule.writePDF();
							}
						}
					});
				}
			});
			p4.add(finalized);
			p4.add(finished);
			//System.out.println(h+";"+w);
			p2.setPreferredSize(new Dimension((int)(600/1440.0*w), (int)(1000*h/900.0)));
			p3.setPreferredSize(new Dimension((int)(500/1440.0*w), (int)(500/900.0*h)));
			p4.setPreferredSize(new Dimension((int)(500/1440.0*w), (int)(500/900.0*h)));
			p5.setPreferredSize(new Dimension((int)(1450/1440.0*w), (int)(600/900.0*h)));
			p2.setBackground(Color.RED);
			p3.setBackground(Color.RED);
			p4.setBackground(Color.RED);
			p5.setBackground(Color.RED);
			scroll = new JScrollPane(p2);
			p2.setAutoscrolls(true);
			scroll.setPreferredSize(p2.getPreferredSize());
			scroll.setViewportView(p2);
			scroll.setBorder(null);
			scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			scroll1 = new JScrollPane(p5);
			p5.setAutoscrolls(true);
			scroll1.setPreferredSize(p5.getPreferredSize());
			scroll1.setViewportView(p5);
			scroll1.setBorder(null);
			scroll1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scroll1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//			MouseMotionListener doScrollRectToVisible = new MouseMotionAdapter() {
//			     public void mouseDragged(MouseEvent e) {
//			        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
//			        ((JPanel)e.getSource()).scrollRectToVisible(r);
//			    }
//			 };
//			p2.addMouseMotionListener(doScrollRectToVisible);
//			p2.setAutoscrolls(true);
			scroll2 = new JScrollPane(p3);
			p3.setAutoscrolls(true);
			scroll2.setPreferredSize(p3.getPreferredSize());
			scroll2.setViewportView(p3);
			scroll2.setBorder(null);
			scroll2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scroll2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			panel.add(scroll, BorderLayout.WEST);
			panel.add(scroll2, BorderLayout.EAST);
			panel.add(p4, BorderLayout.CENTER);
//			panel.add(p5, BorderLayout.SOUTH);
			panel.add(scroll1,  BorderLayout.SOUTH);
		}
	}
	//calculate the schedule: not useful
	public void recalculate(){
		
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}


}


