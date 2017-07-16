import java.io.*;
import java.util.*;
import org.eclipse.jdt.core.dom.*;

public class HelloWorld {
	static LinkedList<Activity> activities = new LinkedList<Activity>();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		File root = new File(new File(".").getCanonicalPath() + File.separator+"src"+File.separator);
		File[] files = root.listFiles();
		 for (File file : files)
			 if (file.isFile())
				 parse(readFileToString(file.getAbsolutePath()));
		 for (Object o : activities)	System.out.println(o.toString());
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

			public boolean visit(IfStatement node) {
				Activity type = new Activity(StatementType.CONDITIONAL_STATEMENT, cu.getLineNumber(node.getStartPosition()));
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(SwitchStatement node) {
				Activity type = new Activity(StatementType.CONDITIONAL_STATEMENT, cu.getLineNumber(node.getStartPosition()));
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(DoStatement node) {
				Activity type = new Activity(StatementType.REPEATED_EXECUTION, cu.getLineNumber(node.getStartPosition()));
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(ForStatement node) {
				Activity type = new Activity(StatementType.REPEATED_EXECUTION, cu.getLineNumber(node.getStartPosition()));
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(WhileStatement node) {
				Activity type = new Activity(StatementType.REPEATED_EXECUTION, cu.getLineNumber(node.getStartPosition()));
				activities.addLast(type);
				return true;
			}
			
			public boolean visit(MethodInvocation node) {
				Activity type = new Activity(StatementType.METHOD_INVOCATION, cu.getLineNumber(node.getStartPosition()));
				activities.addLast(type); 
				return true;
			}
		});
	}
}

enum StatementType { METHOD_INVOCATION, CONDITIONAL_STATEMENT, REPEATED_EXECUTION }

class Activity {
	StatementType statementType;
	int startLine;
	
	public Activity(StatementType statementType, int startLine) { 
		this.statementType = statementType;
		this.startLine = startLine;
	}
	
	public String toString() {
		return "On line " + startLine + " there is a " + statementType;
	}
}

//http://www.programcreek.com/2011/11/use-jdt-astparser-to-parse-java-file/