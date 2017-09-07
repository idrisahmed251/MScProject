import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.eclipse.jdt.core.dom.*;

enum StatementType { METHOD_DECLARATION, CONDITIONAL_STATEMENT, REPEATED_EXECUTION, NEW_TREE, VARIABLE_DECLARATION, METHOD_INVOCATION }


public class Parser {
	
	static ArrayList<TreeNode<Activity>> trees = new ArrayList<TreeNode<Activity>>();
	static Map<Activity, ArrayList<Activity>> methodsInvokedInMethods = new HashMap<Activity, ArrayList<Activity>>();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		File root = new File(new File(".").getCanonicalPath() + File.separator+"src"+File.separator);
		File[] files = root.listFiles();
		 for (File file : files)
			 if (file.isFile()){
				 String filePath = file.getAbsolutePath();
				 trees.add(new TreeNode<Activity>(new Activity(StatementType.NEW_TREE, filePath, 1, null)));
				 parse(readFileToString(filePath), filePath);	 
			 }
		 cleanDataUp();
		 /*printTrees();
		 printMethodsInvokedInMethods();
		 System.out.println(trees.get(0).getActivity().getASTNode());
		 
		 System.out.println("There are " + trees.size() + " trees/classes");
		 System.out.println("There are " + methodsInvokedInMethods.size() + " methods that have method calls");*/
		 }

	private static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			fileData.append(new String(String.valueOf(buf, 0, numRead)));
			buf = new char[1024];
		}
		reader.close();
		return  fileData.toString();	
	}

	private static void parse(String file, String path) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(file.toCharArray());		
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);		
		
		cu.accept(new ASTVisitor() {
			
			public boolean visit(MethodDeclaration astNode) {
				TreeNode<Activity> tree = getRightTreeForASTNode(getLastDefinedTree(), astNode);
				MethodDeclarationActivity activity = new MethodDeclarationActivity(StatementType.METHOD_DECLARATION, path, getLineNumber(astNode), astNode);
				tree.addChild(activity);
				methodsInvokedInMethods.put(activity, new ArrayList<Activity>());
				return true;
			}
			
			public boolean visit(VariableDeclarationFragment astNode) {
				ASTNode whereTheVariableIsDefined = getMethodOrClassNodeBelongsTo(astNode);
				TreeNode<Activity> tree = getRightTreeForASTNode(getLastDefinedTree(), whereTheVariableIsDefined);
				Activity activity = new Activity(StatementType.VARIABLE_DECLARATION, path, getLineNumber(astNode), astNode);
				
				findMatchingMethodInTree(whereTheVariableIsDefined, tree).addChild(activity);
				return false;
			}
			
			public boolean visit(MethodInvocation astNode) {
				TreeNode<Activity> tree = getRightTreeForASTNode(getLastDefinedTree(), astNode);
				Activity activity = new MethodInvocationActivity(StatementType.METHOD_INVOCATION, path, getLineNumber(astNode), astNode);
				MethodDeclaration methodWhichTheInvocationOccurredIn = (MethodDeclaration) getMethodOrClassNodeBelongsTo(astNode);
				
				TreeNode<Activity> matchedTreeNode = findMatchingMethodInTree(methodWhichTheInvocationOccurredIn, tree);
				matchedTreeNode.addChild(activity);
				
				
				methodsInvokedInMethods.get(matchedTreeNode.getActivity()).add(activity);
				return true;
			}

			private TreeNode<Activity> getRightTreeForASTNode(TreeNode<Activity> lastDefinedTree, ASTNode astNode) {
				while (astNode.getNodeType() != 15) {
					if (astNode.getNodeType() == 55)
						if (astNode != lastDefinedTree.getActivity().getASTNode()) {
							TreeNode<Activity> newTree = new TreeNode<Activity>(new Activity(StatementType.NEW_TREE, path, getLineNumber(astNode), astNode));
							trees.add(newTree);
						}
					astNode = astNode.getParent();
				}
				return getLastDefinedTree();
			}
			
			private TreeNode<Activity> getLastDefinedTree() { return trees.get(trees.size() - 1); }
			
			private int getLineNumber(ASTNode astNode) { return cu.getLineNumber(astNode.getStartPosition()); }
			
			private ASTNode getMethodOrClassNodeBelongsTo(ASTNode node) {
				while (node.getNodeType() != 15) {
					if (node.getNodeType() == 31) return node; else if (node.getNodeType() == 55) return node;
					node = node.getParent();
				}
				return null;
			}

			//TODO: currently only works for class methods and class variables. In future, would like it to find any node in any tree.
			private TreeNode<Activity> findMatchingMethodInTree(ASTNode nodeToFind, TreeNode<Activity> tree) {
				ASTNode astInTreeNode = tree.getActivity().getASTNode();
				TreeNode<Activity> matchedNode = tree;
				
				if (astInTreeNode != nodeToFind)
					for (TreeNode<Activity> child : tree.getAllChildren())
						if (child.getActivity().getASTNode() == nodeToFind) matchedNode = child;
				return matchedNode;
			}			
		});
	}
	
	private static void cleanDataUp() {
		 for (Iterator<TreeNode<Activity>> i = trees.iterator(); i.hasNext(); ) 
		        if (i.next().getActivity().getASTNode() == null) 
		            i.remove();
		 for(Iterator<Entry<Activity, ArrayList<Activity>>> j = methodsInvokedInMethods.entrySet().iterator(); j.hasNext(); ) {
			 Entry<Activity, ArrayList<Activity>> entry = j.next();
			 if (entry.getValue().size() == 0)
				 j.remove();
		    }
	}
	
	private static void printTree(TreeNode<Activity> tree) {
		tree.print();
		if (tree.hasChild()) for (TreeNode<Activity> child : tree.getAllChildren()) printTree(child);
	}
	
	private static void printTrees() { for (TreeNode<Activity> tree : trees) printTree(tree); System.out.println("---------------------------------");}

	private static void printMethodsInvokedInMethods() {
		for (Map.Entry<Activity, ArrayList<Activity>> entry : methodsInvokedInMethods.entrySet()) {
		    System.out.println("key:\n" + entry.getKey());
		    System.out.println();
		    System.out.println("value:\n" + entry.getValue());
		    System.out.println("-------------------------------------------------------------------");
		    
		}
	}

}


class Activity {
	private StatementType statementType; 
	private String filePath; 
	private int startLine; 
	private ASTNode astNode;
	
	public Activity(StatementType statementType, String filePath, int startLine, ASTNode node) { 
		this.statementType = statementType; this.filePath = filePath; this.startLine = startLine; this.astNode = node;
	}
	
	public String getStatementType() { return statementType.toString(); }
	
	public ASTNode getASTNode() { return this.astNode; }
	
 	public String toString() {
 		return "In file: " + filePath + "\nOn line: " + startLine + " there is a " + statementType + ". The ast node is:\n" + astNode.toString();
 	}
}


class MethodDeclarationActivity extends Activity {

	//TODO: unsure as to whether or not modifiers is needed. TODO review modifiers and see if variable can be deleted once plugin is created.
	List modifiers; Type returnType; String methodName; List params; String methodBody;
	
	public MethodDeclarationActivity(StatementType statementType, String filePath, int startLine, ASTNode node) {
		super(statementType, filePath, startLine, node);
		MethodDeclaration n = (MethodDeclaration) node;
		modifiers = n.modifiers(); returnType = n.getReturnType2(); methodName = n.getName().toString(); params = n.parameters(); methodBody = n.getBody().toString();
		System.out.println(methodName);
/*		System.out.println(params);
		System.out.println("---------------------------");*/
	}
}

class MethodInvocationActivity extends Activity {
	String methodName; String args;
	
	public MethodInvocationActivity(StatementType statementType, String filePath, int startLine, ASTNode node) {
		super(statementType, filePath, startLine, node);
		MethodInvocation n = (MethodInvocation) node;
		methodName = n.getName().toString();
		args = n.typeArguments().toString();
		
		//System.out.println(methodName);
		//System.out.println(args);
		//System.out.println("--------------------------");
	}
}


//TODO: toString throws StackOverflow area ergo, print was created. find out why and use toString i.e. the standard.
class TreeNode<Activity> {
	private TreeNode<Activity> parentNode = null; 
	private Activity astNodePayload; 
	private List<TreeNode<Activity>> children;
	
	public TreeNode(Activity astNodePaylaod) {
		this.astNodePayload = astNodePaylaod; this.children = new ArrayList<TreeNode<Activity>>();
	}
	
	public Activity getActivity() { return this.astNodePayload; }
	
	public Boolean isRootNode() { if (parentNode == null) return true; else return false; }
	
	public TreeNode<Activity> getRootNode(TreeNode<Activity> treeNode) {
		TreeNode<Activity> tna = treeNode;
		while(tna.isRootNode() == false) tna = tna.getParentNode();
		return tna;
	}
	
	public TreeNode<Activity> getParentNode() { return parentNode; }
	
	public void addChild(Activity astNodePayload) {
		TreeNode<Activity> childNode = new TreeNode<Activity>(astNodePayload);
		childNode.parentNode = this;
		this.children.add(childNode);
	}
	
	public Boolean hasChild() { if (children.size() > 0) return true; else return false; }
	
	public List<TreeNode<Activity>> getAllChildren() { return children;	}
	
	public void print() {
		if (this.isRootNode())
			System.out.println("Node is root node. Memory Address is: " + this + "\n" + astNodePayload.toString());
		else
			System.out.println("Parent node is: " + parentNode + ". This node is: " + this + "\n" + astNodePayload.toString());
	}
}

//http://www.programcreek.com/2011/11/use-jdt-astparser-to-parse-java-file/
//https://stackoverflow.com/questions/3522454/java-tree-data-structure
//http://www.programcreek.com/2011/07/find-all-callers-of-a-method/ GET ALL CALLERS OF A METHOD
/*
 *AST NODE TYPES:
 *15 = file
 *55 = class
 *31 = method
*/
/*
private static int findNumberOfDependencies() {
int numberOfExternalDependencies = 0;
 
	for (TreeNode<Activity> tree : trees)
		for (TreeNode<Activity> child : tree.getAllChildren()) {
			if (child.getActivity().getStatementType() == StatementType.METHOD_DECLARATION.toString());
		}

 for (Map.Entry<Activity, ArrayList<Activity>> entry : methodsInvokedInMethods.entrySet()) {
	 TreeNode<Activity> rootOfKey = getRootNode(findTreeNodeBelongsTo(entry.getKey().getASTNode()));
	 for (Activity a : entry.getValue()){
		 MethodInvocation b = (MethodInvocation) a.getASTNode();
		 System.out.println(b.getName());
		 for (TreeNode<Activity> tree : trees) {
			 for (TreeNode<Activity> child : tree.getAllChildren()) {
				 if (child.getActivity().getStatementType() == StatementType.METHOD_DECLARATION.toString()) {
					 MethodDeclarationActivity c = (MethodDeclarationActivity) child.getActivity();
					 if (c.methodName == b.getName().toString()) {
						 TreeNode<Activity> rootOfValue = getRootNode(findTreeNodeBelongsTo2(a.getASTNode()));
						 if (rootOfKey != rootOfValue)
							 numberOfExternalDependencies ++;
					 }
				 }
			 }
		 }
		 //TreeNode<Activity> rootOfValue = getRootNode(findTreeNodeBelongsTo2(b));
		 //if (rootOfKey != rootOfValue)
			 //numberOfExternalDependencies ++;
	 }
 }


 System.out.println("Number of external class dependencies: " + numberOfExternalDependencies);
 return numberOfExternalDependencies;
}
*/

/*
private static TreeNode<Activity> findTreeNodeBelongsTo2(ASTNode nodeToFind) {
TreeNode<Activity> temp;
for (TreeNode<Activity> tree : trees) {
	for (TreeNode<Activity> child : tree.getAllChildren()) {
		if (child.hasChild()) {
			for (TreeNode<Activity> baby : child.getAllChildren()) {
				if (baby.getActivity().getASTNode() == nodeToFind)
					return baby.getRootNode(baby);
			}
		}
		else
			if (child.getActivity().getASTNode() == nodeToFind) 
				return child.getRootNode(child);
			
	}
}
return null;
}
*/

/*

private static TreeNode<Activity> getRootNode(TreeNode<Activity> treeNode) { return treeNode.getRootNode(treeNode);	}

private static TreeNode<Activity> findTreeNodeBelongsTo(ASTNode nodeToFind) {
	for (TreeNode<Activity> treeOnTrial : trees) 
		for (TreeNode<Activity> child : treeOnTrial.getAllChildren()) 
				if (child.getActivity().getASTNode() == nodeToFind) return child.getRootNode(child);
	return null;
	
}
*/