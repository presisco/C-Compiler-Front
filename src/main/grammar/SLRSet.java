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

public class SLRSet extends LRSet implements LRSetInterface {

	public SLRSet(Grammars g, FirstSet fst, FollowSet flw) {
		super(g, fst, flw);
		super.genMove4Item = this;
	}

	public static List<SLRItem> instances = new LinkedList<>();

	public static SLRItem getItemInstance(Nonterminal l, String[] r, int pl) {
		for (SLRItem instance : instances) {
			if (instance.right == r && instance.left == l && instance.prefix_length == pl) {
				return instance;
			}
		}
		SLRItem newitem = new SLRItem(l, r, pl);
		instances.add(newitem);
		return newitem;
	}

	@Override
	public ItemGroup closure(SLRItem seed) {
		Set<SLRItem> items = new HashSet<>();
		items.add(seed);
		if (seed.prefix_length < seed.right.length) {
			Nonterminal non = Nonterminal.getInstanceByTag(seed.right[seed.prefix_length]);
			if (non != null) {
				items.addAll(closure_itr(non));
			}
		}
		return new ItemGroup(items);
	}

	public static Set<SLRItem> closure_itr(Nonterminal seed) {
		Set<SLRItem> items = new HashSet<>();
		for (String[] right : GrammarTable.getRightSides(seed).rightSides) {
			items.add(getItemInstance(seed, right, 0));
			Nonterminal non = Nonterminal.getInstanceByTag(right[0]);
			if (right[0] != seed.TAG && non != null) {
				items.addAll(closure_itr(non));
			}
		}
		return items;
	}

	@Override
	public void genReduction4Item(int id, SLRItem item) {
		if (item.left == Nonterminal.getInstanceByTag(GrammarTable.IGNITION)) {
			actionTable.add(id, GrammarTable.END);
		} else {
			for (String flw : GrammarTable.follow.get(item.left))
				actionTable.add(id, flw, item);
		}
	}

	@Override
	public SLRItem genRootItem() {
		Nonterminal ig = Nonterminal.getInstanceByTag(GrammarTable.IGNITION);
		SLRItem root = new SLRItem(ig, GrammarTable.getRightSides(ig).get(0), 0);
		return root;
	}

	@Override
	public SLRItem step(SLRItem current) {
		return getItemInstance(current.left, current.right, current.prefix_length + 1);
	}
}
