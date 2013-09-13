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

import java.util.Arrays;

public class ECNumber {

	private final int[] codes;

	public ECNumber(int[] codes) {
		this.codes = codes;
	}

	public ECNumber(String code) {
		String[] parts = code.split("\\.");
		codes = new int[parts.length];
		for (int i = 0; i < parts.length; i++) {
			try {
				codes[i] = Integer.parseInt(parts[i]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Bad EC number " + code, e);
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ECNumber other = (ECNumber) obj;
		if (!Arrays.equals(codes, other.codes)) return false;
		return true;
	}

	public int getCodeAtDepth(int depth) {
		return codes[depth - 1];
	}

	public int getDepth() {
		return codes.length;
	}

	public ECNumber getParentNumber() {
		int[] p = new int[codes.length - 1];
		for (int i = 0; i < codes.length - 1; i++)
			p[i] = codes[i];
		return new ECNumber(p);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(codes);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < codes.length; i++) {
			sb.append(codes[i]);
			if (i < codes.length - 1) sb.append(".");
		}
		return sb.toString();
	}

}
