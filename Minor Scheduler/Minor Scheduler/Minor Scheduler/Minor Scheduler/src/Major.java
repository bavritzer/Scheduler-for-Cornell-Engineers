import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.net.URL;
import java.nio.file.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class Major {
	public static String qz = "";
	public static String[] types = {"Engineering Physics", "Operations Research and Engineering", "Chemical Engineering", "Materials Science and Engineering", "Mechanical Engineering"};
	public static String[] subjects = {"Psychology", "Sociology", "Applied Economics and Management"};
	public String thisMajor = "";
	public String[] notes;
	public ArrayList<String> reqs;//required courses in string format
	public ArrayList<sched> courses = new ArrayList<sched>();
	public static Path p;
	
	public Major(String s) throws IOException{
		if(!this.contains(types, s)){
//			System.out.println(s+" isn't a real major! Might as well be ILR.");
			return;
		}
		thisMajor = s;
		reqs = getRequirement(this);
		this.reqs = this.removeMins(reqs);
	}
	//removes anything with minimum in it
	public ArrayList<String> removeMins(ArrayList<String> v){
		ArrayList<String> q = new ArrayList<String>();
		for(String i : v){
			if(i.contains("minimum")){
			}
			else q.add(i);
		}
		return q;
	}
	//checks if a String[] contains a given string
	public boolean contains(String[] t, String s){
		for(String i : t){
			if(i.equals(s)){
				return true;
			}
		}
		return false;
	}
	//reverses the order in the reqs field
	public void reverseReqs(){
		Collections.reverse(reqs);
	}
//gets the abbreviation for a given coursetype (like Computer Science -> CS)
	public String abbreviate(String s){
//		if(s.equals("Biological Engineering")){
//			s = "Biological & Environmental Engineering";
//		}
//		if(s.equals("Civil Engineering")){
//			s = "Civil & Environmental Engineering";
//		}
//		if(s.equals("Mechanical Engineering")){
//			s = "Mechanical & Aerospace Engineering";
//		}
		String[] k = s.split(" ");
		String x = "(";
		for(String q: k){
			x+="("+q+")"+".*";
		}
		x+=")";
		s = x;
		s = s.replace("and", "&");
		Pattern p1 = Pattern.compile(s);
		Matcher m1 = p1.matcher(qz);
		int j = 0;
		while(m1.find()){
		j = m1.start();}
//		int j = qz.indexOf(s);
		String f = "";
		String y = qz.substring(0,j);
		Pattern p = Pattern.compile("([A-Z]{2,})");
		Matcher m = p.matcher(y);
		while(m.find()){
			if(!m.group().contains("CDATA")){
			f = m.group();}
		}
		//System.out.println(f);
		return f;
	}
//sets the available majors and subjects
	public static void setTypes() throws IOException{
		ArrayList<String> t = new ArrayList<String>();
		ArrayList<String> jx = new ArrayList<String>();
		String x = "";
		int year = Calendar.getInstance().get(Calendar.YEAR);
		if(Calendar.getInstance().get(Calendar.MONTH)<9) {year--;x ="FA";}
		else{x="SP";}//new version comes out in Sep
		URL website = new URL("https://www.engineering.cornell.edu/academics/undergraduate/curriculum/handbook/upload/"+year+"-Eng-Handbook-PDF.pdf");
		//f = new File(System.getProperty("user.home")+File.separator+"Downloads"+File.separator+"Engineering_Handbook.pdf");
		File f = new File(System.getProperty("user.home")+File.separator+"Engineering_Handbook.pdf");
		if(f.exists()){f.delete();}//update to current version
		Files.copy(website.openStream(), f.toPath());
		p = f.toPath();
		PDFTextStripper st = new PDFTextStripper();
		PDDocument doc = PDDocument.load(f);
		st.setStartPage(1);
		String s = st.getText(doc);
		int v = s.indexOf("Major Programs");
		int g = s.indexOf("Minors", v);
		int i = s.indexOf("Special Programs", g);//for use with minors
		v = s.indexOf("Major: ", v);
		while (v!=-1 && v<g){
			int q = s.indexOf("(", v);
			String h = s.substring(v+"Major: ".length(),  q-1);
			t.add(h.substring(0));
			v = s.indexOf("Major: ", q);
		}
		types = t.toArray(types);
		doc.close();
		URL subj = new URL("https://classes.cornell.edu/api/2.0/config/subjects?json&roster="+x+(year-2000));

		InputStreamReader isr= new InputStreamReader(
				subj.openStream());
		BufferedReader br= new BufferedReader(isr);
		qz = "";
		String b = br.readLine();
		while (b != null){
			qz += b;
			b = br.readLine();}
		//System.out.println(q);
		Pattern p = Pattern.compile("([A-Z]{2,})");
		Matcher m = p.matcher(qz);
		while(m.find()){
			if(!m.group().contains("CDATA")){
			jx.add(m.group());}
		}
		jx.add(0, "Variety (recommended)");
		subjects = jx.toArray(subjects);
	}
//gets major requirements	
	public ArrayList<String> getRequirement(Major m) throws IOException{
		ArrayList<String> requirements = new ArrayList<String>();
			int year = Calendar.getInstance().get(Calendar.YEAR);
			if(Calendar.getInstance().get(Calendar.MONTH)<8) year--;//new version comes out in Sep
//			URL website = new URL("https://www.engineering.cornell.edu/academics/undergraduate/curriculum/handbook/upload/"+year+"-Eng-Handbook-PDF.pdf");
			File f = new File(System.getProperty("user.home")+File.separator+"Engineering_Handbook.pdf");
//			if(f.exists()){f.delete();}//update to current version
//			Files.copy(website.openStream(), f.toPath());
			PDFTextStripper st = new PDFTextStripper();
			PDDocument doc = PDDocument.load(f);
			st.setStartPage(1);
			String s = st.getText(doc);
			int v = s.indexOf("Major Programs");
			String q = m.thisMajor+" Major Check List";
			int k = s.indexOf(q);
			String s1 = s.substring(k-1000, k);
			String s2 = s1.replaceAll("([0-9]{2})", "!@");
			int j = Integer.parseInt(s1.substring(s2.lastIndexOf("!@"), s2.lastIndexOf("!@")+2));
			j+=2;
			int l = s.indexOf("Notes", k);//end of checklist page
//			System.out.println("l: "+l);
			int x = s.indexOf("\n"+Integer.toString(j), l);//end of notes page
			String classPage = s.substring(k+q.length(), l);
//			System.out.println("j: "+j);
//			System.out.println("x: "+x+"and the substring is "+s.substring(k-1, k));
			String notePage = s.substring(l, x);
			String[] c = classPage.split("❑");
			String[] n = notePage.split("(\n[a-z]\\.)");
			String[] classes = new String[c.length-2];
			String[] notes = new String[n.length-1];
			for(int y = 1; y<n.length; y++){
				//System.out.println(n[y]);
				notes[y-1] = " "+n[y-1];
				notes[y-1] = y>1?Character.toString((char)('a'+y-2))+"."+notes[y-1]:("")+notes[y-1];}
			for (int i = 0; i<c.length-2; i++){
				classes[i] = c[i];
				classes[i] = getClass(classes[i]);
			}
			for(String i: classes){
				if(!i.equals(i.toLowerCase())){
					if(!(this.has(requirements, i) && i.matches("([A-Z]{2,}\\s*\\d\\d\\d\\d[a-z]?)")))
					requirements.add(i);
					//System.out.println(i);
				}
			}
			doc.close();
			this.notes = notes;
			return requirements;
	}
		//finds strings of the form AEP 3110 or technical elective etc.
		public String getClass(String s){
			String c = "";
			if(s.matches("(.*\\d[a-z].*)")){
//				System.out.println(s+"a#");
				int q = s.replaceAll("(\\d[a-z])", "###").indexOf("###")+1;
				c = Character.toString(s.charAt(q));
//				System.out.println(s+"###"+c);
			}
			if(s.toLowerCase().matches("(.*(elective)\\s{0,}[a-z]\\s.*)")){
//				System.out.println(s+"b#");
				int q = s.toLowerCase().indexOf("elective")+"elective".length();
				while(s.charAt(q) == ' '){
					q++;
				}
				c = Character.toString(s.charAt(q));
//				System.out.println(s+"###"+c);
			}
			if(s.toLowerCase().contains("advisor-approved elective")){
				return "Advisor-Approved Elective"+(c!=""?" ("+c+")":"");
			}
			if(s.toLowerCase().contains("major-approved elective")){
				return "Major-Approved Elective"+(c!=""?" ("+c+")":"");
			}
			if(s.toLowerCase().contains("technical elective")){
				return "Technical Elective"+(c!=""?" ("+c+")":"");
			}
			if(s.toLowerCase().contains("elective ") 
					&& (('a'<=(s.charAt(s.toLowerCase().indexOf("elective")+"elective".length()))&& (s.charAt(s.toLowerCase().indexOf("elective")+"elective".length()))<='n') || s.charAt(s.toLowerCase().indexOf("elective")+"elective".length()) == ' ')){
				int uo = s.toLowerCase().lastIndexOf("elective ");
				int p = s.indexOf("\n");
				int q = s.indexOf("\n", p+1);
				String x = s.substring(q>-1?q:0,'a'<=(s.charAt(uo+9))&&(s.charAt(uo+9))<='z'?uo+8:uo+9).trim();
				return  x+(c!=""?" ("+c+")":"");
			}
			if(s.toLowerCase().contains("elective")
					&& (('a'<=(s.charAt(s.toLowerCase().indexOf("elective")+"elective".length()))&& (s.charAt(s.toLowerCase().indexOf("elective")+"elective".length()))<='n') || s.charAt(s.toLowerCase().indexOf("elective")+"elective".length()) == ' ')){
				int uo = s.toLowerCase().lastIndexOf("elective");
//				System.out.println(s+"@@@");
				int p = s.indexOf("\n");
				int q = s.indexOf("\n", p+1);
				String x = s.substring(q>-1?q:0,'a'<=(s.charAt(uo+9))&&(s.charAt(uo+9))<='z'?uo+8:uo+9).trim();
				return  x+(c!=""?" ("+c+")":"");
			}
			if(s.toLowerCase().contains("course") 
					&& ((s.length()<s.indexOf("course")+"course".length())||('a'<=(s.charAt(s.toLowerCase().indexOf("course")+"course".length()))&& (s.charAt(s.toLowerCase().indexOf("course")+"course".length()))<='n') || s.charAt(s.toLowerCase().indexOf("course")+"course".length()) == ' ')){
//				System.out.println(s+"#");
				int uo = s.toLowerCase().lastIndexOf("course");
//				System.out.println(uo+"#$");
				int p = s.indexOf("\n");
				int q = s.indexOf("\n", p+1);
				String x = s.substring(q>-1?q:0,'a'<=(s.charAt(uo+"course ".length()))&&(s.charAt(uo+"course ".length()))<='z'?uo+"course ".length()-1:uo+"course ".length()).trim();
				return  x+(c!=""?" ("+c+")":"");
			}
			if(s.toLowerCase().contains("requirement")){
				String q = s.replaceAll("[^\\p{Alnum}&&[^\\s]]", "").trim();
				Character t = q.charAt(q.length()-1);
				return t!=' '?q.substring(0, q.length()-1)+' '+q.charAt(q.length()-1):q+(c!=""?" ("+c+")":"");
			}
			if(s.toLowerCase().contains("first-year writing seminar") || s.toLowerCase().contains("first year writing seminar")){
				return "FWS"+(c!=""?" ("+c+")":"");
			}
			if(s.toLowerCase().contains("engri")){
				return "ENGRI"+(c!=""?" ("+c+")":"");
			}
			if(s.toLowerCase().contains("liberal studies")){
				return "Liberal Studies"+(c!=""?" ("+c+")":"");
			}
			else {Pattern pattern = Pattern.compile("([A-Z]{2,}\\s*\\d\\d\\d\\d[a-z]?)");
			Matcher matcher = pattern.matcher(s);
			String f = "";
			while(matcher.find()){
				f = matcher.group();
				//System.out.println(f);
			}
			if(!f.toLowerCase().equals(f)){
			System.out.println(f+"#f");
				String q = f.trim();
				return q;}
			else return "";}	
		}
		//tells if the arraylist has an ENGRD in it
		public boolean hasENGRD(String[] s){
			if(s== null ){return false;}
			for(String i:s){
				if(i.contains("ENGRD") && thisMajor != "Engineering Physics"){
					return true;
				}
			}
			return false;
		}
		//is an instance of the string found in the list?
		public boolean has(ArrayList<String> l, String s){
			for(String q: l){
				if(q.contains(s)){
					return true;
				}
			}
			return false;
		}
}
