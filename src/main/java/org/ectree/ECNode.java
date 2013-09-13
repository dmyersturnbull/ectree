/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 * @author dmyersturnbull
 */
package org.ectree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.TreeSet;

public class ECNode implements Iterable<ECNode>, Comparable<ECNode> {

	private NavigableSet<ECNode> children;
	private final String description;

	private final ECNumber ecNumber;
	private ECNode parent;

	public ECNode(ECNumber ecNumber, String description) {
		this.ecNumber = ecNumber;
		this.description = description;
		children = new TreeSet<>();
	}

	protected boolean addChild(ECNode e) {
		return children.add(e);
	}

	public Iterator<ECNode> breadthFirst() {
		List<ECNode> visited = new ArrayList<>();
		Queue<ECNode> queue = new ArrayDeque<>();
		queue.add(this);
		while (!queue.isEmpty()) {
			ECNode current = queue.poll();
			visited.add(current);
			for (ECNode child : current.getChildren()) {
				queue.add(child);
			}
		}
		return visited.iterator();
	}

	@Override
	public int compareTo(ECNode second) {
		if (this.equals(second)) return 0;
		int min = Math.min(this.getDepth(), second.getDepth());
		for (int i = 1; i <= min; i++) {
			if (this.getEcNumber().getCodeAtDepth(i) < second.getEcNumber().getCodeAtDepth(i)) {
				return -1;
			} else if (this.getEcNumber().getCodeAtDepth(i) > second.getEcNumber().getCodeAtDepth(i)) {
				return 1;
			}
		}
		return -1;
	}

	public Iterator<ECNode> depthFirst() {
		LinkedHashSet<ECNode> visited = new LinkedHashSet<>();
		for (ECNode child : children) {
			Iterator<ECNode> i = child.depthFirst();
			while (i.hasNext())
				visited.add(i.next());
		}
		visited.add(this);
		return visited.iterator();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ECNode other = (ECNode) obj;
		if (ecNumber == null) {
			if (other.ecNumber != null) return false;
		} else if (!ecNumber.equals(other.ecNumber)) return false;
		return true;
	}

	public NavigableSet<ECNode> getChildren() {
		return children;
	}

	public int getDepth() {
		if (isRoot()) return 0;
		return ecNumber.getDepth();
	}

	public String getDescription() {
		return description;
	}

	public ECNumber getEcNumber() {
		return ecNumber;
	}

	public ECNode getParent() {
		return parent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ecNumber == null ? 0 : ecNumber.hashCode());
		return result;
	}

	public boolean isRoot() {
		return ecNumber == null;
	}

	@Override
	public Iterator<ECNode> iterator() {
		return listOrder();
	}

	public Iterator<ECNode> listOrder() {
		List<ECNode> visited = new ArrayList<>();
		visited.add(this);
		for (ECNode child : children) {
			Iterator<ECNode> i = child.listOrder();
			while (i.hasNext())
				visited.add(i.next());
		}
		return visited.iterator();
	}

	protected void setChildren(NavigableSet<ECNode> children) {
		this.children = children;
	}

	protected void setParent(ECNode parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return ecNumber + ": " + description;
	}

}
