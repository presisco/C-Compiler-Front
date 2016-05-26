package main.lexer;

import main.symbols.SymbolTable;

public class Token {
	public final String tag;
	
	public Token(String t){
		tag=t;
	}
	
	public Token(String id,String t){
		this(t);
		SymbolTable.setItem(id, "category", tag);
	}
	public String toString(){return tag;}
	public String getTag(){return tag;}
	public String getValue(){return "";}
	public String toTerminal(){return tag;}
}