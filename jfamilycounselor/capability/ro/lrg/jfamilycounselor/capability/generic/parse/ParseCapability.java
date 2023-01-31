package ro.lrg.jfamilycounselor.capability.generic.parse;

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
import ro.lrg.jfamilycounselor.util.cache.CacheService;
import ro.lrg.jfamilycounselor.util.cache.KeyManager;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class ParseCapability {
    private ParseCapability() {
    }

    private static final Cache<String, CompilationUnit> cache = CacheService.getCache(512);

    private static final Logger logger = jFCLogger.getJavaLogger();

    private static final ASTParser parser = ASTParser.newParser(AST.getJLSLatest());

    static {
	parser.setKind(ASTParser.K_COMPILATION_UNIT);
	parser.setResolveBindings(true);
    }

    public Optional<MethodDeclaration> parse(IMethod iMethod) {
	var visitor = new MethodDeclarationVisitor(iMethod);
	return parse(iMethod.getCompilationUnit())
		.flatMap(cuAST -> {
		    cuAST.accept(visitor);
		    return visitor.getLastNode();
		});
    }

    public Optional<CompilationUnit> parse(ICompilationUnit iCompilationUnit) {
	var key = KeyManager.compileationUnit(iCompilationUnit);

	if (cache.contains(key)) {
	    return cache.get(key);
	}

	parser.setSource(iCompilationUnit);
	parser.setProject(iCompilationUnit.getJavaProject());

	Optional<CompilationUnit> compUnit = Optional.ofNullable((CompilationUnit) parser.createAST(new NullProgressMonitor()));

	if (compUnit.isEmpty()) {
	    logger.warning("Compilation unit AST was null: " + iCompilationUnit.getElementName());
	}

	compUnit.ifPresent(ast -> cache.put(key, ast));

	return compUnit;
    }
}
