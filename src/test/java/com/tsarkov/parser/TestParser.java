package com.tsarkov.parser;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;

public class TestParser {
    private String pathSourceFile = "/home/x7/IdeaProjects/ParserFile/src/main/resources/sourceFile.txt";

    @Test
    public void testCreateTreeNode() {
        Document document = new Document();
        document.parseFile(pathSourceFile);
        Assert.assertEquals(Document.threeNode.children.isEmpty(), false);
    }
}
