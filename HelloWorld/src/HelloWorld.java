import java.io.*;
import java.util.*;
import org.eclipse.jdt.core.dom.*;

public class HelloWorld {
	
	static ArrayList<TreeNode<Activity>> trees = new ArrayList<TreeNode<Activity>>();
	static ArrayList tempa = new ArrayList();
	static ArrayList<String> tempb = new ArrayList();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		File root = new File(new File(".").getCanonicalPath() + File.separator+"src"+File.separator);
		File[] files = root.listFiles();
		 for (File file : files)
			 if (file.isFile()){
				 String filePath = file.getAbsolutePath();
				 trees.add(new TreeNode<Activity>(new Activity(StatementType.NEW_TREE, filePath, 1, null)));
				 parse(readFileToString(filePath), filePath, trees.get(trees.size() - 1));	 
			 }

		 //for (TreeNode<Activity> tree : trees)	printTree(tree); //PRINT THE TREE FROM ROOT NODE GOING LEFT TO RIGHT(PARENT BEFORE CHILD)
		 
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

	public static void parse(String file, String path, TreeNode<Activity> tree) {
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
				MethodDeclaration methodNode = (MethodDeclaration) getDeclaredMethod(astNode);
				Activity activity = new Activity(StatementType.METHOD_INVOCATION, path, cu.getLineNumber(astNode.getStartPosition()), astNode);
				return true;
			}
			
			public ASTNode getDeclaredMethod(ASTNode node) {
				ASTNode tempNode = node;
				while (tempNode.getNodeType() != 15) {
					if (tempNode.getNodeType() == 31)
						return tempNode;
					tempNode = tempNode.getParent();
				}
				return tempNode;
			}
			
			public boolean visit(VariableDeclarationFragment node) {
				ASTNode parentNode = getDeclaredMethod(node);
				MethodDeclaration methodName = null;
				if (parentNode.getNodeType() == 31) methodName = (MethodDeclaration) getDeclaredMethod(node);
				
				Activity activity = new Activity(StatementType.VARIABLE_DECLARATION, path, cu.getLineNumber(node.getStartPosition()), node);
				return false; // do not continue to avoid usage info
			}

			public boolean visit(MethodDeclaration astNode) {
				MethodDeclarationActivity activity = new MethodDeclarationActivity(StatementType.METHOD_DECLARATION, path, cu.getLineNumber(astNode.getStartPosition()), astNode);
				tree.addChild(activity);
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
 	
	
	public TreeNode<Activity> getParentOfVariableDeclaration(VariableDeclaration node, TreeNode treeRoot) {
		System.out.println("$$$$$$" + node + "$$$$$");
		System.out.println(node.getParent().getParent());
		
		return treeRoot;
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
/*		Activity newNode = this.astNodePayload.astNode;
		if (this.parentNode != null) {
			newNode.getStatementType();
				
			}
		}*/
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

/*
for (MethodDeclaration method : visitor.getMethods()) {
String method_Name=method.getName().toString();
      String method_Parameters= method.parameters().toString();
       String method_Body=method.getBody().toString(); // Let me know if you will get strange errors in this type conversion to String because at the moment I do not investigate the reason of error. You will need good
                                                       // volume of testing code to see this error. not just a class 
      String method_Modifier=method.modifiers().toString();
}
*/