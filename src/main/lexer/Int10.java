package main.lexer;

import main.lexer.Token;
import main.symbols.SymbolTable;

public class Int10 extends Token{
	final int value;
	
	public Int10(int v) {
		super(v+"",Tag.INT10);
		value=v;
	}
	
	public String getValue(){return value+"";}
	
	public String toTerminal(){return tag;}
}
