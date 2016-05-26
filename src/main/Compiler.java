package main;

import java.io.File;
import java.util.Scanner;

import main.grammar.ActionTable;
import main.grammar.GotoTable;
import main.grammar.GrammarTable;
import main.grammar.LR1Set;
import main.grammar.LRParser;
import main.grammar.LRSet;
import main.grammar.SLRSet;
import main.lexer.Lexer;
import main.semantic.GrammarTree;
import main.symbols.SymbolTable;
import main.utils.Log;
import main.utils.Utils;

public class Compiler {
	private static final boolean IS_DEBUG = false;
	private static final String DEBUG_INPUT_FILE_NAME = "src.txt";
	private static final String DEBUG_OUTPUT_GRAMMA_TABLE_FILE_NAME = "gramma-table.txt";

	private static final Boolean USE_LR1=false;
	private static final Boolean USE_SLR=true;
	private static final Boolean GEN_GRAMMAR_TREE=true;

	public Compiler() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			System.out.println("input file name(enter 4 default file):");
			Scanner scanner = new Scanner(System.in);
			String filename;
			File inputFile;
			if (IS_DEBUG) {
				inputFile = new File(DEBUG_INPUT_FILE_NAME);
			} else {
				do {
					filename = scanner.nextLine();
					if (filename.equals("")) {
						filename = "src.txt";
					}
					inputFile = new File(filename);
					if (inputFile.isDirectory() || !inputFile.exists()) {
						System.out.println("wrong file name or not exist:" + filename);
						System.out.println("input file name(enter 4 default file):");
					} else {
						break;
					}
				} while (true);
			}
			scanner.close();
			
			SymbolTable symbolTable=new SymbolTable();
			
			Lexer l = new Lexer(inputFile);
			if(!l.parse())
				return;
			Utils.printStringToFile("lexer-info.txt", l.printTokensToString());
			
			GrammarTable.cookGramma();
			GrammarTable.printGrammaTableToFile("grammar.txt");
			GrammarTable.genFirstSet();
			GrammarTable.genFollowSet();
			Utils.printStringToFile("grammar-info.txt", GrammarTable.printGrammaTableToString(),
					GrammarTable.printFirstSetToString(), GrammarTable.printFollowSetToString());
			
			GrammarTree grammarTree=null;
			if(GEN_GRAMMAR_TREE)
				grammarTree=new GrammarTree();
			
			boolean result=false;
			if(USE_LR1){
				LR1Set lr1Set = new LR1Set(GrammarTable.grammars, GrammarTable.first, GrammarTable.follow);
				lr1Set.genItemGroups();
				Utils.printStringToFile("lr1set-info.txt", lr1Set.printItemGroupsToString(),
						lr1Set.gotoTable.printGotoTableToString(), lr1Set.actionTable.printActionTableToString());
				LRParser lp=new LRParser(lr1Set);
				result=lp.parse(l.getTokens(),grammarTree);
				lp=null;
			}
			
			if(USE_SLR){
				LRSet lrSet = new SLRSet(GrammarTable.grammars, GrammarTable.first, GrammarTable.follow);
				lrSet.genItemGroups();
				Utils.printStringToFile("slrset-info.txt", lrSet.printItemGroupsToString(),
						lrSet.gotoTable.printGotoTableToString(), lrSet.actionTable.printActionTableToString());
				LRParser lp=new LRParser(lrSet);
				result=lp.parse(l.getTokens(),grammarTree);
				lp=null;
			}
			if(!result)
				return;
			
			if(GEN_GRAMMAR_TREE){
				Utils.printStringToFile("grammar-tree.txt", grammarTree.printGrammarTreeToString());
				Utils.printStringToFile("mediate-code.txt", grammarTree.getFullcode());
			}
			
			Utils.printStringToFile("symbol-table.txt", SymbolTable.printSymbolTableToString());
			
		} catch (Exception e) {
			System.out.println("**************************************");
			System.out.println("**************************************");
			e.printStackTrace();
			System.out.println("**************************************");
			System.out.println("**************************************");
		}
	}

}
