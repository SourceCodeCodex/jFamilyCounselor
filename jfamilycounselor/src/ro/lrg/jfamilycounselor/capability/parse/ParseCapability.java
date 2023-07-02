package ro.lrg.jfamilycounselor.capability.parse;

import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Capability that builds the AST objects corresponding to different JDT
 * elements.
 * 
 * @author rosualinpetru
 *
 */
public class ParseCapability {
	private ParseCapability() {
	}

	private static final Cache<ICompilationUnit, CompilationUnit> cache = MonitoredUnboundedCache
			.getHighConsumingCache();

	private static final Logger logger = jFCLogger.getLogger();

	public static Optional<MethodDeclaration> parse(IMethod iMethod) {
		return parse(iMethod.getCompilationUnit()).flatMap(cuAST -> {
			var visitor = new MethodDeclarationVisitor(iMethod);
			cuAST.accept(visitor);
			return visitor.getLastNode();
		});
	}

	public static Optional<CompilationUnit> parse(ICompilationUnit iCompilationUnit) {
		if (cache.contains(iCompilationUnit)) {
			return cache.get(iCompilationUnit);
		}

		var parser = ASTParser.newParser(AST.getJLSLatest());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setSource(iCompilationUnit);
		parser.setProject(iCompilationUnit.getJavaProject());

		Optional<CompilationUnit> compUnit = Optional
				.ofNullable((CompilationUnit) parser.createAST(new NullProgressMonitor()));

		if (compUnit.isEmpty()) {
			logger.warning("Compilation unit AST was null: " + iCompilationUnit.getElementName());
		}

		compUnit.ifPresent(ast -> cache.put(iCompilationUnit, ast));

		return compUnit;
	}
}
