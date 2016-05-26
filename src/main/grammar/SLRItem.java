package main.grammar;

import main.grammar.GrammarTable.Nonterminal;

//SLR(1)По
public class SLRItem {
	public final Nonterminal left;
	public final String[] right;
	public final int prefix_length;

	public SLRItem(Nonterminal l, String[] r, int pl) {
		left = l;
		right = r;
		prefix_length = pl;
	}
	
	public String toString() {
		String tmp = new String(left.TAG);
		tmp += "->";
		for (int i = 0; i < right.length; ++i) {
			if (i == prefix_length)
				tmp += ".";
			tmp += right[i];
		}
		if (prefix_length == right.length) {
			tmp += ".";
		}
		return tmp;
	}
}