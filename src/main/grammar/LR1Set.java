package main.grammar;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import main.grammar.GrammarTable.FirstSet;
import main.grammar.GrammarTable.FollowSet;
import main.grammar.GrammarTable.Grammars;
import main.grammar.GrammarTable.Nonterminal;
import main.grammar.LRSet.LRSetInterface;

public class LR1Set extends LRSet implements LRSetInterface {
	public LR1Set(Grammars g, FirstSet fst, FollowSet flw) {
		super(g, fst, flw);
		super.genMove4Item = this;
	}

	public List<LR1Item> instances = new LinkedList<>();

	@Override
	public ItemGroup closure(SLRItem seed) {
		Set<SLRItem> items = new HashSet<>();
		items.add(seed);
		if (seed.prefix_length < seed.right.length) {
			Nonterminal non = Nonterminal.getInstanceByTag(seed.right[seed.prefix_length]);
			if (non != null) {
				closure_itr(items, non, calcEnd4((LR1Item) seed));
			}
		}
		return new ItemGroup(items);
	}

	public void closure_itr(Set<SLRItem> group, Nonterminal seed, Set<String> ends) {
		Set<SLRItem> newitems = new HashSet<>();
		for (String[] right : GrammarTable.getRightSides(seed).rightSides) {
			for (String end : ends) {
				LR1Item newitem=getItemInstance(seed, right, 0, end);
				if(!group.contains(newitem))
					newitems.add(newitem);
			}
		}
		if (newitems.size()==0) {
			return;
		}
		group.addAll(newitems);
		for (SLRItem item : newitems) {
			Nonterminal non = Nonterminal.getInstanceByTag(item.right[0]);
			if (non != null) {
				closure_itr(group, non, calcEnd4((LR1Item) item));
			}
		}
	}

	public Set<String> calcEnd4(LR1Item seed) {
		Set<String> ends;
		if (seed.prefix_length < seed.right.length - 1) {
			Nonterminal rear = Nonterminal.getInstanceByTag(seed.right[seed.prefix_length + 1]);
			if (rear != null) {
				ends = first.get(rear);
			} else {
				ends = new HashSet<>();
				ends.add(seed.right[seed.prefix_length + 1]);
			}
		} else {
			ends = new HashSet<>();
			ends.add(seed.real_end);
		}
		return ends;
	}

	public Set<String> calcEnd4(Nonterminal seed, String[] right, int pl, String end) {
		return calcEnd4(getItemInstance(seed, right, 0, end));
	}

	public LR1Item getItemInstance(Nonterminal l, String[] r, int pl, String end) {
		for (LR1Item instance : instances) {
			if (instance.right == r && instance.left == l && instance.prefix_length == pl && instance.real_end == end) {
				return instance;
			}
		}
		LR1Item newitem = new LR1Item(l, r, pl, end);
		instances.add(newitem);
		return newitem;
	}

	@Override
	public void genReduction4Item(int id, SLRItem item) {
		if(item.left==Nonterminal.getInstanceByTag(GrammarTable.IGNITION))
			actionTable.add(id, GrammarTable.END);
		else
			actionTable.add(id, ((LR1Item) item).real_end, item);
	}

	@Override
	public SLRItem genRootItem() {
		Nonterminal ig = Nonterminal.getInstanceByTag(GrammarTable.IGNITION);
		SLRItem root = new LR1Item(ig, GrammarTable.getRightSides(ig).get(0), 0, GrammarTable.END);
		return root;
	}

	@Override
	public SLRItem step(SLRItem current) {
		return getItemInstance(current.left, current.right, current.prefix_length + 1, ((LR1Item) current).real_end);
	}
}
