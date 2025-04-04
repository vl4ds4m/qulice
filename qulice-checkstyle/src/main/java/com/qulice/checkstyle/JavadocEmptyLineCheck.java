/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Check for empty line at the beginning and at the end of Javadoc.
 *
 * <p>You can't have empty line at the beginning or at the end of Javadoc.
 *
 * <p>The following red lines in class Javadoc will be reported as violations.
 * <pre>
 * &#47;**
 *  <span style="color:red" >*</span>
 *  * This is my class.
 *  <span style="color:red" >*</span>
 *  *&#47;
 * public final class Foo {
 *     // ...
 * </pre>
 *
 * @since 0.17
 */
public final class JavadocEmptyLineCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.PACKAGE_DEF,
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ANNOTATION_DEF,
            TokenTypes.ANNOTATION_FIELD_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ENUM_CONSTANT_DEF,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.CTOR_DEF,
            TokenTypes.METHOD_DEF,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public void visitToken(final DetailAST ast) {
        final String[] lines = this.getLines();
        final int current = ast.getLineNo();
        final int start =
            JavadocEmptyLineCheck.findCommentStart(lines, current) + 1;
        if (JavadocEmptyLineCheck.isNodeHavingJavadoc(ast, start)) {
            if (JavadocEmptyLineCheck.isJavadocLineEmpty(lines[start])) {
                this.log(start + 1, "Empty Javadoc line at the beginning");
            }
            final int end =
                JavadocEmptyLineCheck.findCommentEnd(lines, current) - 1;
            if (JavadocEmptyLineCheck.isJavadocLineEmpty(lines[end])) {
                this.log(end + 1, "Empty Javadoc line at the end");
            }
        }
    }

    /**
     * Check if Javadoc line is empty.
     * @param line Javadoc line
     * @return True when Javadoc line is empty
     */
    private static boolean isJavadocLineEmpty(final String line) {
        return "*".equals(line.trim());
    }

    /**
     * Check if node has Javadoc.
     * @param node Node to be checked for Javadoc.
     * @param start Line number where comment starts.
     * @return True when node has Javadoc
     */
    private static boolean isNodeHavingJavadoc(final DetailAST node,
        final int start) {
        return start > getLineNoOfPreviousNode(node);
    }

    /**
     * Returns line number of previous node.
     * @param node Current node.
     * @return Line number of previous node
     */
    private static int getLineNoOfPreviousNode(final DetailAST node) {
        int start = 0;
        final DetailAST previous = node.getPreviousSibling();
        if (previous != null) {
            start = previous.getLineNo();
        }
        return start;
    }

    /**
     * Find Javadoc starting comment.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @return Line number with found starting comment or -1 otherwise.
     */
    private static int findCommentStart(final String[] lines, final int start) {
        return JavadocEmptyLineCheck.findTrimmedTextUp(lines, start, "/**");
    }

    /**
     * Find Javadoc ending comment.
     * @param lines Array of lines to check.
     * @param start Start searching from this line number.
     * @return Line number with found ending comment, or -1 if it wasn't found.
     */
    private static int findCommentEnd(final String[] lines, final int start) {
        return JavadocEmptyLineCheck.findTrimmedTextUp(lines, start, "*/");
    }

    /**
     * Find a text in lines, by going up.
     * @param lines Array of lines to check.
     * @param start Start searching from this line number.
     * @param text Text to find.
     * @return Line number with found text, or -1 if it wasn't found.
     */
    private static int findTrimmedTextUp(final String[] lines,
        final int start, final String text) {
        int found = -1;
        for (int pos = start - 1; pos >= 0; pos -= 1) {
            if (lines[pos].trim().equals(text)) {
                found = pos;
                break;
            }
        }
        return found;
    }
}
