import java.io.*;
import java.util.*;
import org.eclipse.jdt.core.dom.*;

enum StatementType { METHOD_DECLARATION, CONDITIONAL_STATEMENT, REPEATED_EXECUTION, NEW_TREE }

public class HelloWorld {
	static ArrayList<TreeNode<Activity>> trees = new ArrayList<TreeNode<Activity>>();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		File root = new File(new File(".").getCanonicalPath() + File.separator+"src"+File.separator);
		File[] files = root.listFiles();
		 for (File file : files)
			 if (file.isFile()){
				 String filePath = file.getAbsolutePath();
				 System.out.println("creating a new tree for: " + filePath);
				 trees.add(new TreeNode<Activity>(new Activity(StatementType.NEW_TREE, filePath, 1, null)));
				 parse(readFileToString(filePath), filePath, trees.get(trees.size() - 1));	 
			 }
		 
		 for (TreeNode<Activity> tree: trees) {
			 tree.printNode();
		 }
		/*
		 for (TreeNode<Activity> tree : trees) {
			 Iterator<TreeNode<Activity>> it = tree.iterator();
			 while (it.hasNext()) {
				 TreeNode<Activity> cur = it.next();
				 cur.printNode();
			 }
		 }*/
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
			public boolean visit(MethodDeclaration astNode) {
				Activity activity = new Activity(StatementType.METHOD_DECLARATION, path, cu.getLineNumber(astNode.getStartPosition()), astNode);
				tree.addChild(activity);
				return true;
			}
		});
	}
}

class Activity {
	StatementType statementType;
	String filePath;
	int startLine;
	ASTNode node;
	
	public Activity(StatementType statementType, String filePath, int startLine, ASTNode node) { 
		this.statementType = statementType;
		this.filePath = filePath;
		this.startLine = startLine;
		this.node = node;
	}
	
	public String toString() {
		if (node == null)
			return "There is no data packet associated to this activity. This may be because it is used as a "
					+ "root node for a class.";
		else return "in file " + filePath + ", on line " + startLine + " there is a " + statementType + "\nThe node is:\n" + node;
	}
}


class TreeNode<Activity> implements Iterable<TreeNode<Activity>> {
	public TreeNode<Activity> parentNode = null;
	public Activity astNodePayload;
	public List<TreeNode<Activity>> children;
	
	public TreeNode(Activity astNodePaylaod) {
		System.out.println(astNodePaylaod);
		this.astNodePayload = astNodePaylaod;
		this.children = new ArrayList<TreeNode<Activity>>();
	}
	
	public void printNode() {
		if (this.parentNode != null){
			System.out.println("Parent Node is: " + parentNode.toString());
			System.out.println(astNodePayload);
		}
	}

	public Boolean addChild(Activity astNodePayload) {
		TreeNode<Activity> childNode = new TreeNode<Activity>(astNodePayload);
		childNode.parentNode = this;
		children.add(childNode);
		return true;
	}
	
	public Boolean isRootNode() { if (parentNode == null) return true; else return false; }
	
	//public String toString() { if (astNodePayload != null) return (this.astNodePayload.toString()); else return null;}

	public Iterator<TreeNode<Activity>> iterator() { return new treeIterator();	}
	
	private class treeIterator implements Iterator<TreeNode<Activity>> {
		private List<TreeNode<Activity>> childNodes;
		private int index = 0;
		public treeIterator() { childNodes = TreeNode.this.children; childNodes.add(0, TreeNode.this); }
		@Override
		public boolean hasNext() { if (index < childNodes.size() - 1) return true; else return false; }
		@Override
		public TreeNode<Activity> next() { return childNodes.get(index++); }
	}
}





/*
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
	public Boolean addChild(Activity node) {
        TreeNode<Activity> childNode = new TreeNode<Activity>(node);
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
		return new TreeNodeIterator();
	}
	
	private class TreeNodeIterator implements Iterator<TreeNode<Activity>> {

		private int index;
		
		@Override
		public boolean hasNext() {
			return children.get(children.size()) != null;
		}

		@Override
		public TreeNode<Activity> next() {
			if (this.hasNext()) {
				//TreeNode<Activity> current = index;
				index ++;
				return null;
			}
			return null;
		}
		
	}

    
	
	
	*/
	
	
	
	
	
	
	
    
    /*
	@Override
	public Iterator<TreeNode<Activity>> iterator() {
		Iterator<TreeNode<Activity>> it = new Iterator<TreeNode<Activity>>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return children.get(children.size()) != null;
			}
			
			@Override
			public TreeNode<Activity> next() {
				return children.get(index++);
			}
		};
		return null;
	}*/
//}


//http://www.programcreek.com/2011/11/use-jdt-astparser-to-parse-java-file/
//https://stackoverflow.com/questions/3522454/java-tree-data-structure
//https://stackoverflow.com/questions/5849154/can-we-write-our-own-iterator-in-java
//https://codereview.stackexchange.com/questions/48109/simple-example-of-an-iterable-and-an-iterator-in-java
	