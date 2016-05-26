package main.lexer;

import main.symbols.SymbolTable;

public class Id extends Token {
	final String name;

	public Id(String v) {
		super(v,Tag.ID);
		name=v;
	}
	
	public String getValue(){return name;}
	
	public String toTerminal(){return tag;}
}
