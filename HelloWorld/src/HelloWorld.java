import java.io.*;
import java.util.*;
import org.eclipse.jdt.core.dom.*;

public class HelloWorld {
	
	static ArrayList<TreeNode<Activity>> trees = new ArrayList<TreeNode<Activity>>();
	static Map<Activity, Set<Activity>> methodsCalled = new HashMap<Activity, Set<Activity>>();
	
	public static void main(String[] args) throws IOException {
		File root = new File(new File(".").getCanonicalPath() + File.separator+"src"+File.separator);
		File[] files = root.listFiles();
		 for (File file : files)
			 if (file.isFile()){
				 String filePath = file.getAbsolutePath();
				 trees.add(new TreeNode<Activity>(new Activity(StatementType.NEW_TREE, filePath, 1, null)));
				 parse(readFileToString(filePath), filePath); //, trees.get(trees.size() - 1)	 
			 }

		 //for (TreeNode<Activity> tree : trees)	printTree(tree); //PRINT THE TREE FROM ROOT NODE GOING LEFT TO RIGHT(PARENT BEFORE CHILD)
		/* for (Activity a : methodsCalled.keySet())
			 System.out.println(a.statementType + "\t" + a.astNode + "\n" + methodsCalled.get(a));*/
		 //THIS WILL PRINT OUT ALL THE METHODS. SOME METHODS MAY NOT HAVE ANY VALUES IN THE HASHMAP SO YOU WILL NEED TO CHECK FOR NULL IN FUTURE.

		 removeNullNodesFromTrees();		
		 for (TreeNode<Activity> tree : trees)	printTree(tree);
		 //System.out.println(trees.get(0).getLastNode().getActivity());
	}

	public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) { //-1 == eof
			fileData.append(new String(String.valueOf(buf, 0, numRead)));
			buf = new char[1024];
		}
		reader.close();
		return  fileData.toString();	
	}

	public static void parse(String file, String path) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(file.toCharArray());		
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			
/*			public boolean visit(MethodInvocation astNode) {
				TreeNode<Activity> tree = trees.get(trees.size() - 1);
				Activity activity = new Activity(StatementType.METHOD_INVOCATION, path, cu.getLineNumber(astNode.getStartPosition()), astNode);
				
				MethodDeclaration parentNode = (MethodDeclaration) getDeclaredMethod(astNode);
				TreeNode<Activity> matchedTreeNode = findMatchingNodeInTree(parentNode, tree);
				if (matchedTreeNode == null) tree.addChild(activity); 
				else matchedTreeNode.addChild(activity);
				
				methodsCalled.get(matchedTreeNode.getActivity()).add(activity);
				return true;
			}*/

			public boolean visit(VariableDeclarationFragment node) {
				ASTNode whereTheVariableIsDefined = getDeclaredMethod(node);
				TreeNode<Activity> tree = getCorrectTreeForNode(trees.get(trees.size() - 1), whereTheVariableIsDefined);
				Activity activity = new Activity(StatementType.VARIABLE_DECLARATION, path, cu.getLineNumber(node.getStartPosition()), node);
				if (whereTheVariableIsDefined.getNodeType() == 31)
					findMatchingNodeInTree(whereTheVariableIsDefined, tree).addChild(activity);
				else
					tree.addChild(activity);
				return false; // do not continue to avoid usage info
			}

			public TreeNode<Activity> findMatchingNodeInTree(ASTNode parentNode, TreeNode<Activity> tree) {
				Activity activityInTreeNode = tree.getActivity();
				TreeNode<Activity> oneToReturn = tree;
				
				if (activityInTreeNode.getPayLoad() == null || activityInTreeNode.getPayLoad() != parentNode) {
					for (TreeNode<Activity> tna : tree.getAllChildren())
						if (tna.getActivity().getPayLoad() == parentNode) {
							oneToReturn = tna;
							break;
						}
				}
				
				return oneToReturn;
			}
			
			

			public ASTNode getDeclaredMethod(ASTNode node) {
				ASTNode tempNode = node;
				while (tempNode.getNodeType() != 15) { //15 = file
					if (tempNode.getNodeType() == 31) { //31 = method
						System.out.println(tempNode);
						return tempNode;
					}else if (tempNode.getNodeType() == 55) {//55=class
						return tempNode;
					}
					tempNode = tempNode.getParent();
				}
				if(tempNode.getNodeType() == 15) System.out.println(tempNode);
				System.out.println("==========================");
				return tempNode;
			}

			public boolean visit(MethodDeclaration astNode) {
				TreeNode<Activity> tree = getCorrectTreeForNode(trees.get(trees.size() - 1), astNode);;
				MethodDeclarationActivity activity = new MethodDeclarationActivity(StatementType.METHOD_DECLARATION, path, cu.getLineNumber(astNode.getStartPosition()), astNode);
				tree.addChild(activity);
				methodsCalled.put(activity, new HashSet<Activity>());
				return true;
			}

			private TreeNode<Activity> getCorrectTreeForNode(TreeNode<Activity> thisTree, ASTNode astNode) {
				while (astNode.getNodeType() != 15) {//15 = file
					if (astNode.getNodeType() == 55) //55 = class
						if (astNode != thisTree.getActivity().getPayLoad()) {
							TreeNode<Activity> newTree = new TreeNode<Activity>(new Activity(StatementType.NEW_TREE, path, cu.getLineNumber(astNode.getStartPosition()), astNode));
							trees.add(newTree);
						}
					astNode = astNode.getParent();
				}
				return trees.get(trees.size() - 1);
			}
		});
	}
	
	private static void printTree(TreeNode<Activity> tree) {
		tree.print();
		if (tree.hasChild()) for (TreeNode<Activity> child : tree.getAllChildren()) printTree(child);
	}
	
	
	private static void removeNullNodesFromTrees() {
		 for (Iterator<TreeNode<Activity>> i = trees.iterator(); i.hasNext(); ) 
		        if (i.next().getActivity().getPayLoad() == null) 
		            i.remove();
	}
}



















enum StatementType { METHOD_DECLARATION, CONDITIONAL_STATEMENT, REPEATED_EXECUTION, NEW_TREE, VARIABLE_DECLARATION, METHOD_INVOCATION }


class Activity {
	private StatementType statementType; 
	private String filePath; 
	private int startLine; 
	private ASTNode astNode;
	
	public Activity(StatementType statementType, String filePath, int startLine, ASTNode node) { 
		this.statementType = statementType; this.filePath = filePath; this.startLine = startLine; this.astNode = node;
	}
	
	public String getStatementType() { return statementType.toString(); }
	
	public ASTNode getPayLoad() { return this.astNode; }
	
 	public String toString() {
 		return "In file: " + filePath + "\nOn line: " + startLine + " there is a " + statementType + ". The ast node is:\n" + astNode.toString();
 	}
}


class MethodDeclarationActivity extends Activity {

	List modifiers; Type returnType; String methodName; String params; String methodBody;
	//Unsure as to whether or not modifiers is needed. TODO review modifiers and see if variable can be deleted once plugin is created.
	public MethodDeclarationActivity(StatementType statementType, String filePath, int startLine, ASTNode node) {
		super(statementType, filePath, startLine, node);
		MethodDeclaration n = (MethodDeclaration) node;
		modifiers = n.modifiers(); returnType = n.getReturnType2(); methodName = n.getName().toString(); params = n.parameters().toString(); methodBody = n.getBody().toString();
	}
}


class TreeNode<Activity> {
	private TreeNode<Activity> parentNode = null; 
	private Activity astNodePayload; 
	private List<TreeNode<Activity>> children;
	
	public TreeNode(Activity astNodePaylaod) {
		this.astNodePayload = astNodePaylaod; this.children = new ArrayList<TreeNode<Activity>>();
	}
	
	public Activity getActivity() { return this.astNodePayload; }
	
	public Boolean isRootNode() { if (parentNode == null) return true; else return false; }
	
	public void addChild(Activity astNodePayload) {
		TreeNode<Activity> childNode = new TreeNode<Activity>(astNodePayload);
		childNode.parentNode = this;
		this.children.add(childNode);
	}
	
	public Boolean hasChild() { if (children.size() > 0) return true; else return false; }
	
	public TreeNode<Activity> getChild(int index) { return children.get(index); }
	
	public List<TreeNode<Activity>> getAllChildren() { return children;	}
	
 	public TreeNode<Activity> getLastNode() {
		TreeNode<Activity> lastTreeNode = this;
		while (lastTreeNode.hasChild()) 
			if (lastTreeNode.hasChild()) lastTreeNode = children.get(children.size() - 1);
		return lastTreeNode;
	}	
	
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