package main.grammar;

import java.util.HashMap;
import java.util.Map;

import main.grammar.GrammarTable.Nonterminal;
import main.utils.Log;

public class GotoTable {
	public static final String TAG = GotoTable.class.getSimpleName();

	public Map<Integer, Map<Nonterminal, Integer>> gotoTable;

	public GotoTable() {
		gotoTable = new HashMap<>();
	}

	public void add(int current, Nonterminal non, int next) {
		if (!gotoTable.containsKey(current)) {
			gotoTable.put(current, new HashMap<Nonterminal, Integer>());
		}
		gotoTable.get(current).put(non, next);
	}

	public int go2(int current, Nonterminal non) {
		return gotoTable.get(current).get(non);
	}

	public void printGotoTable() {
		String buff = "";
		for (Integer id : gotoTable.keySet()) {
			buff = "";
			for (Nonterminal non : gotoTable.get(id).keySet()) {
				buff += non.TAG + " " + gotoTable.get(id).get(non) + " | ";
			}
			Log.s(TAG, "Group " + id, buff);
		}
	}

	public String printGotoTableToString() {
		String buff = "";
		String result = TAG + "\n\n";
		for (Integer id : gotoTable.keySet()) {
			buff = "";
			for (Nonterminal non : gotoTable.get(id).keySet()) {
				buff += non.TAG + " " + gotoTable.get(id).get(non) + " | ";
			}
			result += "Group " + id + ":" + buff + "\n";
		}
		return result;
	}
}
