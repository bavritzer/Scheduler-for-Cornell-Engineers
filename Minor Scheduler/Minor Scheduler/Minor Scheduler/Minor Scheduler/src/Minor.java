import java.util.ArrayList;

public class Minor {
public static String[] types = {"Computer Science","English", "Operations Research", "Mathematics", "Dyson Business Minor for Engineers", "Engineering Management", "Linguistics", "Electrical and Computer Engineering", "No Minor"};
public String[] requirements = {};
public String[] replacements = {};
//public ArrayList<String> reqts = new ArrayList<String>();
//public ArrayList<String> replts = new ArrayList<String>();
public String thisMinor = "";
public int[] reqType;//the number of required courses and the number total
public ArrayList<sched> courses = new ArrayList<sched>();

Minor(String s){
	if(!this.contains(types, s)){
//		System.out.println("That's not a real minor!");
		return;
	}
	thisMinor = s;
	reqType = getRequirement(this);
}
//checks if a string array contains a string
public boolean contains(String[] t, String s){
	for(String i : t){
		if(i.equals(s)){
			return true;
		}
	}
	return false;
}
public static void orderTypes(){
	ArrayList<String> s = new ArrayList<String>();
	for(String i : types){
		s.add(i);
	}
	s.sort((s1, s2)->s1.charAt(0)-s2.charAt(0));
	for(int i = 0; i<s.size(); i++){
		types[i] = s.get(i);
	}
}

//returns number of specifically required courses and number of total courses
public int[] getRequirement(Minor m){
	int[] y;
	if(m.thisMinor.equals("Computer Science")){
		m.requirements = new String[6];
		m.replacements = new String[6];
		m.requirements[0] = "CS 2110";
		m.requirements[1] = "CS 3410 or CS 3420";
		m.requirements[2] = "CS 3000s, 4000s, or 5000s course/CS 2800";//"subject=CS&classLevels[]=3000&classLevels[]=4000&classLevels[]=5000";
		m.requirements[3] = m.requirements[2].substring(0)+" b";
		m.requirements[4] = m.requirements[2].substring(0)+" c";
		m.requirements[5] = m.requirements[2].substring(0)+" d";
		m.requirements[2]+=" a";
		y= new int[]{1, 1, 4};}//1 hard, 1 medium, 4 soft requirement
	else if(m.thisMinor.equals("English")){
		m.requirements = new String[5];
		m.replacements = new String[5];
		m.requirements[0] = "ENGL 2000s, 3000s, or 4000s course";//"subject=ENGL&classLevels[]=2000&classLevels[]=3000&classLevels[]=4000";
		m.requirements[1] = m.requirements[0].substring(0)+" b";
		m.requirements[2] = m.requirements[0].substring(0)+" c";
		m.requirements[3] = m.requirements[0].substring(0)+" d";
		m.requirements[4] = m.requirements[0].substring(0)+" e";
		m.requirements[0]+=" a";
		y= new int[]{0, 0, 5};}//5 soft requirements
	else if(m.thisMinor.equals("Operations Research")){
		m.requirements = new String[6];
		m.replacements = new String[6];
		m.requirements[0] = "ENGRD 2700 or ORIE 3300 or ORIE 3310 or ORIE 3500 or ORIE 3510 or ORIE 4580";
		m.requirements[1] = "ENGRD 2700 or ORIE 3300 or ORIE 3310 or ORIE 3500 or ORIE 3510 or ORIE 4580";
		m.requirements[2] = "ENGRD 2700 or ORIE 3300 or ORIE 3310 or ORIE 3500 or ORIE 3510 or ORIE 4580";
		m.requirements[3] = "ORIE 3000s, 4000s, or 5000s course";
		m.requirements[4] = m.requirements[3].substring(0)+" b";
		m.requirements[5] = m.requirements[3].substring(0)+" c";
		m.requirements[3]+=" a";
		m.replacements[0] = "ORIE core course a";
		m.replacements[1] = "ORIE core course b";
		m.replacements[2] = "ORIE core course c";
		y= new int[]{0, 3, 3};}//3 med and 3 soft reqs
	else if(m.thisMinor.equals("Mathematics")){
		m.requirements = new String[6];
		m.replacements = new String[6];
		m.requirements[0] = "MATH 1920";
		m.requirements[1] = "MATH 2940";
		m.requirements[2] = "MATH 3320 or MATH 3340 or MATH 3360 or MATH 4310 or MATH 4315 or MATH 4330 or MATH 4340 or MATH 4370 or MATH 4500";
		m.requirements[3] = "MATH 3110 or MATH 3210 or MATH 3230 or MATH 4130 or MATH 4140 or MATH 4180 or MATH 4200 or MATH 4210 or MATH 4220 or MATH 4240 or MATH 4250 or MATH 4260 or MATH 4280";
		m.requirements[4] = "MATH 3000s or 4000s course";
		m.requirements[5] = "MATH 4000s course";
//		m.requirements[4]+=" a";
		m.replacements[2] = "Advanced Algebra";
		m.replacements[3] = "Mathematical Analysis";
		y= new int[]{2, 2, 2};}
	else if(m.thisMinor.equals("Dyson Business Minor for Engineers")){
		m.requirements = new String[6];
		m.replacements = new String[6];
		m.requirements[0] = "ECON 1110";
		m.requirements[1] = "AEM 4460";
		m.requirements[2] = "AEM 1200 or AEM 2400 or ENGRI 1270  or NCC 5580 or ORIE 4152 or HADM 2410 or ILRID 1700 or NCC 5530";
		m.requirements[3] = "AEM 2210 or ORIE 3150 or NCC 5500 or HADM 2230";
		m.requirements[4] = "AEM 2241 or  HADM 2250  or  NCC 5560";
		m.requirements[5] = "AEM 3100 or AEM 3200 or AEM 3210 or AEM 3220 or AEM 3230 or AEM 3249 or AEM 3250 or AEM 3280 or AEM 3310 or AEM 3350 or AEM 3360 or AEM 3370 or AEM 3380 or AEM 3430 or AEM 3440 or AEM 3550 or AEM 4020 or AEM 4060 or AEM 4070 or AEM 4120 or AEM 4140 or AEM 4150 or AEM 4160 or AEM 4170 or AEM 4190 or AEM 4210 or AEM 4230 or AEM 4260 or AEM 4280 or AEM 4290 or AEM 4300 or AEM 4370 or AEM 4400 or AEM 4421 or AEM 4450 or AEM 4520 or AEM 4550 or AEM 4560 or AEM 4570 or AEM 4580 or AEM 4590 or AEM 4650 or AEM 4670 or BEE 4890";
		m.replacements[2] = "Basic Business Concepts";
		m.replacements[3] = "Accounting Principles";
		m.replacements[4] = "Finance";
		m.replacements[5] = "Business Management to Support Career Goals";
		y= new int[]{2, 4, 0};}
	else if(m.thisMinor.equals("Linguistics")){
		m.requirements = new String[5];
		m.replacements = new String[5];
		m.requirements[0] = "LING 1101";
		m.requirements[1] = "LING 3302 or LING 3303 or LING 3314";
		m.requirements[2] = "LING 3000s or 4000s course";
		m.requirements[3] = "LING 1000s, 2000s, 3000s, or 4000s course";
		m.requirements[4] = "LING 2000s, 3000s, or 4000s course";
		m.replacements[1] = "Foundational Linguistics";
		y= new int[]{1, 1, 3};}
	else if(m.thisMinor.equals("Engineering Management")){
		m.requirements = new String[6];
		m.replacements = new String[6];
		m.requirements[0] = "ORIE 3150";
		m.requirements[1] = "CEE 3230 or ORIE 4150";
		m.requirements[2] = "CEE 3040 or ENGRD 2700 or ECE 3100";
		m.requirements[3] = "CEE 5930 or CEE 5950 or CEE 5970 or CEE 5980 or ENGRG 3600 or NBA 5070 or MAE 4610 or BEE 4890";
		m.requirements[4] = "CEE 5930 or CEE 5950 or CEE 5970 or CEE 5980 or ENGRG 3600 or NBA 5070 or MAE 4610 or BEE 4890";
		m.requirements[5] = "CEE 5930 or CEE 5950 or CEE 5970 or CEE 5980 or ENGRG 3600 or NBA 5070 or MAE 4610 or BEE 4890";
		m.replacements[1] = "Engineering Economics";
		m.replacements[2] = "Engineering Statistics";
		m.replacements[3] = "Additional Management Course a";
		m.replacements[4] = "Additional Management Course b";
		m.replacements[5] = "Additional Management Course c";
		y= new int[]{1, 5, 0};}
	else if(m.thisMinor.equals("Electrical and Computer Engineering")){
		m.requirements = new String[6];
		m.replacements = new String[6];
		m.requirements[0] = "ECE 2100 or ECE 2200 or ECE 2300";
		m.requirements[1] = "ECE 2100 or ECE 2200 or ECE 2300";
		m.requirements[2] = "ECE 3030 or ECE 3100 or ECE 3140 or ECE 3150 or ECE 3250";
		m.requirements[3] = "ECE 3030 or ECE 3100 or ECE 3140 or ECE 3150 or ECE 3250";
		m.requirements[4] = "ECE 3000s, 4000s, or 5000s course";
		m.requirements[5] = "ECE 4000s or 5000s course";
		m.replacements[0] = "Intro ECE course a";
		m.replacements[1] = "Intro ECE course b";
		m.replacements[2] = "ECE Core Course a";
		m.replacements[3] = "ECE Core Course b";
		y= new int[]{0, 4, 2};}
	else if(m.thisMinor.equals("No Minor")){
		m.requirements = new String[0];
		m.replacements = new String[0];
		y= new int[]{0, 0, 0};}
	else y= new int[]{};
//	for(String i : m.requirements){
//		m.reqts.add(i);
//	}
//	for(String i : m.replacements){
//		m.replts.add(i);
//	}
return y;}
}
