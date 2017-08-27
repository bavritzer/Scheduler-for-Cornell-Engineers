import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;

public class Schedule {
	public String maj;
	public String min;
	public String libst;
	public Major major;
	public Minor minor;
	public ArrayList<sched> classes = new ArrayList<sched>();
	public int numSems=8;
	public int[] classesPerSem;
	public Calendar schedule;
	public static String[] variety = new String[]{"CS 2850", "ECON 1110", "PSYCH 1101", "BIOEE 1540", "HADM 4300","ENGRC 3500"};

	public Schedule(String tm, String ti, String tlib, int sems){
		maj = tm;
		min = ti;
		libst = tlib;
		try {
			major = new Major(maj);
			minor = new Minor(min);
		} catch (IOException e) {
		}
		numSems = sems;
		classesPerSem = new int[(int)numSems];
		//detClass(major.reqs, minor.requirements, minor.reqType);
	}
	//
	public void setSems(){
//		for(int i = 0; i<numSems; i++){
//			if(i==numSems-1){classesPerSem[i] = classes.size()%(classesPerSem[i-1]*(i));}
//			else{classesPerSem[i] = (int) Math.floor((classes.size()/(numSems-1)));}
//		}
		int k = classes.size()/numSems;
		int k1 = classes.size()%numSems;
		int k2 = k1!=0?Math.floorDiv(numSems,k1):0;
		for(int i = 0; i<numSems; i++){
			classesPerSem[i] = k;
			if(k1>0&&k2>0&&i%k2==0){
				classesPerSem[i]++;
				k1--;
			}
		}
	}
	//determines if a class satisfies a requirement given by a string
	public boolean satisfiesReq(String s, sched c, ArrayList<String> m){
		if(major.reqs.contains(c.name) || m.contains(c.name)){
			return false;
		}
		if(c.isLibArts&&!s.contains("Liberal")){
			return false;
		}
		for(String z: c.getCrosslisting()){
			if(major.reqs.contains(z) || m.contains(z))
				return false;
		}
		if(s.contains("ENGRI") || (s.contains("ENGRD")&&!s.contains("@@@"))){
			return c.courseType.equals("ENGRI") || c.courseType.equals("ENGRD");
		}
		if(s.contains("ENGRI") || (s.contains("ENGRD")&&s.contains("@@@"))){
			return c.name.equals(s);
		}
		if(s.matches("([A-Z]{2,}\\s\\d\\d\\d\\d[a-z]{0,5})")){//like AEP 2640d
			return false; //because we don't want a duplicate
			//return (s.contains(c.name));
		}
		if(s.contains("Liberal Arts")){
			return c.isLibArts;
		}
		if(s.contains("Elective")){
			if(Integer.toString(c.courseNum).endsWith("999")||Integer.toString(c.courseNum).endsWith("090")||Integer.toString(c.courseNum).endsWith("998")){
				return false; //these electives are research
			}
			else if(s.toLowerCase().equals("advisor-approved elective")){
				return true;
			}
			else if(s.toLowerCase().contains((major.abbreviate(major.thisMajor)).toLowerCase())){
				if(s.matches("(.*\\d\\d\\d\\d.*)")){
					Pattern pattern = Pattern.compile("(\\d\\d\\d\\d.)");
					Matcher matcher = pattern.matcher(s);
					String f = "";
					while(matcher.find()){
						f = matcher.group();
						//System.out.println(f);
					}
					if(f.endsWith("+")){
						return c.isTE&&c.courseType.equals(major.abbreviate(major.thisMajor))&&c.courseNum>=Integer.parseInt(f.substring(0, f.length()-1));
					}
					if(f.endsWith("s")||f.endsWith("-")){
						return c.isTE&&c.courseType.equals(major.abbreviate(major.thisMajor))&&(c.courseNum>=Integer.parseInt(f.substring(0, f.length()-1))&&(c.courseNum<=1000+Integer.parseInt(f.substring(0, f.length()-1))));
					}
					else{
						return c.isTE&&c.courseType.equals(major.abbreviate(major.thisMajor))&&(c.courseNum>=Integer.parseInt(f.substring(0, f.length()-1))&&(c.courseNum<=1000+Integer.parseInt(f.substring(0, f.length()-1))));
					}
					
				}
				else return c.isTE&&c.courseType.equals(major.abbreviate(major.thisMajor));
			}
			else if(s.toLowerCase().equals("major-approved elective")){
				return c.isTE;
			}
			else if(s.toLowerCase().equals("technical elective")){
				return c.isTE;
			}
			else{
				//check the appropriate note
			}
		}
		return false;
	}
	//determine the classes in the schedule given minor strings, major strings
	public void detClass(ArrayList<String> mac, String[] mic, int[] rt){
		ArrayList<String> used = new ArrayList<String>();
		int hard = rt[0];
		int med = rt[1];
		int soft = rt[2];
		//		for(int i = 0; i<mac.size(); i++){
		//			mac.set(i,  this.getString(mac.get(i)));
		//		}
		major.reqs = removeNulls(major.reqs);
		for(String i : major.reqs){
			if(i.matches("(.*[A-Z]{2,}\\s\\d\\d\\d\\d[a-z].*)")){
				major.reqs.set(major.reqs.indexOf(i),i.substring(0, i.length()-1));//remove end chars
			}
		}
		mac = (ArrayList<String>) major.reqs.clone();
		//set up requirements lists: hard is one course, med is two or more, soft is a format
		ArrayList<Object> hards = new ArrayList<Object>();
		for(int i = 0; i<hard; i++){
			hards.add(mic[i]);
		}
		ArrayList<Object> meds = new ArrayList<Object>();
		for(int i = 0; i<med; i++){
			meds.add(mic[i+hards.size()]);
			//System.out.println(meds.get(i));
		}
		ArrayList<Object> softs = new ArrayList<Object>();
		for(int i = 0; i<soft; i++){
			softs.add(mic[i+hards.size()+meds.size()]);
		}
//		System.out.println("hsize:"+ hards.size()+" msize:"+ meds.size()+" ssize:"+ softs.size());
		Collections.reverse(mac);
		major.reverseReqs();
		//while(!(hards.size()==0) ||!(meds.size()==0)){
			for(Object i: hards){
				if(i!=null){
					sched n = new sched((String) i);
				boolean a = true;
				if(major.reqs.contains(i)&& !used.contains(i)){
					//hards.remove(i);//only want non-fulfilled requirements
					a = false;
					used.add((String)i);
				}
				for(String j : n.getCrosslisting()){
					if(major.reqs.contains(j)&&!used.contains(j)){
						a = false;//note that two hard requirements cannot be the same
						used.add((String)j);
					}
				}
				for(String j : major.reqs){
					if(a&&this.satisfiesReq(j, n, mac)){
						major.reqs.set(major.reqs.indexOf(j), (String)i);
						//hards.remove(i);
						a = false;
//						System.out.println("hard "+n.name+" satisfies "+j);
					}
				}
				if(a){
					major.reqs.add(i.toString());
//					System.out.println("hard "+i.toString()+" failed to satisfy");
					mac.add(i.toString());
				}
			}
			}
			for(Object i: meds){
				if(i!=null){
//				System.out.println("med: "+i);
				String[] q = i.toString().split(" or ");
//				System.out.println("qsize =  "+q.length);
				boolean k = false;
				boolean b = false;//b only relates to the class, k is the requirement fulfillment
				for(String z: q){
					sched s = new sched(z);
					if(major.reqs.contains(s.name)&&!used.contains(s.name)){
						if(mac.contains(s.name)){
						k = true;}
						b = true;
						used.add(s.name);
					}
					for(String j: s.getCrosslisting()){
						if(major.reqs.contains(j)&&!used.contains(j)){
							if(mac.contains(j)){
							k = true;}
							b = true;
							used.add(j);
						}
					}
//					System.out.println("mclass: "+z+" and k is "+k);
					for(String j : major.reqs){
						if(this.satisfiesReq(j, s, major.reqs)&&!k&&!b){
							//System.out.println(q.length);
							major.reqs.set(major.reqs.indexOf(j), z);
							//meds.remove(i);
							k = true;
//							System.out.println("med "+s.name+" satisfies "+j);
						}
					}
					if(!k&&!b&&!used.contains(s.name)){//do not add a used requirement
					major.reqs.add(z);
					k = true;
//					System.out.println("med "+z+" failed to satisfy");
					//meds.remove(i);
					}
				b = false;}
				}
			}
		//}
		//code to solve soft requirements
		ArrayList<String> al = new ArrayList<String>();
		for(Object q : softs){
			if(q!=null){
//			System.out.println(q);
			if(!al.contains(q.toString()) ){//|| !q.toString().matches("([A-Z]{2,}\\s\\d\\d\\d\\d[a-z].*)")){
			String n = Interpret(q.toString());
			String k = getData1(n);
			ArrayList<String> z = extractClasses(k);//gets all the classes that satisfy req
			ArrayList<sched> s = Courseify(z);
			boolean t = false;
			for(sched r: s){
//				System.out.println("Description:  "+r.name);
				if(!major.reqs.contains(r)){
				//System.out.println("Sched processed: "+r.name+s.indexOf(r));
				if(checkPreq(r, major.reqs)){
					for(String j : major.reqs){
						//System.out.println(r.name+" checks "+"j is: "+j+" and "+satisfiesReq(j, r, major.reqs)+" and t is" + t);
						if(satisfiesReq(j,  r, major.reqs)&&!t&&r.getCredits()>=3&&this.checkPreq(r, major.reqs)){
							major.reqs.set(major.reqs.indexOf(j), r.name);
							t = true;
//							System.out.println("soft "+r.name+" satisfies "+j);
						}
					}
					
				}
			}
			}
			if(!t){
				int i = 0;
				while(i<s.size()-1 && !satisfiesReq("Major-Approved Elective", s.get(i), major.reqs)){
					i++;
				}
//				System.out.println("rohanmobile###"+s.get(i).name+"$"+satisfiesReq("Major-Approved Elective", s.get(i), major.reqs));
					major.reqs.add(s.get(i).name);
//					for(String mn : s.get(i).getCrosslisting()){
//					System.out.println(mn);
//					}
					t = true;
//					System.out.println("soft "+s.get(i).name+" failed to satisfy");
				}
			al.add(q.toString());}
			}
		}
		Collections.reverse(mac);
		major.reverseReqs();
		major.reqs = selectFinal(major.reqs);
		classes = Courseify(major.reqs);
		classes = clearVisible(classes);
		setSems();
		classes = sortClasses(classes);
	}
	//clears invisible elements
	public ArrayList<sched> clearVisible(ArrayList<sched> s){
		ArrayList<sched> q = new ArrayList<sched>();
		for(sched i : s){
			if(i.visible){
				q.add(i);
			}
		}
		return q;
	}
	//sorts the classes
	public ArrayList<sched> sortClasses(ArrayList<sched>v){
		int maxsem = 1;
		v.sort((s1, s2) -> s1.courseNum - s2.courseNum);
		for(sched i : v){
			i.setSem(1);
		}
		for(sched i : v){
			for(sched j : v){
//				if( i.courseType.equals(j.courseType) && (Math.abs(i.courseNum-j.courseNum))%1000==10){
//					if(i.courseNum>j.courseNum){
//						i.setSem(j.sem+1);
//						j.isPrereqfor = true;
//					}
//					if(i.courseNum<j.courseNum){
//						j.setSem(i.sem+1);
//						i.isPrereqfor = true;
//					}
//				}
				for(sched q : i.Prereq){
				if(q.name.contains(j.name)){
					i.setSem(Math.max(j.sem+1, i.sem));//don't lower sem if an easy class is listed as a prereq
					j.isPrereqfor = true;
					j.build = i;
					i.priority = i.sem;
//					System.out.println("The class" +j.name +" is a prerequisite for " + i.name);
				}
				}
			}
		}
		for(sched i : v){
			maxsem = Math.max(maxsem,  i.sem);
		}
		for(int i = numSems-1; i>=0; i--){
//			System.out.println("for sorting, i is: "+i);
			int count = 0;
			for(sched j : v){
				if(j.sem ==maxsem){
					count++;
				}
			}
//			System.out.println("for sorting, count is: "+count + " and maxsem is "+maxsem+" and cps[i] is "+classesPerSem[i]);
			if(count < classesPerSem[i]){
				int j = classesPerSem[i]-count;//this bit fills the actual semesters
				int l = 0;
//				System.out.println("for sorting, j is: "+j);
				while(j>0 && l<v.size()){
					sched f = v.get(l);
					if(f.sem==1 && !f.isPrereqfor){
						f.setSem(maxsem);
						v.set(l,  f);
						j--;
					}
					l++;
				}
			}
			maxsem = Math.max(1,  maxsem-1);//don't go under 1
		}
		v.sort((s1, s2)->(100000*s1.sem+s1.courseNum-10000*(s1.isPrereqfor?1:0))-(100000*s2.sem+s2.courseNum-10000*(s2.isPrereqfor?1:0)));//take prereqs as early as possible
		for(sched i : v){
			int y = v.indexOf(i);
			int j = -1;
			int b = 0;
			while(j<y){
				j+=classesPerSem[b];
				b++;
			}
			i.setSem(b);//find the actual semester it will be taken - must be done before checking eligibility
		}
		for(sched i : v){
			if(i.isPrereqfor&&i.priority==1){
				i.priority = 0;
			}
		}
		Collections.reverse(v);//want to check later requirements first
		for(sched q : v){
			int pp = 0;
			boolean qlp = true;
			while(!(q.semOff.contains(q.sem%4)&&q.pSem(v)<q.sem)&&q.sem>=1&& qlp&&pp<100){//want each thing to be possible and after its prereqs
				for(sched k : v){
					boolean g = true;//would this violate a prereq ordering
					for(sched r : q.Prereq){
						for(sched e : v){
							if((e.name.contains(r.name)||r.name.contains(e.name))&&e.sem>k.sem){
							g = false;}
						}
					}
					for(sched u : k.Prereq){
						for(sched e: v){
							if((e.name.contains(u.name)||u.name.contains(e.name))&&e.sem>q.sem){
								g = false;}
						}
					}
//					if(q.name.equals("AEP 3550")){
////					System.out.println("q: "+q.name+" k: "+k.name+" sems are: "+q.sem+"#"+k.sem+" priority: "+k.priority+" g: "+g+" pp: "+pp+" require: "+k.semOff.contains(q.sem%4)+q.semOff.contains(k.sem%4)+" qlp: "+qlp);
//					}
					if(
							!(k.isPrereqfor)&&
							k.sem>q.pSem(v)&&q.sem>k.pSem(v)&&q.sem<k.sSem(v)&&k.sem<q.sSem(v)&&//if k is a prereq don't put it after the required class
							q.semOff.contains(k.sem%4)&&qlp&&k.priority<=pp&&g&&k.semOff.contains(q.sem%4)){//if the switch is viable
						sched l = k;
						int h = v.indexOf(q);
						int t = v.indexOf(k);
						int st = q.sem;
						q.setSem(l.sem);
						l.setSem(st);//swaps the two classes in the schedule
						v.set(t, q);
						v.set(h, l);
						qlp = false;
					}
				}
				pp++;
			}
		}
		for(sched q : v){
			int pp = 0;
			boolean qlp = true;
			while(!(q.semOff.contains(q.sem%4)&&q.pSem(v)<q.sem)&&q.sem>=1&& qlp&&pp<100){//want each thing to be possible and after its prereqs
				for(sched k : v){
					boolean g = true;//would this violate a prereq ordering
					for(sched r : q.Prereq){
						for(sched e : v){
							if((e.name.contains(r.name)||r.name.contains(e.name))&&e.sem>k.sem){
							g = false;}
						}
					}
					for(sched u : k.Prereq){
						for(sched e: v){
							if((e.name.contains(u.name)||u.name.contains(e.name))&&e.sem>q.sem){
								g = false;}
						}
					}
//					if(q.name.equals("AEP 3550")){
//					System.out.println("q: "+q.name+" k: "+k.name+" sems are: "+q.sem+"#"+k.sem+" priority: "+k.priority+" g: "+g+" pp: "+pp+" require: "+k.semOff.contains(q.sem%4)+q.semOff.contains(k.sem%4)+" qlp: "+qlp);
//					}
					if(
							!(k.isPrereqfor)&&
							k.sem>q.pSem(v)&&q.sem>k.pSem(v)&&q.sem<k.sSem(v)&&k.sem<q.sSem(v)&&//if k is a prereq don't put it after the required class
							q.semOff.contains(k.sem%4)&&qlp&&k.priority<=pp&&g&&k.semOff.contains(q.sem%4)){//if the switch is viable
						sched l = k;
						int h = v.indexOf(q);
						int t = v.indexOf(k);
						int st = q.sem;
						q.setSem(l.sem);
						l.setSem(st);//swaps the two classes in the schedule
						v.set(t, q);
						v.set(h, l);
						qlp = false;
					}
				}
				pp++;
			}
		}
		Collections.reverse(v);
		return v;
	}
	//calculate the priority of all scheds in a list
	public ArrayList<sched>calP(ArrayList<sched> v){
		for(sched i : v){
			int l = i.priority;
			for(sched j : i.Prereq){
				if(v.contains(j)){
					l = Math.max(l, j.sem+1);
				}
			}
			i.priority = l;
		}
		return v;
	}
	//removes nulls
	public ArrayList<String> removeNulls(ArrayList<String> s){
		ArrayList<String> z = new ArrayList<String>();
		for(String i : s){
			if(i!=null){
			z.add(i);}
		}
		return z;
	}
	//tells the full size of a class's prereq history
	public int PSize(sched s){
		int i = 0;
		for(sched j : s.Prereq){
			i++;
			i+=PSize(j);
		}
		return i;
	}
	
	//assigns specific classses to untaken general requirements
	public ArrayList<String> selectFinal(ArrayList<String> s){
		String g = "#^^";//used for invisible requirements
		int t = 0;//keeps track of the index in the preselected courses
		boolean a = true;//used to see if designated liberal arts subject has exhausted itself
		ArrayList<String> typ = new ArrayList<String>();
		int x = 0;//typ and x keep track of lib studies to make sure 3 categories are fulfilled
		String u;
		if(libst.contains("Variety")){
			libst = "PSYCH";
			a = false;
		}
		int month = Calendar.getInstance().get(Calendar.MONTH);
		if((month<9)){
			u = "FA";
		}
		else u = "SP";
		u+=(Calendar.getInstance().get(Calendar.YEAR)-2000);
		String z = "https://classes.cornell.edu/api/2.0/search/classes.json?roster="+u+"&subject=ENGL&q=FWS";
		String s1 = getData1(z);
		ArrayList<String> FWS = extractClasses(s1);
		z="https://classes.cornell.edu/api/2.0/search/classes.json?roster="+u+"&subject=ENGRI&q=ENGRI";
		s1 = getData1(z);
		ArrayList<String> ENGRI = extractClasses(s1);
		z = Interpret(libst);
		s1 = getData1(z);
		ArrayList<String> LibSt = extractClasses(s1);
		z = Interpret(major.abbreviate(major.thisMajor));
//		System.out.println(major.abbreviate(major.thisMajor)+"&**$#$#");
		s1 = getData1(z);
		ArrayList<String> maj = extractClasses(s1);
		s1 = getData1(z.replace(u, u.contains("FA")?u.replace("FA", "SP"):u.replace("SP", "FA").replace(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)-2000), Integer.toString(Calendar.getInstance().get(Calendar.YEAR)-2001))));
		maj.addAll(extractClasses(s1));
		maj = this.removeDupes(maj);
		ArrayList<String> q = new ArrayList<String>();
		for(String i : s){
			if(i.contains("FWS")){
				boolean qy = false;
				if(i.contains(g)){
					qy = true;
				}
//				System.out.println(FWS.size()+"fws");
				int y = (int)(Math.random()*FWS.size());
//				System.out.println("yf: "+y);
				i = FWS.get(y);
				if(qy){
					i+=g;
				}
//				System.out.println("if%%"+i);
				q.add(i);
				i = i.replaceAll(g,  "");
				FWS.remove(i);
			}
			else if(i.contains("ENGRI")){
//				System.out.println(FWS.size()+"f");
//				System.out.println(ENGRI.size()+"e");
				boolean qy = false;
				if(i.contains(g)){
					qy = true;
				}
				int y = (int)(Math.random()*ENGRI.size());
//				System.out.println("ye: "+y);
				i = ENGRI.get(y);
//				System.out.println("ie&&"+i);
				if(qy){
					i+=g;
				}
				q.add(i);
				i = i.replaceAll(g,  "");
				ENGRI.remove(i);
			}
			else if (i.contains("Liberal Studies")){
				boolean qy = false;
				if(i.contains(g)){
					qy = true;
				}
				if(!a){
					if(!major.reqs.contains(variety[t])){
					i=variety[t];
					if(qy){
						i+=g;
					}
					q.add(i);}
					t++;
				}
				else{
				int y = 0;
				sched b = new sched(LibSt.get(y));
				while((major.reqs.contains(b.name)||!checkPreq(b, major.reqs)||!b.isLibArts||b.getCredits()<3)&&y<LibSt.size()-1){
					y++;
					while(!LibSt.get(y).contains(libst)&&y<LibSt.size()-1){
					y++;}
//					System.out.println(LibSt.get(y)+"#####");
					b = new sched(LibSt.get(y));
//					System.out.println(LibSt.get(y)+"@&&&");
				}
//				System.out.println("Dorne");
				if(y>=LibSt.size()-1){
					a = false;
				}
				if(y<LibSt.size()&&b.isLibArts&&checkPreq(b, major.reqs)){
				i = LibSt.get(y);
				if(qy){
					i+=g;
				}
				q.add(i);
				i.replace(g,  "");
//				System.out.println(i+"***%%");
				LibSt.remove(i);
				x++;
				if(!typ.contains(b.LType)){
				typ.add(b.LType);}
				a = (6-x)>(3-typ.size());
				}
				}
			}
			else if(i.contains("Major-Approved")||i.toLowerCase().contains("technical elective")||i.contains("Advisor-Approved")
					||(i.toLowerCase().contains("elective") && i.contains(major.abbreviate(major.thisMajor)))
					){
				int y = 0;
				sched b = new sched(maj.get(y));
				boolean hh = true;
				for(String su: b.getCrosslisting()){
					for(String xb : major.reqs){
						if(xb.contains(su)||su.contains(xb)){
							hh = false;
						}
					}
				}
				while((major.reqs.contains(b.name)||!checkPreq(b, major.reqs)||!b.isTE||b.getCredits()<3)&&y<maj.size()-1||!satisfiesReq(i, b, major.reqs)||!hh){
					y++;
					hh = true;
					for(String su: b.getCrosslisting()){
						for(String xb : major.reqs){
							if(xb.contains(su)||su.contains(xb)){
								hh = false;
							}
						}
					}
					while(!maj.get(y).contains(major.abbreviate(major.thisMajor))&&y<maj.size()-1){
					y++;}
//					System.out.println(maj.get(y)+"#####");
//					System.out.println(major.reqs.contains(b.name)+"$$"+checkPreq(b, major.reqs)+"&&"+b.isTE+"**"+b.getCredits()+"^^"+(y<maj.size()-1));
					b = new sched(maj.get(y));
//					System.out.println(maj.get(y)+"@&&&");
				}
//				System.out.println("Dorne");
				if(y>=maj.size()-1){
					a = false;
				}
				if(y<maj.size()&&b.isTE&&checkPreq(b, major.reqs)&&satisfiesReq(i, b, major.reqs)){
//				System.out.println("i: "+i);
					i = maj.get(y);
//					System.out.println("c: "+i);
				q.add(i);
//				for(String h : major.reqs){
//					System.out.println(h+"&^^^^^");
//				}
//				System.out.println(major.reqs.contains(b.name)+"#^%");
//				System.out.println(i+"***%%");
				maj.remove(i);}	
			}
		}
		s.addAll(q);
		return s;
	}
	//checks a class's prereqs to see if they're in the requirements list
	public boolean checkPreq(sched r, ArrayList<String> m){
		for(sched b: r.Prereq){
			if(!m.contains(b.name)){
				return false;
			}
		}
		return true;
	}
	//removes duplicates from a List of Strings
	public ArrayList<String> removeDupes(ArrayList<String> s){
		ArrayList<String> h = new ArrayList<String>();
		for(String k : s){
			if(!h.contains(k)){
				h.add(k);
			}
		}
		return h;
	}
	//removes duplicates from an ArrayList
	public ArrayList<sched> removeDups(ArrayList<sched> s){
		ArrayList<sched> h = new ArrayList<sched>();
		for(sched k : s){
			if(!h.contains(k)){
				h.add(k);
			}
		}
		return h;
	}
	//interprets a soft requirement of the form CS 3000s, 4000s, or 5000s course and makes it into a URL
	public String Interpret(String q){
		String v="";
		while(q.contains("/")){ //reads exceptions
			int l = q.lastIndexOf("/");
			v+=(";;;"+q.substring(l+1));//;;; is a separator for classes
			q = q.substring(0, l);
		}
		String u;
		int month = Calendar.getInstance().get(Calendar.MONTH);
		if((month<9)){
			u = "FA";
		}
		else u = "SP";
		u+=(Calendar.getInstance().get(Calendar.YEAR)-2000);
		String s = "https://classes.cornell.edu/api/2.0/search/classes.json?roster="+u;
		s+="&subject=";
		boolean j = true;
		for(Character i : q.toCharArray()){
			if(Character.isUpperCase(i)&&j){
				s+=i;
			}
			else{j = false;}
		}
		boolean y = true;
		boolean l = true;
		for(Character i : q.toCharArray()){
			if(Character.isDigit(i)){
				if(l){s+="&classLevels[]=";
				l=false;
				}
				s+=i;
				y = true;
			}
			else if (y){
				y = false;
				l = true;
			}
			else{y = false;}
		}
		return s+(v.length()==0?"":("%%%%"+v));//%%%% is a separator
	}
	//extract classes from a JSON list
	public ArrayList<String> extractClasses(String s){
		ArrayList<String> a = new ArrayList<String>();
		int q = 0;
		int i = 0;
		while(q>-1){
			q = s.indexOf("subject\":", q+1);
			i = s.indexOf("catalogNbr\":", i+1);
			String b = s.substring(q+1+"subject\":".length(), s.indexOf(',', q+"subject\":".length()+1)-1);
			String c = s.substring(i+1+"catalogNbr\":".length(), s.indexOf(',', i)-1);
			a.add(b+' '+c);
		}
		ArrayList<String>b = new ArrayList<String>();
		for(String h : a){
			if(h.matches("([A-Z]{2,}\\s\\d\\d\\d\\d.*)")){b.add(h);}
		}
		return b;
	}
	//gets the rawdata from an input string which represents a URL
	public String getData1(String s){
		String v = "";
		if(s.contains("%%%%")){
			int i = s.indexOf("%%%%");
			v = s.substring(i+4);
			s = s.substring(0,i);
		}
		ArrayList<String> dates = new ArrayList<String>();
		String rawdata = "";
		if(v.length()>1){
			String[] strs = v.split(";;;");
			for(String i : strs){
				if(i!=null&&i.length()>2){
					//System.out.println(i);
				sched y = new sched(i);
				rawdata+=y.rawdata;}
			}
			}
//		int month = Calendar.getInstance().get(Calendar.MONTH);
//		String u;
//		if((month<9)){
//			u = "FA";
//		}
//		else u = "SP";
		String classPath = s;
		dates.add("SP17");
		dates.add("FA16");
		dates.add("SP16");
		dates.add("FA15");
		dates.add("SP15");
		boolean a = false;
		try{
			InputStreamReader isr= new InputStreamReader(
					new URL(classPath).openStream());
			BufferedReader br= new BufferedReader(isr);
			String q = "";
			String j = br.readLine();
			while (j != null){
				q += j;
				j = br.readLine();
			}
			rawdata = q;
		}
		catch(Exception e){
//			System.out.println(e.getMessage()+"; ENGRI");
			if(e.getMessage().contains("Server returned HTTP response code: 500")){
				try{
					for(String i: dates){
						if(!a){
							try{InputStreamReader isr= new InputStreamReader(
									new URL(classPath.replace("FA17", i)).openStream());
							BufferedReader br= new BufferedReader(isr);
							String q = "";
							String j = br.readLine();
							while (j != null){
								q += j;
								j = br.readLine();
							}
							rawdata = q;
							a = true;
							}
							catch(Exception g){}
						}
					}
				}
				catch(Exception f){//System.out.println(e.getMessage());
				}
			}
		}
		return rawdata;
	}
	//turns the String list into a course list
	public ArrayList<sched> Courseify(ArrayList<String> a){
		//System.out.println("piedpiper");
		ArrayList<sched> q = new ArrayList<sched>();
		String z="";
		for(String i: a){
			if(i.matches("([A-Z]{2,}\\s\\d\\d\\d\\d)")){
			z = i;
			q.add(new sched(i));}
		}
		//System.out.println(z);
		//System.out.println("diedpiper");
		return q;
	}
	//writes the schedule to a PDF file and saves it in the user's documents folder
	public void writePDF(){
		classes = removeDups(classes);
//		File f = new File(System.getProperty("user.home")+File.separator+"Downloads"+File.separator+"Schedule.pdf");
//		if(f.exists()){
//			f.delete();
//		}
		File f = GUI.alph.toFile();
		PDDocument document = new PDDocument();
		PDPage page = new PDPage();
		document.addPage( page );
		PDFont font = PDType1Font.HELVETICA;
		PDFont bfont = PDType1Font.HELVETICA_BOLD;
		int q = 0;
		try{
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			contentStream.beginText();
			contentStream.newLineAtOffset(25, 725);
			contentStream.setLeading(14.5f);
//			contentStream.setFont(bfont, 25);
//			contentStream.showText("Major: "+major.thisMajor);
//			contentStream.newLine();
			contentStream.setFont(bfont,  15);
			contentStream.showText("Major: "+major.thisMajor+"/Minor: "+minor.thisMinor);
			contentStream.newLine();
			contentStream.newLine();
			for(int i = 0; i<classesPerSem.length; i++){
				contentStream.setFont( bfont, 15 );
				contentStream.showText("Semester " + new Integer(i+1).toString());
				contentStream.newLine();
				contentStream.setFont(font,  12);
				for(int j = 0; j < classesPerSem[i] //5
						; j++){
					contentStream.showText(classes.get(q).name+": "+classes.get(q).title);
					contentStream.newLine();
					q++;
				}
			}
			contentStream.setFont(font, 8);
			contentStream.showText("*Some electives may not be represented on this list");
			contentStream.endText();
//			for(sched v: classes){
//			System.out.println(v.name+"###"+v.isLibArts);}
//			for(String w: major.reqs){
//				System.out.println(w+"@&&%");
//			}
			contentStream.close();
//			document.save(System.getProperty("user.home")+File.separator+"Downloads"+File.separator+"Schedule.pdf");
			document.save(f.getAbsolutePath());
			document.close();
			if(Desktop.isDesktopSupported()){
			Desktop.getDesktop().open(f);}
			Major.p.toFile().delete();
//			System.out.println(major.abbreviate(major.thisMajor));
			Toolkit.getDefaultToolkit().beep();
			System.exit(0);}
		catch(Exception e){
//			System.out.println(e.getMessage());
			}
	}
	//gets the appropriate string from a raw one
	public String getString(String t){
		String name = t;
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
		String courseType = name.substring(name.substring(0,3).equals("CO:")?3:0, n);//cut string if it starts with CO: for corequisite
		int courseNum = Integer.parseInt(name.substring(n+1, n1));
		return courseType + courseNum;
	}
}
