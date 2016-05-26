package main.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.grammar.GrammarTable.FirstSet;
import main.grammar.GrammarTable.FollowSet;
import main.grammar.GrammarTable.Grammars;
import main.grammar.GrammarTable.Nonterminal;
import main.utils.Log;

public class LRSet {
	public String TAG=LRSet.class.getSimpleName();
	
	public static interface LRSetInterface{
		SLRItem genRootItem();
		void genReduction4Item(int id,SLRItem item);
		//传入移动后的item
		ItemGroup closure(SLRItem seed);
		SLRItem step(SLRItem current);
	}
	
	Grammars grammar;
	FirstSet first;
	FollowSet follow;
	LRSetInterface genMove4Item;

	public List<ItemGroup> itemGroups;
	public Set<Integer> finishedGroups;
	public ActionTable actionTable;
	public GotoTable gotoTable;
	
	LRSet(Grammars g, FirstSet fst, FollowSet flw){
		grammar = g;
		first = fst;
		follow = flw;
		itemGroups = new ArrayList<>();
		finishedGroups = new HashSet<>();
		actionTable = new ActionTable();
		gotoTable = new GotoTable();
	}
	
	public void genItemGroups() {
		addGroup(genMove4Item.closure(genMove4Item.genRootItem()));
		genItemGroups(0);
	}
	
	public void genItemGroups(int id) {
		if (finishedGroups.contains(id))
			return;
		ItemGroup tmpGroup = null;
		Map<String, Set<SLRItem>> edgeNgroup = new HashMap<>();
		List<Integer> queue = new LinkedList<>();
		for (SLRItem item : itemGroups.get(id).items) {
			if (item.prefix_length == item.right.length) {
				// 添加规约操作
				genMove4Item.genReduction4Item(id, item);
			} else {
				String edge = item.right[item.prefix_length];
				
				tmpGroup = genMove4Item.closure(genMove4Item.step(item));
				
				if (edgeNgroup.get(edge) != null) {
					edgeNgroup.get(edge).addAll(tmpGroup.items);
				} else {
					edgeNgroup.put(edge, tmpGroup.items);
				}
			}
		}
		for (String edge : edgeNgroup.keySet()) {
			Nonterminal non = Nonterminal.getInstanceByTag(edge);
			tmpGroup = new ItemGroup(edgeNgroup.get(edge));
			int nextid = addGroup(tmpGroup);
			if (non == null) {
				// 添加移入操作
				actionTable.add(id, edge, nextid);
			} else {
				// 添加状态转移
				gotoTable.add(id, non, nextid);
			}
			queue.add(nextid);
		}
		finishedGroups.add(id);
		for (Integer nextid : queue) {
			genItemGroups(nextid);
		}
		queue = null;
		edgeNgroup = null;
	}
	
	public int addGroup(ItemGroup newGroup) {
		for (ItemGroup group : itemGroups) {
			if(group.items.containsAll(newGroup.items))
				return group.ID;
		}
		itemGroups.add(newGroup);
		newGroup.ID = itemGroups.size() - 1;
		return newGroup.ID;
	}
	
	public void printItemGroups() {
		for (ItemGroup group : itemGroups) {
			for (SLRItem item : group.items) {
				Log.s(TAG, "Group " + group.ID, item.toString());
			}
		}
	}
	
	public String printItemGroupsToString(){
		String buff=TAG+"\n\n";
		String tmp="";
		for (ItemGroup group : itemGroups) {
			tmp="Group " + group.ID + ":";
			for (SLRItem item : group.items) {
				tmp+=item.toString() + " | ";
			}
			buff+=tmp+"\n";
		}
		return buff;
	}
}
