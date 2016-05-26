package main.semantic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import main.grammar.SLRItem;
import main.lexer.Token;
import main.utils.Log;
import main.semantic.Rule.Node;;

public class GrammarTree {
	public static final String TAG=GrammarTree.class.getSimpleName();
	
	public static class TreeNode extends Node{
		public List<TreeNode> childs;
		
		public TreeNode(SLRItem item,List<TreeNode> _childs){
			super(item,_childs);
			childs=new ArrayList<>();
			if(_childs!=null)
				childs.addAll(_childs);
		}
		
		public TreeNode(Token token){
			super(token);
			childs=new ArrayList<>();
		}
		
		public void addChild(TreeNode child){
			childs.add(child);
		}
		
		public List<TreeNode> getAllChilds(){
			return childs;
		}
		
		public String toString(){
			String buff="";
			for(TreeNode child:childs){
				buff+=super.toString()+",";
			}
			return "TreeNode:"+super.toString()+"|childs:"+buff;
		}
	}
	
	TreeNode rootNode;
	Stack<TreeNode> genStack;
	List<TreeNode> workspace;
	
	public GrammarTree(){
		genStack=new Stack<>();
		workspace=new ArrayList<>();
	}
	
	public void pushGenStack(TreeNode treeNode){
		genStack.push(treeNode);
	}
	
	public TreeNode popGenStack(){
		return genStack.pop();
	}
	
	public void popGenStack2Workspace(){
		workspace.add(0,popGenStack());
	}
	
	public void addParent4Workspace(SLRItem item){
		TreeNode newParent= new TreeNode(item,workspace);
		genStack.push(newParent);
		workspace.clear();
	}
	
	public void popGenStackToRoot(){
		rootNode=popGenStack();
	}
	
	public void printGrammarTree(){
		printGrammarTree(rootNode);
	}
	
	public String printGrammarTreeToString(){
		String result=new String();
		printGrammarTreeToString(rootNode,result);
		return result;
	}
	
	public void printGrammarTreeToString(TreeNode parent,String result){
		if(parent==null)
			return;
		for(TreeNode child:parent.childs){
			printGrammarTreeToString(child,result);
		}
		result.concat(parent.tag+" ");
	}
	
	public void printGrammarTree(TreeNode parent){
		if(parent==null)
			return;
		for(TreeNode child:parent.childs){
			printGrammarTree(child);
		}
		Log.s(TAG,"result",parent.tag);
	}
	
	public String getFullcode(){
		return rootNode.getValue("code");
	}
}
