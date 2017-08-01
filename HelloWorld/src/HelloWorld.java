import java.io.*;
import java.util.*;
import org.eclipse.jdt.core.dom.*;

public class HelloWorld {
	
	static ArrayList<TreeNode<Activity>> trees = new ArrayList<TreeNode<Activity>>();
	static Map<Activity, Set<Activity>> methodsCalled = new HashMap<Activity, Set<Activity>>();
	
	public static void main(String[] args) throws IOException, InterruptedException {
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
		 /*for (TreeNode<Activity> tna : trees)
			 if (tna.astNodePayload.astNode == null)
				 trees.remove(tna);
		 System.out.println(trees.size());*/
		 
		 for (Iterator<TreeNode<Activity>> i = trees.iterator(); i.hasNext(); ) {
		        if (i.next().astNodePayload.astNode == null) {
		            i.remove();
		            
		        } 
		    }
		 for (TreeNode<Activity> tna : trees){
			 System.out.println(tna.children.size());
			 System.out.println("-----------------------------");
		 }
		// System.out.println(trees.size());

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
		parser.setProject(null);
		
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			
/*
			public boolean visit(IfStatement node) {
				Activity type = new Activity(StatementType.CONDITIONAL_STATEMENT, filePath, cu.getLineNumber(node.getStartPosition()), node);
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(SwitchStatement node) {
				Activity type = new Activity(StatementType.CONDITIONAL_STATEMENT, filePath, cu.getLineNumber(node.getStartPosition()), node);
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(DoStatement node) {
				Activity type = new Activity(StatementType.REPEATED_EXECUTION, filePath, cu.getLineNumber(node.getStartPosition()), node);
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(ForStatement node) {
				Activity type = new Activity(StatementType.REPEATED_EXECUTION, filePath, cu.getLineNumber(node.getStartPosition()), node);
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(WhileStatement node) {
				Activity type = new Activity(StatementType.REPEATED_EXECUTION, filePath, cu.getLineNumber(node.getStartPosition()), node);
				activities.addLast(type);
				return true;
			}
			*/
			
			public boolean visit(MethodInvocation astNode) {
				TreeNode<Activity> tree = trees.get(trees.size() - 1);
				MethodDeclaration parentNode = (MethodDeclaration) getDeclaredMethod(astNode);
				Activity activity = new Activity(StatementType.METHOD_INVOCATION, path, cu.getLineNumber(astNode.getStartPosition()), astNode);
				TreeNode<Activity> nodeInTree = findMatchingNodeInTree(parentNode, tree);
				if (nodeInTree == null) tree.addChild(activity); 
				else nodeInTree.addChild(activity);
				
				methodsCalled.get(nodeInTree.astNodePayload).add(activity);
				return true;
			}

			public boolean visit(VariableDeclarationFragment node) {
				TreeNode<Activity> tree = trees.get(trees.size() - 1);
				ASTNode parentNode = getDeclaredMethod(node);
				if (parentNode.getNodeType() == 31)
					parentNode = (MethodDeclaration) getDeclaredMethod(node);
				Activity activity = new Activity(StatementType.VARIABLE_DECLARATION, path, cu.getLineNumber(node.getStartPosition()), node);
				
				TreeNode<Activity> nodeInTree = findMatchingNodeInTree(parentNode, tree);
				if (nodeInTree == null) tree.addChild(activity); else nodeInTree.addChild(activity);
				return false; // do not continue to avoid usage info
			}

			public TreeNode<Activity> findMatchingNodeInTree(ASTNode parentNode, TreeNode<Activity> tree) {
				Activity activityInTreeNode = tree.astNodePayload;
				TreeNode<Activity> oneToReturn = tree;
				
				if (activityInTreeNode.astNode == null || activityInTreeNode.astNode != parentNode) {
					for (TreeNode<Activity> tna : tree.children)
						if (tna.astNodePayload.astNode == parentNode) {
							oneToReturn = tna;
							break;
						}
				}
				
				return oneToReturn;
			}
			
			

			public ASTNode getDeclaredMethod(ASTNode node) {
				ASTNode tempNode = node;
				while (tempNode.getNodeType() != 15) {
					if (tempNode.getNodeType() == 31) {
						return tempNode;
					}
					tempNode = tempNode.getParent();
				}
				return tempNode;
			}

			public boolean visit(MethodDeclaration astNode) {
				TreeNode<Activity> tree = trees.get(trees.size() - 1);
				MethodDeclarationActivity activity = new MethodDeclarationActivity(StatementType.METHOD_DECLARATION, path, cu.getLineNumber(astNode.getStartPosition()), astNode);
				
				ASTNode temp = astNode;
				while(temp.getNodeType() != 15) {
					//System.out.println(temp.getNodeType());
					if (temp.getNodeType() == 55) {
						if (temp != tree.astNodePayload.astNode) {
							//System.out.println("---------------------" + tree.astNodePayload.astNode);
							TreeNode<Activity> newNode = new TreeNode<Activity>(new Activity(StatementType.NEW_TREE, path, cu.getLineNumber(temp.getStartPosition()), temp));
							trees.add(newNode);
							tree = trees.get(trees.size() - 1);
							//System.out.println("found one");
						}
					}
					temp = temp.getParent();
				
				}
				//System.out.println("_______________________");
				
				
				
				
				tree.addChild(activity);
				methodsCalled.put(activity, new HashSet<Activity>());
				/*ASTNode temp = astNode;
				//System.out.println("temp node type:" + temp.getNodeType());
				while (temp.getParent() != null){
					if (temp.getNodeType() == 55) 
						System.out.println(temp);
					System.out.println(temp.getNodeType());
					temp = temp.getParent();
				}
				System.out.println("_________________");*/
					return true;
			}
		});
	}
	
	private static void printTree(TreeNode<Activity> tree) {
		tree.printNode();
		if (tree.hasChild()) for (TreeNode<Activity> child : tree.children) printTree(child);
	}
}


enum StatementType { METHOD_DECLARATION, CONDITIONAL_STATEMENT, REPEATED_EXECUTION, NEW_TREE, VARIABLE_DECLARATION, METHOD_INVOCATION }

class MethodDeclarationActivity extends Activity {

	List modifiers; Type returnType; String methodName; String params; String methodBody; List methodVariables; 
	
	public MethodDeclarationActivity(StatementType statementType, String filePath, int startLine, ASTNode node) {
		super(statementType, filePath, startLine, node);
		MethodDeclaration n = (MethodDeclaration) node;
		modifiers = n.modifiers();
		returnType = n.getReturnType2();
		methodName = n.getName().toString();
		params = n.parameters().toString();
		methodBody = n.getBody().toString();
		//print();
	}
	
	public void addMethodVariable(String variable) {
		System.out.println(variable);
		methodVariables.add(variable);
	}
	
	public void print() {
		System.out.println(modifiers + " " + returnType + " " + methodName + " " + params);
		System.out.println(methodBody);
		System.out.println(methodVariables);
		System.out.println("---------------------------------------------------");
	}
}

class Activity {
	public StatementType statementType; 
	public String filePath; 
	public int startLine; 
	public ASTNode astNode;
	
	public Activity(StatementType statementType, String filePath, int startLine, ASTNode node) { 
		this.statementType = statementType;
		this.filePath = filePath;
		this.startLine = startLine;
		this.astNode = node;
	}
	
	public String getStatementType() { return statementType.toString(); }
	
	public ASTNode getPayLoad() { return this.astNode; }
	
 	public String toString() {
		if (astNode == null)
			return "There is no data packet associated to this activity. This may be because it is used as a "
					+ "root node for a class.";
		else return "in file " + filePath + ", on line " + startLine + " there is a " + statementType + "\nThe node is:\n" + astNode;
	}
}


class TreeNode<Activity> {
	public TreeNode<Activity> parentNode = null; 
	public Activity astNodePayload; 
	public List<TreeNode<Activity>> children;
	
	public TreeNode(Activity astNodePaylaod) {
		this.astNodePayload = astNodePaylaod;
		this.children = new ArrayList<TreeNode<Activity>>();
	}
	
	public TreeNode<Activity> getLastNode(TreeNode<Activity> treeNode) {
		/*if (this.hasChild()) {
			getLastNode(children.get(children.size() - 1));
			//return children.get(children.size() - 1).astNodePayload;
		}
		//else return astNodePayload;
		return treeNode.astNodePayload;*/
		
		TreeNode<Activity> tna = treeNode;
		if (tna.hasChild()) {
			while (tna.hasChild()) {
				if (tna.hasChild()) {
					tna = children.get(children.size() - 1);
				}
			}
		}
		return tna;
	}
	

	public Boolean hasChild() { if (children.size() > 0) return true; else return false; }
	
	public void printNode() {
		if (this.parentNode != null){
			System.out.println("Parent Node is: " + parentNode);
			System.out.println(astNodePayload.toString());
		}
		else
			System.out.println(astNodePayload.toString());
	}

	public Boolean addChild(Activity astNodePayload) {
		TreeNode<Activity> childNode = new TreeNode<Activity>(astNodePayload);
		childNode.parentNode = this;
		this.children.add(childNode);
		return true;
	}
	
	public Boolean isRootNode() { if (parentNode == null) return true; else return false; }
}

//http://www.programcreek.com/2011/11/use-jdt-astparser-to-parse-java-file/
//https://stackoverflow.com/questions/3522454/java-tree-data-structure
//http://www.programcreek.com/2011/07/find-all-callers-of-a-method/ GET ALL CALLERS OF A METHOD