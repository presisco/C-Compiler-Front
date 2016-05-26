package main.grammar;

import java.util.Set;

public class ItemGroup {
	public int ID;
	public final Set<SLRItem> items;

	ItemGroup(Set<SLRItem> is) {
		items = is;
	}
}