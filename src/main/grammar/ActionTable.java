package main.grammar;

import java.util.HashMap;
import java.util.Map;

import main.utils.Log;

public class ActionTable {
	public static final String TAG = ActionTable.class.getSimpleName();

	public static class Action {
		public static final String ACTION_REDUCTION = "reduction";
		public static final String ACTION_SHIFTIN = "shiftin";
		public static final String ACTION_FINISHED="finished";
		public static final String ACTION_ERROR="error";

		public final String mode;
		public final int groupid;
		public final SLRItem gen;
		
		Action(String m){
			this(m,-1);
		}

		Action(String m, int id) {
			mode = m;
			groupid = id;
			gen = null;
		}

		Action(String m, SLRItem g) {
			mode = m;
			groupid = -1;
			gen = g;
		}

		public String toString() {
			String tmp = "";
			switch(mode){
			case ACTION_REDUCTION:
				tmp += "r";
				tmp += "(" + gen.toString() + ")";
				break;
			case ACTION_SHIFTIN:
				tmp += "s";
				tmp += groupid;
				break;
			default:
				tmp += mode;
				break;
			}
			return tmp;
		}
	}

	Map<Integer, Map<String, Action>> actionTable;

	public ActionTable() {
		actionTable = new HashMap<>();
	}

	public void add(int id, String terminal, int nextid) {
		Action newAction = new Action(Action.ACTION_SHIFTIN, nextid);
		add(id, terminal, newAction);
	}

	public void add(int id, String terminal, SLRItem gen) {
		Action newAction = new Action(Action.ACTION_REDUCTION, gen);
		add(id, terminal, newAction);
	}
	
	public void add(int id, String terminal){
		add(id,terminal,new Action(Action.ACTION_FINISHED));
	}

	public void add(int id, String terminal, Action newAction) {
		if (!actionTable.containsKey(id)) {
			actionTable.put(id, new HashMap<String, Action>());
		}
		if (actionTable.get(id).containsKey(terminal)) {
			printConflict(id, terminal, actionTable.get(id).get(terminal), newAction);
		}
		actionTable.get(id).put(terminal, newAction);
	}

	public Action getAction(int id, String terminal) {
		Action action=actionTable.get(id).get(terminal);
		if(action==null)
			action=new Action(Action.ACTION_ERROR);
		return action;
	}

	public void printActionTable() {
		String buff = "";
		for (Integer id : actionTable.keySet()) {
			buff = "";
			for (String terminal : actionTable.get(id).keySet()) {
				buff += terminal + " " + actionTable.get(id).get(terminal).toString() + " | ";
			}
			Log.s(TAG, "Group " + id, buff);
		}
	}

	public String printActionTableToString() {
		String buff = "";
		String result = TAG + "\n\n";
		for (Integer id : actionTable.keySet()) {
			buff = "";
			for (String terminal : actionTable.get(id).keySet()) {
				buff += "'" + terminal + "' " + actionTable.get(id).get(terminal).toString() + " | ";
			}
			result += "Group " + id + ":" + buff + "\n";
		}
		return result;
	}

	public void printConflict(int id, String terminal, Action original, Action newAction) {
		Log.s(TAG, "Conflict@Group " + id + " with terminal " + terminal,
				"orignial " + original.toString() + " new " + newAction.toString());
	}
}
