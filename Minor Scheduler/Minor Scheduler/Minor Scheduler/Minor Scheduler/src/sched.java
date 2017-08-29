import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sched {
	public static ArrayList<sched> allscheds = new ArrayList<sched>();
	public static ArrayList<sched> nonexistent = new ArrayList<sched>();
	ArrayList<String> dates = new ArrayList<String>();
	public String title = "";
	int sem;
	boolean b = true;
	public int priority = 1;
	public sched pointer = this;
	public boolean isPrereqfor = false;
	public sched build = this;
	public boolean visible = true;
	public int an;
	public int an1;
	public String name;
	public ArrayList<sched> Prereq = new ArrayList<sched>();
	public ArrayList<sched> Coreq = new ArrayList<sched>();
	public String rawdata="";
	public String classPath; 
	public boolean isLibArts=false;
	public boolean isTE = false;
	public String satEngReq = "";
	public String courseType = "";
	public String roster = "FA17";
	public int courseNum = 0;
	public ArrayList<String> offered = new ArrayList<String>();
	public ArrayList<Integer> semOff = new ArrayList<Integer>();
	public String time;
	public String LType="";
	public String[][]times;
	//saves the new schedule into a file
	public void getSchedule(
			URL inUrl, String outFn) throws IOException {
		try{
			FileWriter fw= new FileWriter(outFn, false); 
			BufferedWriter bw= new BufferedWriter(fw);
			String q = getDescendants(this);
			//q = this.prune(q);
			//System.out.println(q);
			bw.write(q);
			bw.close();
		}
		catch(Exception e){
			throw new IOException();
		}
	}

	public sched(String t){
//		System.out.println(name);
		name = t;
		if(this.name.contains("BIOSM")){//BIOSM is only available in summer
			name = name.replace("BIOSM",  "BIOG");
		}
		if(name.endsWith("#^^")){
			visible = false;
		}
		if(!(Character.isUpperCase(name.charAt(0)))){
			int i = 1;
			while(!Character.isUpperCase(name.charAt(i))){
				i++;
			}
			name = name.substring(i);
		}
		//System.out.println(name);
		int n = name.indexOf(' ');
		int n1 = n+1;
		int n2 = n1;
		while(n2<name.length()){ //only find the number of the prereq etc.
			if(Character.isDigit(name.charAt(n1))){
				n1++;
				n2++;
			}
			else{n2 = name.length();}
		}
		name = name.substring(0, n1);
		for(sched i : nonexistent){
			if(i.name.contains(this.name)|| this.name.contains(i.name)){
				b = false; 
				pointer = i;
			}
		}
		for(sched i : allscheds){
		if(i.name.contains(this.name)|| this.name.contains(i.name)){
			b = false;
			pointer = i;}
		}
		if(b){
//		System.out.println(name+":##$NNN");
		courseType = name.substring(name.substring(0,3).equals("CO:")?3:0, n);//cut string if it starts with CO: for corequisite
		courseNum = Integer.parseInt(name.substring(n+1, n1));
		roster = getRoster(Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR));
		classPath = "https://classes.cornell.edu/api/2.0/search/"
				+ "classes.json?roster="+roster
				+"&subject=" + courseType
				+"&q="+courseNum;
		an = n;
		an1 = n1;
		if(!name.contains("ENGRC 2640")){
		getData();
		//System.out.println(this.name);
		checkReq();
		checkOffering();//TIME CONSUMING - adds the semesters the course was offered
		//getTime();
		updateReq();
		isPreq();
		}
		allscheds.add(this);}
	else{
		this.copyProperties(pointer);
	}
	}
	//returns the highest semester of a prereq in the given list
	public int pSem(ArrayList<sched> k){
		int p = 0;
		for(sched i : this.Prereq){
			if(k.contains(i)){
				p = Math.max(p, i.sem);
			}
		}
		return p;
	}
	//returns the lowest semester of a successive class in the given list
		public int sSem(ArrayList<sched> k){
			int p = 999;
			for(sched i : k){
				if(i.Prereq.contains(this)){
					p = Math.min(p, i.sem);
				}
			}
			return p;
		}
	//returns the current roster String
	public String getRoster(int month, int year){
		String j = "";
		if(month>4&&month<=10){
			j+="FA";
		}
		else j+="SP";
		j+=year-2000;
		return j;
	}
	//decides if this is a special prereq
	public void isPreq(){
		if(rawdata.length()>0){
		int i = Math.max(rawdata.indexOf(this.courseNum+"\",\""+"titleShort"), rawdata.indexOf("titleShort"));
		int j = rawdata.indexOf("titleLong", i);
//		System.out.println(name);
//		System.out.println("i#"+i+"%j**"+j);
//		System.out.println(rawdata);
		String q = rawdata.substring(i, j);
		q = q.substring(q.indexOf(":")+2);
		q = q.substring(0, q.lastIndexOf(",")-1);
		title = q;
		if(title.contains("FWS")){
			isPrereqfor = true;
			return;
		}
		for(String l : getCrosslisting()){
			if(l.contains("ENGRI")||name.contains("ENGRI")){
				isPrereqfor = true;
				return;
			}
		}
		}
	}
	//sets the properties of this sched to those of another sched
	public void copyProperties(sched v){
		this.setSem(v.sem);
		this.Prereq = v.Prereq;
		this.Coreq = v.Coreq;
		this.classPath = v.classPath;
		this.isLibArts = v.isLibArts;
		this.isTE = v.isTE;
		this.courseNum = v.courseNum;
		this.LType =v.LType;
		this.rawdata = v.rawdata;
		this.satEngReq = v.satEngReq;
		this.courseType = v.courseType;
		this.title = v.title;
		this.semOff = v.semOff;
		this.offered = v.offered;
		this.dates = v.dates;
	}
	//sets the sem of a sched to be taken
	public void setSem(int i){
		sem = i;
	}
	//gets the credit hours of a class
	public int getCredits(){
		int k = rawdata.indexOf("unitsMaximum\":");
		return Integer.parseInt(Character.toString(rawdata.charAt(k+"unitsMaximum\":".length())));
	}
	//gets all crosslistings of a course's names
	public ArrayList<String> getCrosslisting(){
		ArrayList<String> a = new ArrayList<String>();
		String s = rawdata;
		Pattern pattern = Pattern.compile("(\\{\"subject\":\""+"[A-Z]{2,}"+"\",\"catalogNbr\":\""+"(\\d\\d\\d\\d)"+"\",\"type\":\"C\"\\})");
		Matcher matcher = pattern.matcher(s);
		String f = "";
		while(matcher.find()){
			f = matcher.group();
			Pattern pattern1 = Pattern.compile("([A-Z]{2,})");
			Matcher matcher1 = pattern1.matcher(f);
			String z = "";
			while(matcher1.find()){
				z = matcher1.group();
			}
			Pattern pattern2 = Pattern.compile("(\\d\\d\\d\\d)");
			Matcher matcher2 = pattern2.matcher(f);
			String u = "";
			while(matcher2.find()){
				u = matcher2.group();
				z+=" "+u;
			}
			a.add(z);
		}
		pattern = Pattern.compile("(\\{\"subject\":\""+"[A-Z]{2,}"+"\",\"catalogNbr\":\""+"(\\d\\d\\d\\d)"+"\",\"type\":\"W\"\\})");
		matcher = pattern.matcher(s);
		f = "";
		while(matcher.find()){
			f = matcher.group();
			Pattern pattern1 = Pattern.compile("([A-Z]{2,})");
			Matcher matcher1 = pattern1.matcher(f);
			String z = "";
			while(matcher1.find()){
				z = matcher1.group();
			}
			Pattern pattern2 = Pattern.compile("(\\d\\d\\d\\d)");
			Matcher matcher2 = pattern2.matcher(f);
			String u = "";
			while(matcher2.find()){
				u = matcher2.group();
				z+=" "+u;
			}
			a.add(z);
		}
		pattern = Pattern.compile("(\\{\"subject\":\""+"[A-Z]{2,}"+"\",\"catalogNbr\":\""+"(\\d\\d\\d\\d)"+"\",\"type\":\"B\"\\})");
		matcher = pattern.matcher(s);
		f = "";
		while(matcher.find()){
			f = matcher.group();
			Pattern pattern1 = Pattern.compile("([A-Z]{2,})");
			Matcher matcher1 = pattern1.matcher(f);
			String z = "";
			while(matcher1.find()){
				z = matcher1.group();
			}
			Pattern pattern2 = Pattern.compile("(\\d\\d\\d\\d)");
			Matcher matcher2 = pattern2.matcher(f);
			String u = "";
			while(matcher2.find()){
				u = matcher2.group();
				z+=" "+u;
			}
			a.add(z);
		}
		return a;
	}
	//stuffs all class data into rawdata
	public void getData() {
		ArrayList<String> dates = new ArrayList<String>();
		dates.add(getRoster((Calendar.getInstance().get(Calendar.MONTH)+6)%12, Calendar.getInstance().get(Calendar.YEAR)));
		dates.add(getRoster((Calendar.getInstance().get(Calendar.MONTH)), Calendar.getInstance().get(Calendar.YEAR)-1));
		dates.add(getRoster((Calendar.getInstance().get(Calendar.MONTH)+6)%12, Calendar.getInstance().get(Calendar.YEAR)-1));
		dates.add(getRoster((Calendar.getInstance().get(Calendar.MONTH)), Calendar.getInstance().get(Calendar.YEAR)-2));
		dates.add(getRoster((Calendar.getInstance().get(Calendar.MONTH)+6)%12, Calendar.getInstance().get(Calendar.YEAR)-2));
		boolean a = false;
//		for(String i : dates){
//			System.out.println("Date: "+i);
//		}
		try{
//			System.out.println(classPath);
			InputStreamReader isr= new InputStreamReader(
					new URL(classPath).openStream());
			BufferedReader br= new BufferedReader(isr);
			String q = "";
			String j = br.readLine();
			while (j != null){
				q += j;
				j = br.readLine();
			}
			q = q.replace("\\u00a0", " ");
			rawdata = q;
		}
		catch(Exception e){
//			System.out.println(e.getMessage());
			if(e.getMessage().contains("Server returned HTTP response code: 500")){
//				if(name.contains("BIONB")){
//				System.out.println("papaya");}
				try{
					for(String i: dates){
						if(!a){
					try{
//						System.out.println(classPath);
						InputStreamReader isr= new InputStreamReader(
							new URL(classPath.replace(roster, i)).openStream());
					BufferedReader br= new BufferedReader(isr);
					String q = "";
					String j = br.readLine();
					while (j != null){
						q += j;
						j = br.readLine();
					}
					rawdata = q;
					a = true;
//					System.out.println("Radishes");
					}
					catch(Exception g){if(dates.indexOf(i) >= dates.size()-1){
						nonexistent.add(this);}
					}
					}
						}
				}
				catch(Exception f){
					nonexistent.add(this);
//					System.out.println(e.getMessage());
				}
			}
		}
//		System.out.println("Pnappl");
	}
	//updates prereq and coreq fields
	public void updateReq(){
		//System.out.println(this.name+"#&*$@#$");
		int pr = rawdata.indexOf("catalogPrereqCoreq\":\"Prerequisite: ")+"catalogPrereqCoreq\":\"Prerequisite: ".length();
		int co = rawdata.indexOf("Corequisite: ")+13;
		if((!rawdata.contains("Prerequisite") && !rawdata.contains("Corequisite")) 
				//|| !Character.isUpperCase(rawdata.charAt(pr)) 
				//|| !Character.isUpperCase(rawdata.charAt(co)) 
				){ //no wonky prereqs or coreqs
			return;
		}
		if(this.name == "ENGRC 2640"){
		return;}
		if(rawdata.contains("Corequisite")){
			if(pr != -1){
				String k = rawdata.substring(pr, co-13);
				k=this.purify(k);
				//k = k.replace(" as", ",");//unreliable keyword as
				//System.out.println(k);
				if(this.name.equals("PHYS 1112")){
					sched m = new sched("MATH 1910");
					if(checkDupeReq(this,m)){
						Prereq.add(m);}
					sched n = new sched("MATH 1120");
					if(checkDupeReq(this,n)){
						Prereq.add(n);}
					sched o = new sched("MATH 1220");
					if(checkDupeReq(this,o)){
						Prereq.add(o);}
				}
				else if(this.name.equals("PHYS 2213")){
					sched m = new sched("MATH 1920");
					if(checkDupeReq(this,m)){
						Prereq.add(m);}
					sched n = new sched("PHYS 1112");
					if(checkDupeReq(this,n)){
						Prereq.add(n);}
					sched o = new sched("MATH 1220");
				}
				else if(this.name.contains("MATH 2240")){
					sched m = new sched("MATH 2210");
					Prereq.add(m);
				}
				else {for (String s : k.split(", ")){
					if(s.length()> 5 && s.contains(" ") && Character.isDigit(s.split(" ")[1].charAt(0))){ //numbered course
						//System.out.println(Prereq.size());
						s = s.replace(".", "");
						//System.out.println(s);
						if(!(s.contains(name)||name.contains(s))){
						sched m = new sched(s);
						if(checkDupeReq(this,m)){
						Prereq.add(m);}}}
				}
				}
			}
			String j = rawdata.substring(co, rawdata.indexOf('"', co));
			j = this.purify(j);
			//j = j.replace(" as", ",");//unreliable keyword as
			//System.out.println(j);
			if(this.name.equals("PHYS 1112")){
				sched m = new sched("MATH 1920");
				if(checkDupeReq(this,m)){
					Coreq.add(m);}
			}
			else {for (String s : j.split(", ")){
				if(s.length()> 5 && s.contains(" ") && Character.isDigit(s.split(" ")[1].charAt(0))){ //numbered course
					//System.out.println('w');
					//System.out.println(s);
					s = s.replace(".", "");
					if(!(s.contains(name)||name.contains(s))){
					sched m = new sched("CO:"+s);
					if(checkDupeReq(this,m)){
					Coreq.add(m);}}}
			}
			}
		}
		else{
			String k = rawdata.substring(pr, rawdata.indexOf('"', pr));
//			System.out.println(pr+"###"+rawdata.indexOf('"', pr));
			k = this.purify(k);
//			k = k.replace(" as", ","); //as is unreliable as a keyword
//			System.out.println(k);
			for (String s : k.split(", ")){
//				System.out.println(s);
				try{
				if(s.length()> 5 && s.contains(" ") && Character.isDigit(s.split(" ")[1].charAt(0))){ //numbered course
					s = s.replace(".", "");
//					System.out.println("ersatz");
					if(!(s.contains(name)||name.contains(s))){
					sched m = new sched(s);
					if(checkDupeReq(this, m)){
					Prereq.add(m);}}}}
				catch(Exception e){
//					System.out.println(e.getMessage());
					//System.out.println(s);
					//System.out.println(this.name);
				}
			}
		}
		for(sched q : Prereq){
			q.updateReq();
		}
		for(sched e: Coreq){
			e.updateReq();
		}
	}
	//gets all descendants
	public String getDescendants(sched a){
		//int i = 0;
		String j = "";
		if(!j.contains(a.name)){
			//if(i==0){
		j+=
				//"Has prereq " + 
			"["+a.name+" ";//to prune, change ; to space
		j+=a.satEngReq+' ';
		for(String u: a.offered){
		j+=u;
	}
		}
			//if(i==1){
				//j+="Has coreq " + a.name+ ";";}
		//}
		ArrayList<sched> q = new ArrayList<sched>();
		q.addAll(a.Prereq);
		//System.out.println("name: "+a.name);
		//printP(a);
		//printC(a);
		q.addAll(a.Coreq);
		for(sched b : q){
			//if(q.indexOf(b)>Prereq.size()-1){i = 1;}

			j+=getDescendants(b);
			j+="]";}
		return j;
	}
	//removes duplicates
	public String prune(String a){
		String[] s = a.split(";");
		String q = "";
		for(String i : s){
			if(!q.contains(i)){
				q+=i+" ";
			}
		}
		return q;
	}
	//prints P then all prereqs
	public void printP(sched c){
		System.out.println("P \n"+c.name+'-');
		for(sched s : c.Prereq){
			System.out.println(s.name+"P");
		}
	}
	//prints C then all coreqs
		public void printC(sched c){
			System.out.println("C \n"+c.name+'-');
			for(sched s : c.Coreq){
				System.out.println(s.name+"C");
			}
		}
		//checks to see if the sched being added is already in the reqs
	public boolean checkDupeReq(sched a, sched b){ //a contains the object with reqs
		ArrayList<sched> q = new ArrayList<sched>();
		if(a.name.contains(b.name)||b.name.contains(a.name)){
			return false;
		}
		q.addAll(a.Coreq);
		q.addAll(a.Prereq);
		for(sched s: q){
			if(b.name.equals(s.name)){
				return false;
			}
		}
		return true;
	}
	//checks to see what requirements in engineering the course satisfies
	public void checkReq(){
		if(rawdata.contains("catalogDistr")){
			int k = rawdata.indexOf("catalogDistr")+15;
			int j = rawdata.indexOf('"', k);
			String distr = rawdata.substring(k,j);
			if(distr.contains("SBA")){LType="SBA";satEngReq+="L-SBA. ";isLibArts = true;}
			if(distr.contains("LA")){LType="LA";satEngReq+="L-LA. ";isLibArts = true;}
			if(distr.contains("CA")){LType="CA";satEngReq+="L-CA. ";isLibArts = true;}
			if(distr.contains("HA")){LType="HA";satEngReq+="L-HA. ";isLibArts = true;}
			if(distr.contains("CE")){LType="CE";satEngReq+="L-CE. TWRQ. ";isLibArts = true;}
			if(distr.contains("KCM")){LType="KCM";satEngReq+="L-KCM. ";isLibArts = true;}
		}
		if(Integer.parseInt(name.substring(an+1, an1))>=3000){
			satEngReq+="TE. ";
			isTE=true;
		}
	}
	//purifies a string to make it usable by class
	public String purify(String j){
		j = j.replace(", and", ",");
		j = j.replace(" and",",");
		j = j.replace(", or", ",");
		j = j.replace(";", ",");
		j = j.replace(" or",",");
		j = j.replace("\\u00a0and", ",");
		j = j.replace("\\u00a0or", ",");
		j = j.replace("or", ",");
		j = j.replace("\\u00a0", ", ");
		j = j.replace(" of", ",");
		j = j.replace("&",  ",");
		//j = j.replace(".", ",");
		j = j.replace("/", ",");
		if(j.indexOf("not")!=-1){
			j=j.substring(0, j.indexOf("not")-1);
		}
		if(j.contains(".")&&j.replaceAll("([a-z]\\.[a-z]\\.)", "######").equals(j)){//abbreviations like e.g. and i.e. are ok
//			String a = j.substring(0,j.indexOf(".")+1);
//			if(a.contains(".")){
//				j = j.substring(0,a.indexOf("."));
//			}
			j = j.substring(0, j.indexOf("."));
		} //2 periods usually indicate multiple notes
		return j;
	}
	@Override
	public boolean equals(Object o){
		if(this == o)return true;
		else if(!(o instanceof sched))return false;
		else return this.name.equals(((sched)o).name);
	}
	//checks when the course is offered
	public void checkOffering(){
		dates.add(getRoster((Calendar.getInstance().get(Calendar.MONTH)+6)%12, Calendar.getInstance().get(Calendar.YEAR)));//sem4 is 0 since 4%4=0; use this year instead of 2 years ago in case of new classes
		dates.add(getRoster((Calendar.getInstance().get(Calendar.MONTH)), Calendar.getInstance().get(Calendar.YEAR)));//sem1
		dates.add(getRoster((Calendar.getInstance().get(Calendar.MONTH)+6)%12, Calendar.getInstance().get(Calendar.YEAR)-1));//sem2
		dates.add(getRoster((Calendar.getInstance().get(Calendar.MONTH)), Calendar.getInstance().get(Calendar.YEAR)-1));//sem3
//		for(String i : dates){
//			System.out.println("date: " + i);
//		}
		if(name.equals("AEP 3560")){//this course was changed last year
			semOff.add(1);
			semOff.add(3);
		}
		else if(name.equals("AEP 3550")){//also changed last year
			semOff.add(2);
			semOff.add(0);
		}
//		else if(name.equals("AEP 3610")){//also changed last year
//			semOff.add(1);
//			semOff.add(3);
//		}
		else{
		for(String i: dates){
		try{InputStreamReader isr= new InputStreamReader(
				new URL(classPath.replace(roster, i)).openStream());//see if a record exists
		offered.add(i);
		semOff.add(dates.indexOf(i));
		}
		catch(Exception g){}
		}
		}
	}
	//gets the Time the course is offered
	public void getTime(){
		//search for lecture times
		String s = "";
		String s1 = findDYTM("\"ssrComponent\":\"LEC\"");
		String s2 = findDYTM("\"ssrComponent\":\"DIS\"");
		String s3 = findDYTM("\"ssrComponent\":\"LAB\"");
		s+=s1;
		if(!s2.equals("")){
		s+="#"+s2;}
		if(!s3.equals("")){
		s+="#"+s3;}
		time = s.substring(0);
		String[] j = time.split("#").clone();
		String [][] timer = new String[j.length][];
		for(int i = 0; i<j.length; i++){
			//System.out.println(j[i]);
			timer[i] = j[i].split("/").clone();
		}
		times = timer.clone();
	}
	public String findDYTM(String s){
		String str = "";
		int t1 = 0;
		while(rawdata.indexOf(s, t1)!=-1){
		if(t1>=1){str+="/";}
		int q = rawdata.indexOf("timeStart", t1);
		int q1 = rawdata.indexOf(":", q)+2;
		int q2 = rawdata.indexOf('"', q1);
		str+= rawdata.substring(q1, q2);
		int p = rawdata.indexOf("timeEnd", q2);
		int p1 = rawdata.indexOf(":", p)+2;
		int p2 = rawdata.indexOf('"', p1);
		str+= "-"+rawdata.substring(p1, p2);
		int l = rawdata.indexOf("pattern\":", p2);
		int l1 = rawdata.indexOf(":", l)+2;
		int l2 = rawdata.indexOf('"', l1);
		str+= "$"+rawdata.substring(l1, l2);
		t1=l2;}
		return str;
	}
}

