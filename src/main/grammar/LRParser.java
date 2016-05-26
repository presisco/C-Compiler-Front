package main.grammar;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import main.grammar.ActionTable.Action;
import main.lexer.Token;
import main.semantic.GrammarTree;
import main.semantic.GrammarTree.TreeNode;
import main.semantic.Rule.Node;
import main.semantic.Rule;
import main.utils.Log;

public class LRParser {
	public static final String TAG = LRParser.class.getSimpleName();
	public static final String LOGFILE_NAME = "lrparser.log";

	private Stack<Integer> statusStack;
	private Stack<String> symbolStack;
	private List<Token> input;
	private LRSet lrset;
	private BufferedWriter logWriter;

	public LRParser(LRSet _set) throws IOException {
		lrset = _set;
		statusStack = new Stack<>();
		symbolStack = new Stack<>();
	}

	public boolean parse(List<Token> inputTokens) throws IOException {
		input = inputTokens;
		statusStack.clear();
		symbolStack.clear();
		statusStack.add(0);
		symbolStack.add(GrammarTable.END);
		logWriter = Log.getLogFile(LOGFILE_NAME);

		int cursor = 0;
		String terminal = inputTokens.get(cursor++).toTerminal();
		Boolean finished=false;
		
		do {
			Action action = lrset.actionTable.getAction(statusStack.peek(), terminal);
			printLog(action, terminal);
			switch(action.mode){
			case Action.ACTION_SHIFTIN:
				statusStack.push(action.groupid);
				symbolStack.push(terminal);
				if (cursor < inputTokens.size()) {
					terminal = inputTokens.get(cursor++).toTerminal();
				} else {
					terminal = GrammarTable.END;
				}
				break;
			case Action.ACTION_REDUCTION:
				for (int i = 0; i < action.gen.right.length; ++i) {
					statusStack.pop();
					symbolStack.pop();
				}
				statusStack.push(lrset.gotoTable.go2(statusStack.peek(), action.gen.left));
				symbolStack.push(action.gen.left.TAG);
				break;
			case Action.ACTION_FINISHED:
				finished=true;
				break;
			case Action.ACTION_ERROR:
				printError("wrong terminal:" + terminal + "@" + (cursor - 1));
				return false;
			}
		} while (!finished);
		Log.releaseLogFile(logWriter);
		return true;
	}
	
	public boolean parse(List<Token> inputTokens,GrammarTree grammarTree) throws IOException {
		if(grammarTree==null){
			return parse(inputTokens);
		}
		input = inputTokens;
		statusStack.clear();
		symbolStack.clear();
		statusStack.add(0);
		symbolStack.add(GrammarTable.END);
		logWriter = Log.getLogFile(LOGFILE_NAME);
		int cursor = 0;
		String terminal = inputTokens.get(cursor++).toTerminal();
		Boolean finished=false;
		do {
			Action action = lrset.actionTable.getAction(statusStack.peek(), terminal);
			printLog(action, terminal);
			switch(action.mode){
			case Action.ACTION_SHIFTIN:
				statusStack.push(action.groupid);
				
				symbolStack.push(terminal);
				
				grammarTree.pushGenStack(new TreeNode(inputTokens.get(cursor-1)));
				
				if (cursor < inputTokens.size()) {
					terminal = inputTokens.get(cursor++).toTerminal();
				} else {
					terminal = GrammarTable.END;
				}
				break;
			case Action.ACTION_REDUCTION:
				
				for (int i = 0; i < action.gen.right.length; ++i) {
					statusStack.pop();
					symbolStack.pop();
					grammarTree.popGenStack2Workspace();
				}
				statusStack.push(lrset.gotoTable.go2(statusStack.peek(), action.gen.left));
				
				symbolStack.push(action.gen.left.TAG);
				
				grammarTree.addParent4Workspace(action.gen);
				
				break;
			case Action.ACTION_FINISHED:
				grammarTree.popGenStackToRoot();
				finished=true;
				break;
			case Action.ACTION_ERROR:
				printError("wrong terminal:" + terminal + "@" + (cursor - 1));
				return false;
			}
		} while (!finished);
		Log.releaseLogFile(logWriter);
		return true;
	}

	private void printError(String info) {
		String content = "status stack:" + printStatusStackToString();
		content += "\nsymbol stack:" + printSymbolStackToString();
		Log.s(TAG, "error", info + "\nenv:\n" + content);
	}

	private void printLog(Action action, String terminal) throws IOException {
		logWriter.write("Status Stack:" + printStatusStackToString());
		logWriter.newLine();
		logWriter.write("Symbol Stack:" + printSymbolStackToString());
		logWriter.newLine();
		logWriter.write("Action:" + action.toString() + " , terminal:" + terminal);
		logWriter.newLine();
		logWriter.newLine();
		logWriter.write("*******************************");
		logWriter.newLine();
		logWriter.newLine();
		logWriter.flush();
	}

	private String printStatusStackToString() {
		String content = "";
		for (Integer groupid : statusStack) {
			content += groupid + " ";
		}
		return content;
	}

	private String printSymbolStackToString() {
		String content = "";
		for (String symbol : symbolStack) {
			content += symbol + " ";
		}
		return content;
	}
}
