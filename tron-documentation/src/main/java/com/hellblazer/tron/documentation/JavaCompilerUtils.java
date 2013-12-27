/*
 * Copyright 2012 Martin Skurla
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.hellblazer.tron.documentation;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;

/**
 * Java compiler utility class.
 * 
 * @author Martin Skurla (crazyjavahacking@gmail.com)
 */
public final class JavaCompilerUtils {

    //-----------------------------------------------------------------------------------------------------------------
    // Constructors.
    //-----------------------------------------------------------------------------------------------------------------
    private JavaCompilerUtils() {
    }

    //-----------------------------------------------------------------------------------------------------------------
    // Public Methods.
    //-----------------------------------------------------------------------------------------------------------------
    /**
     * Returns CompilationTask from given JavaFileObject.
     * 
     * @param javaFileObject
     *            JavaFileObject
     * 
     * @return CompilationTask
     */
    public static JavaCompiler.CompilationTask getCompilationTaskFromJavaFileObject(JavaFileObject javaFileObject) {
        List<JavaFileObject> javaSources = Arrays.asList(javaFileObject);

        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

        JavaCompiler.CompilationTask compilationTask = javaCompiler.getTask(new OutputStreamWriter(
                                                                                                   System.out),
                                                                            null, // default file manager
                                                                            null, // default diagnostic listener
                                                                            null, // no compiler options
                                                                            null, // no classes for annotation processing
                                                                            javaSources);

        return compilationTask;
    }

    /**
     * Compiles given CompilationTask.
     * 
     * @param compilationTask
     *            CompilationTask
     * @param parserPhaseListeners
     *            parser phase listeners
     * 
     * @return CompilationUnitTree
     * 
     * @throws JavacParsingException
     *             if problem during javac parsing
     */
    public static CompilationUnitTree compile(JavaCompiler.CompilationTask compilationTask) {

        JavacTask javacTask = (JavacTask) compilationTask;

        ASTRememberingTaskListener astRememberingTaskListener = new ASTRememberingTaskListener();

        javacTask.setTaskListener(astRememberingTaskListener);

        try {
            javacTask.analyze();
            return astRememberingTaskListener.compilationUnitTree;
        } catch (IOException e) {
            throw new JavacParsingException(e);
        }
    }

    /**
     * Compiles given content from URL.
     * 
     * @param javaSourceURL
     *            - java source URL
     * 
     * @return CompilationUnitTree
     * @throws URISyntaxException
     * 
     * @throws JavacParsingException
     *             if problem during javac parsing
     */
    public static CompilationUnitTree compile(URL javaSourceURL)
                                                                throws URISyntaxException {
        JavaFileObject javaFileObject = new UrlJavaFileObject(javaSourceURL);

        JavaCompiler.CompilationTask compilationTask = getCompilationTaskFromJavaFileObject(javaFileObject);

        return compile(compilationTask);
    }

    //-----------------------------------------------------------------------------------------------------------------
    // Inner classes.
    //-----------------------------------------------------------------------------------------------------------------
    /**
     * Represents exceptional state during javac parsing.
     */
    public static final class JavacParsingException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public JavacParsingException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Task listener for remembering AST - CompilationUnitTree.
     */
    private static final class ASTRememberingTaskListener implements
            TaskListener {
        private CompilationUnitTree compilationUnitTree = null;

        @Override
        public void started(TaskEvent taskEvent) {
            if (compilationUnitTree == null) {
                compilationUnitTree = taskEvent.getCompilationUnit();
            }
        }

        @Override
        public void finished(TaskEvent te) {
        }
    }
}