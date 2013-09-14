ECTree: a tree API for EC numbers
======================================================

A simple Java API that provides a tree structure for Enzyme Commission (EC) numbers and the relationships between them.

Example uses
------------
```java
	// create the tree
	ECTree tree = ECTreeFactory.recentVersion();
	tree.print(); // print it
	
	// list node children
	ECNode node = tree.findByEcNumber("3.2");
	NavigableSet<ECNode> children = node.getChildren();
	
	// find nodes by description substring
	NavigableSet<ECNode> sulfurs = tree.findByDescriptionSubstring("sulfur");
	NavigableSet<ECNode> amines = tree.findByEcNumber("4").findByDescriptionSubstring("amine");
	
	// iterate over tree or subtree
	Iterator<ECNode> inOrder = tree.inOrder();
	Iterator<ECNode> bfs = tree.getRoot().breadthFirst();
	Iterator<ECNode> dfs = tree.getRoot().depthFirst();
	
	// find all classes of a given depth
	NavigableSet<ECNode> depth2 = tree.findNodesOfDepth(2);
```

License
-------
ECTree is licensed under the Apache Software License version 2.0
