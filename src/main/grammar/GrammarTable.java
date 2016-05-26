package main.grammar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.lexer.Keyword;
import main.lexer.Operator;
import main.lexer.Tag;
import main.lexer.Type;
import main.utils.Log;
import main.utils.Utils;

public class GrammarTable {
	public static final String TAG = GrammarTable.class.getSimpleName();
	public static final String EMPTY = "empty";
	public static final String END = "$";

	public static class Nonterminal {
		public static Map<String, Nonterminal> instances = new HashMap<>();
		public final String TAG;

		Nonterminal(String tag) {
			TAG = tag;
			instances.put(tag, this);
		}

		public boolean hasEmpty() {
			if (getRightSides(this).hasEmpty())
				return true;
			return false;
		}

		public static Nonterminal getInstanceByTag(String tag) {
			if (!instances.containsKey(tag)) {
				return null;
			}
			return instances.get(tag);
		}
	}

	public static class GrammaFomula {
		public Nonterminal left;
		public RightSide right;

		public GrammaFomula(Nonterminal l, RightSide r) {
			left = l;
			right = r;
		}
	}

	public static class RightSide {
		public List<String[]> rightSides = new ArrayList<>();

		public RightSide(String[]... params) {
			for (int i = 0; i < params.length; ++i) {
				rightSides.add(params[i]);
			}
		}

		public RightSide(String[] params) {
			rightSides.add(params);
		}

		public boolean hasEmpty() {
			for (String[] gen : rightSides) {
				if (gen[0] == EMPTY)
					return true;
			}
			return false;
		}

		public String[] get(int pos) {
			return rightSides.get(pos);
		}
	}

	public static class FirstSet extends HashMap<Nonterminal,Set<String>>{}
	
	public static class FollowSet extends HashMap<Nonterminal, Set<String>>{}
	
	public static class Grammars extends HashSet<GrammaFomula>{}
	
	public static final String IGNITION = "ignition";

	public static Grammars grammars;
//	public static Map<Nonterminal, Set<String>> first;
//	public static Map<Nonterminal, Set<String>> follow;

	public static FirstSet first;
	public static FollowSet follow;
	
	public static void cookGramma() {
		grammars = new Grammars();
		first = new FirstSet();
		follow = new FollowSet();
		
		genFullGramma();
//		genTestGramma();
		
		genFirstSet();
		genFollowSet();
	}
	
	public static void genFullGramma(){
		grammars.add(new GrammaFomula(new Nonterminal(IGNITION), new RightSide(new String[] { "P" })));
		// 原语法
		// gramma.add(new GrammaFomula(new Nonterminal("P"), new RightSide(new
		// String[] { "D", "S" })));
		// gramma.add(new GrammaFomula(new Nonterminal("D"),
		// new RightSide(new String[] { "L", Tag.ID, Operator.LNND, "D" }, new
		// String[] { EMPTY })));
		// 去空产生式语法
		grammars.add(
				new GrammaFomula(new Nonterminal("P"), new RightSide(new String[] { "D", "S" }, new String[] { "S" })));
		grammars.add(new GrammaFomula(new Nonterminal("D"), new RightSide(
				new String[] { "L", Tag.ID, Operator.LNND, "D" }, new String[] { "L", Tag.ID, Operator.LNND })));

		grammars.add(new GrammaFomula(new Nonterminal("L"),
				new RightSide(new String[] { Type.INT }, new String[] { Type.FLOAT })));
		grammars.add(new GrammaFomula(new Nonterminal("S"), new RightSide(new String[] { Tag.ID, Operator.ASSIGN, "E", Operator.LNND},
				new String[] { Keyword.IF, Operator.LPAR, "C", Operator.RPAR, Operator.LBPAR, "S", Operator.RBPAR },
				new String[] { Keyword.IF, Operator.LPAR, "C", Operator.RPAR, Operator.LBPAR, "S", Operator.RBPAR,
						Keyword.ELSE, Operator.LBPAR, "S", Operator.RBPAR },
				new String[] { Keyword.WHILE, Operator.LPAR, "C", Operator.RPAR, Operator.LBPAR, "S", Operator.RBPAR },
				new String[] { "S", "S" })));
		grammars.add(new GrammaFomula(new Nonterminal("C"), new RightSide(new String[] { "E", Operator.MORE, "E" },
				new String[] { "E", Operator.LESS, "E" }, new String[] { "E", Operator.EQU, "E" })));
		grammars.add(new GrammaFomula(new Nonterminal("E"), new RightSide(new String[] { "E", Operator.PLUS, "T" },
				new String[] { "E", Operator.MINUS, "T" }, new String[] { "T" })));
		grammars.add(new GrammaFomula(new Nonterminal("T"), new RightSide(new String[] { "F" },
				new String[] { "T", Operator.MULT, "F" }, new String[] { "T", Operator.DIV, "F" })));
		grammars.add(
				new GrammaFomula(new Nonterminal("F"), new RightSide(new String[] { Operator.LPAR, "E", Operator.RPAR },
						new String[] { Tag.ID }, new String[] { Tag.INT10 })));
	}
	
	public static void genTestGramma() {
		// 局部测试
		grammars.add(new GrammaFomula(new Nonterminal(IGNITION), new RightSide(new String[] { "S" })));
		grammars.add(new GrammaFomula(new Nonterminal("S"),
				new RightSide(new String[] { Tag.ID, Operator.ASSIGN, Tag.INT10, Operator.LNND},
						new String[] { Keyword.IF, Operator.LPAR, Tag.INT10, Operator.RPAR, Operator.LBPAR, "S",
								Operator.RBPAR },
				new String[] { Keyword.IF, Operator.LPAR, Tag.INT10, Operator.RPAR, Operator.LBPAR, "S", Operator.RBPAR,
						Keyword.ELSE, Operator.LBPAR, "S", Operator.RBPAR },
				new String[] { Keyword.WHILE, Operator.LPAR, Tag.INT10, Operator.RPAR, Operator.LBPAR, "S",
						Operator.RBPAR }, new String[] { "S", "S" })));
//		grammars.add(new GrammaFomula(new Nonterminal(IGNITION), new RightSide(new String[] { "S" })));
//		grammars.add(new GrammaFomula(new Nonterminal("S"),new RightSide(new String[] { "C", "C" })));
//		grammars.add(new GrammaFomula(new Nonterminal("C"),new RightSide(new String[] { Operator.MULT, "C" },new String[] { Operator.PLUS })));
	}
	
	public static void genFirstSet() {
		for (GrammaFomula ele : grammars) {
			if (!first.containsKey(ele.left)) {
				// Log.s(TAG, "gen firstset for " + ele.left.TAG);
				first.put(ele.left, getFirstSet(ele.left));
			}
		}
	}
	
	public static Set<String> getFirstSet(Nonterminal n) {
		// Log.s(TAG, "getFirstSet()", n.TAG);
		if (first.get(n) != null) {
			return first.get(n);
		}

		Set<String> firstSet = new HashSet<>();
		RightSide rightSide = getRightSides(n);
		for (String[] gen : rightSide.rightSides) {
			Nonterminal firstEle = Nonterminal.getInstanceByTag(gen[0]);
			if (gen[0] == EMPTY) {
				firstSet.add(EMPTY);
			} else if (firstEle != null) {
				boolean lead2empty = firstEle.hasEmpty();
				int i = 0;
				do {
					Nonterminal eleI = Nonterminal.getInstanceByTag(gen[i++]);
					if (eleI != n) {
						Utils.addWithoutEmpty(firstSet, getFirstSet(eleI));
					}
					lead2empty = eleI.hasEmpty();
				} while (i < gen.length && lead2empty);
			} else {
				firstSet.add(gen[0]);
			}
		}

		first.put(n, firstSet);
		return first.get(n);
	}

	public static void genFollowSet() {
		List<String> ig_follow = new ArrayList<>();
		ig_follow.add(END);
		Map<Nonterminal, Set<Nonterminal>> depends = new HashMap<>();
		FollowSet tmp_follow = new FollowSet();
		for (GrammaFomula ele : grammars) {
			tmp_follow.put(ele.left, new HashSet<String>());
			depends.put(ele.left, new HashSet<Nonterminal>());
		}
		tmp_follow.get(Nonterminal.getInstanceByTag(IGNITION)).add(END);
		for (GrammaFomula ele : grammars) {
			calcFollowSetDepends(depends, tmp_follow, ele);
		}
		try {
			Utils.printStringToFile("grammar-follow-depends.txt", printDependSetToString(depends));
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(!depends.isEmpty()){
			Nonterminal root=getRootFromDepends(depends);
			calcFollowSet(depends,tmp_follow,root);
			depends.remove(root);
		}
		depends = null;
		for (Nonterminal non : tmp_follow.keySet()) {
			follow.put(non, tmp_follow.get(non));
		}
		tmp_follow = null;
	}

	public static void calcFollowSetDepends(Map<Nonterminal, Set<Nonterminal>> depends, FollowSet tmp_follow, GrammaFomula fomula) {
		// Log.s(TAG, "getFollowSet()", n.TAG);
		// Map<String, Boolean> followSet = new HashMap<>();
		RightSide rights = fomula.right;
		for (String[] gen : rights.rightSides) {
			Nonterminal ele = null;
			for (int i = 0; i < gen.length; ++i) {
				boolean canBeLast = false;
				ele = Nonterminal.getInstanceByTag(gen[i]);
				if (ele == null)
					continue;
				if (i == gen.length - 1)
					canBeLast = true;
				for (int j = i + 1; j < gen.length; ++j) {
					Nonterminal next = Nonterminal.getInstanceByTag(gen[j]);
					if (next == null) {
						tmp_follow.get(ele).add(gen[i + 1]);
						canBeLast = false;
						break;
					} else {
						Utils.addWithoutEmpty(tmp_follow.get(ele), first.get(next));
						if (!next.hasEmpty()) {
							canBeLast = false;
							break;
						}
					}
				}
				if (canBeLast && fomula.left!=ele) {
					depends.get(fomula.left).add(ele);
				}
			}
		}
	}

	public static void calcFollowSet(Map<Nonterminal, Set<Nonterminal>> depends, FollowSet tmp_follow, Nonterminal root) {
		if(root==null){
			return;
		}
		for (Nonterminal child : depends.get(root)) {
			tmp_follow.get(child).addAll(tmp_follow.get(root));
			calcFollowSet(depends, tmp_follow, child);
		}
	}

	public static Nonterminal getRootFromDepends(Map<Nonterminal,Set<Nonterminal>> depends){
		Nonterminal root=null;
		for(Nonterminal parent:depends.keySet()){
			root=parent;
			break;
		}
		Nonterminal tmp=getParent(depends,root);
		while(tmp!=null){
			root=tmp;
			tmp=getParent(depends,root);
		}
		return root;
	}
	
	public static Nonterminal getParent(Map<Nonterminal,Set<Nonterminal>> depends,Nonterminal child){
		for(Nonterminal parent:depends.keySet()){
			if(depends.get(parent).contains(child)){
				return parent;
			}
		}
		return null;
	}
	
	public static boolean lead2Empty(Nonterminal... nons) {
		for (int i = 0; i < nons.length; ++i) {
			if (!nons[i].hasEmpty())
				return false;
		}
		return true;
	}

	public static RightSide getRightSides(Nonterminal n) {
		for (GrammaFomula ele : grammars) {
			if (ele.left.TAG.equals(n.TAG)) {
				return ele.right;
			}
		}
		return null;
	}

	public static void printGrammaTable() {
		String tmp = "";
		for (GrammaFomula ele : grammars) {
			for (String[] gen : ele.right.rightSides) {
				tmp = "";
				for (int i = 0; i < gen.length; ++i)
					tmp += gen[i];
				Log.s(TAG, "Grammar", ele.left.TAG + "->" + tmp);
			}
		}
	}

	public static String printGrammaTableToString() {
		String tmp = "";
		String result = "";
		for (GrammaFomula ele : grammars) {
			for (String[] gen : ele.right.rightSides) {
				tmp = "";
				for (int i = 0; i < gen.length; ++i)
					tmp += gen[i];
				result += "Grammar:" + ele.left.TAG + "->" + tmp + "\n";
				// Log.s(TAG, "Grammar", ele.left.TAG + "->" + tmp);
			}
		}
		return result;
	}

	public static void printGrammaTableToFile(String filename) throws IOException {
		File outputFile = new File(filename);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		String tmp = "";
		writer.write("gramma table:");
		writer.newLine();
		for (GrammaFomula ele : grammars) {
			for (String[] gen : ele.right.rightSides) {
				tmp = "";
				for (int i = 0; i < gen.length; ++i)
					tmp += gen[i];
				writer.write(ele.left.TAG + "->" + tmp);
				writer.newLine();
			}
		}
		writer.flush();
		writer.close();
	}

	public static void printFirstSet() {
		String buff = "";
		for (Nonterminal ele : first.keySet()) {
			buff = "";
			for (String data : first.get(ele)) {
				buff = buff + data + " | ";
			}
			Log.s(TAG, "first set for " + ele.TAG + ":" + buff);
		}
	}

	public static String printFirstSetToString() {
		String buff = "";
		String result = "";
		for (Nonterminal ele : first.keySet()) {
			buff = "";
			for (String data : first.get(ele)) {
				buff = buff + data + " | ";
			}
			result += "first set for " + ele.TAG + ":" + buff + "\n";
			// Log.s(TAG, "first set for " + ele.TAG + ":" + buff);
		}
		return result;
	}

	public static void printFollowSet() {
		String buff = "";
		for (Nonterminal ele : follow.keySet()) {
			buff = "";
			for (String data : follow.get(ele)) {
				buff = buff + data + " | ";
			}
			Log.s(TAG, "follow set for " + ele.TAG + ":" + buff);
		}
	}

	public static String printFollowSetToString() {
		String buff = "";
		String result = "";
		for (Nonterminal ele : follow.keySet()) {
			buff = "";
			for (String data : follow.get(ele)) {
				buff = buff + data + " | ";
			}
			// Log.s(TAG, "follow set for " + ele.TAG + ":" + buff);
			result += "follow set for " + ele.TAG + ":" + buff + "\n";
		}
		return result;
	}

	public static String printDependSetToString(Map<Nonterminal,Set<Nonterminal>> data) {
		String tmp="";
		String buff="";
		for(Nonterminal parent:data.keySet()){
			tmp=parent.TAG+"->";
			for(Nonterminal child:data.get(parent)){
				tmp+=child.TAG+" | ";
			}
			buff+=tmp+"\n";
		}
		return buff;
	}
}