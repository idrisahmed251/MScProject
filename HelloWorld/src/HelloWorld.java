import java.io.*;
import java.util.*;
import org.eclipse.jdt.core.dom.*;
enum StatementType { METHOD_DECLARATION, CONDITIONAL_STATEMENT, REPEATED_EXECUTION, NEW_TREE }
public class HelloWorld {
	static LinkedList<Activity> activities = new LinkedList<Activity>();
	static ArrayList<TreeNode> trees = new ArrayList<TreeNode>();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		File root = new File(new File(".").getCanonicalPath() + File.separator+"src"+File.separator);
		File[] files = root.listFiles();
		 for (File file : files)
			 if (file.isFile()){
				 trees.add(new TreeNode<Activity>(new Activity(StatementType.NEW_TREE, 1, null)));
				 parse(readFileToString(file.getAbsolutePath()));}
		 for (Activity activity : activities)	System.out.println(activity.toString());
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

	public static void parse(String fileToParse) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(fileToParse.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
/*
			public boolean visit(IfStatement node) {
				Activity type = new Activity(StatementType.CONDITIONAL_STATEMENT, cu.getLineNumber(node.getStartPosition()), node);
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(SwitchStatement node) {
				Activity type = new Activity(StatementType.CONDITIONAL_STATEMENT, cu.getLineNumber(node.getStartPosition()), node);
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(DoStatement node) {
				Activity type = new Activity(StatementType.REPEATED_EXECUTION, cu.getLineNumber(node.getStartPosition()), node);
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(ForStatement node) {
				Activity type = new Activity(StatementType.REPEATED_EXECUTION, cu.getLineNumber(node.getStartPosition()), node);
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(WhileStatement node) {
				Activity type = new Activity(StatementType.REPEATED_EXECUTION, cu.getLineNumber(node.getStartPosition()), node);
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(MethodDeclaration node) {
				Activity type = new Activity(StatementType.METHOD_DECLARATION, cu.getLineNumber(node.getStartPosition()), node);
				activities.addLast(type);
				return true;
			}*/
		});
	}
}

class Activity {
	StatementType statementType;
	int startLine;
	ASTNode node;
	
	public Activity(StatementType statementType, int startLine, ASTNode node) { 
		this.statementType = statementType;
		this.startLine = startLine;
		this.node = node;
	}
	
	public String toString() {
		if (node == null)
			return "There is no data packet associated to this activity. This may be because it is used as a "
					+ "root node for a class.";
		else return "On line " + startLine + " there is a " + statementType + "\nThe node is:\n" + node;
	}
}

class TreeNode<Activity> implements Iterable<TreeNode<Activity>> {
    private TreeNode<Activity> parent;
    private Activity nodeData;
    private List<TreeNode<Activity>> children;
    private List<TreeNode<Activity>> callers;

    public TreeNode(Activity data) {
        this.nodeData = data;
        this.children = new LinkedList<TreeNode<Activity>>();
        this.callers = new ArrayList<TreeNode<Activity>>();
    }

    public Boolean addChild(Activity child) {
        TreeNode<Activity> childNode = new TreeNode<Activity>(child);
        childNode.parent = this;
        this.children.add(childNode);
        return true;
    }
    
    public List<TreeNode<Activity>> getAllChildren() { return this.children; }
    
    public List<TreeNode<Activity>> getAllMethodsThatCallThisNode() { return this.callers; }
    
    public Activity getActivityInNode() { return this.nodeData; }
    
    public TreeNode<Activity> getParent() { return this.parent; }
    
    public Boolean isRoot() { if (parent == null) return true; else return false; }

	@Override
	public Iterator<TreeNode<Activity>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}


//http://www.programcreek.com/2011/11/use-jdt-astparser-to-parse-java-file/
//https://stackoverflow.com/questions/3522454/java-tree-data-structure