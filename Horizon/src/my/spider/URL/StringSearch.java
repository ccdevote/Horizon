package my.spider.URL;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class StringSearch {

	private TreeNode _root;
	private Set<String> keywordsSet; // 关键字集合

	private final class TreeNode {
		private char _char; // 节点代表的字符
		private TreeNode _parent; // 该节点的父节点
		private TreeNode _failure; // 匹配失败后跳转的节点
		private ArrayList<String> _results; // 存储模式串的数组变量
		private TreeNode[] _transitionsAr;
		private Hashtable<Character, TreeNode> _transHash; // 存储孩子节点的哈希表

		public TreeNode(TreeNode parent, char c) {
			_char = c;
			_parent = parent;
			_results = new ArrayList<String>();
			_transitionsAr = new TreeNode[] {};
			_transHash = new Hashtable<Character, TreeNode>();
		}

		public void addResult(String result) // 将模式串中不在_results中的模式串添加进来
		{
			if (_results.contains(result))
				return;
			_results.add(result);
		}

		public void addTransition(TreeNode node) // 增加一个孩子节点
		{
			_transHash.put(node._char, node);
			TreeNode[] ar = new TreeNode[_transHash.size()];
			Iterator<TreeNode> it = _transHash.values().iterator();
			for (int i = 0; i < ar.length; i++) {
				if (it.hasNext()) {
					ar[i] = it.next();
				}
			}
			_transitionsAr = ar;
		}

		public TreeNode getTransition(char c) {
			return _transHash.get(c);
		}

		public boolean containsTransition(char c) {
			return getTransition(c) != null;
		}

		public char getChar() {
			return _char;
		}

		public TreeNode parent() {
			return _parent;
		}

		public TreeNode failure(TreeNode value) {
			_failure = value;
			return _failure;
		}

		public TreeNode[] transitions() {
			return _transitionsAr;
		}

		public ArrayList<String> results() {
			return _results;
		}
	}

	public StringSearch(Set<String> keywordsSet) // 构造方法，构建一个Trie树，由构建树的本身和增加失败匹配属性两步构成
	{
		this.keywordsSet = keywordsSet;
		buildTree(keywordsSet);
		addFailure();
	}

	void buildTree(Set<String> _keywords) // 构建一个树
	{
		_root = new TreeNode(null, ' ');
		Iterator<String> it = _keywords.iterator();
		while (it.hasNext()) {
			String p = it.next();
			TreeNode nd = _root;

			for (char c : p.toCharArray()) {
				TreeNode ndNew = null;
				for (TreeNode trans : nd.transitions()) {
					if (trans.getChar() == c) {
						ndNew = trans;
						break;
					}
				}

				if (ndNew == null) {
					ndNew = new TreeNode(nd, c);
					nd.addTransition(ndNew);
				}
				nd = ndNew;
			}
			nd.addResult(p);
		}
	}

	private void addFailure() // 增加失败匹配属性，类似KMP算法的回溯
	{
		ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();

		for (TreeNode nd : _root.transitions()) {
			nd.failure(_root);
			for (TreeNode trans : nd.transitions()) {
				nodes.add(trans);
			}
		}

		while (nodes.size() != 0) {
			ArrayList<TreeNode> newNodes = new ArrayList<TreeNode>();
			for (TreeNode nd : nodes) {
				TreeNode r = nd.parent()._failure;
				char c = nd.getChar();

				while (r != null && !r.containsTransition(c))
					r = r._failure;
				if (r == null)
					nd._failure = _root;
				else {
					nd._failure = r.getTransition(c);
					for (String result : nd._failure.results())
						nd.addResult(result);
				}

				for (TreeNode child : nd.transitions())
					newNodes.add(child);
			}
			nodes = newNodes;
		}
		_root._failure = _root;
	}

	public void addKeyWords(String keyword) // 增加关键字
	{
		keywordsSet.add(keyword);
		buildTree(keywordsSet);
		addFailure();
	}

	public void depthSearch(TreeNode node, StringBuilder ret, int deapth) // 深度遍历递归函数，用于打印出树的结构
	{
		if (node != null) {
			for (int i = 0; i < deapth; i++)
				ret.append("| ");
			ret.append("|-");
			ret.append(node._char + "\n");
			for (TreeNode child : node.transitions()) {
				int childDeapth = deapth + 1;
				depthSearch(child, ret, childDeapth);
			}
		}
	}

	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("打印树节点:\n");
		int deapth = 0;
		depthSearch(_root, ret, deapth);
		return ret.toString();
	}

	public StringSearchResult[] findAll(String text) // 从输入文本查找关键词集合
	{
		ArrayList<StringSearchResult> ret = new ArrayList<StringSearchResult>();
		TreeNode ptr = _root;
		int index = 0;

		while (index < text.length()) {
			TreeNode trans = null;
			while (trans == null) {
				ptr.getTransition('w');
				trans = ptr.getTransition(text.charAt(index));

				if (ptr == _root)
					break;
				if (trans == null) {
					ptr = ptr._failure;
				}
			}
			if (trans != null)
				ptr = trans;
			for (String found : ptr.results()) {
				ret.add(new StringSearchResult(index - found.length() + 1,
						found));
			}
			index++;
		}
		return ret.toArray(new StringSearchResult[ret.size()]);
	}
}

class StringSearchResult // 字符串检索结果类，由匹配上的关键字和关键字所在的位置组成
{
	private int location;
	private String matchedkeyword;

	public StringSearchResult(int location, String matchedkeyword) {
		this.location = location;
		this.matchedkeyword = matchedkeyword;
	}

	public int getLocation() {
		return location;
	}

	public String getMatchedkeyword() {
		return matchedkeyword;
	}

	public String toString() {
		return matchedkeyword + ":" + location;
	}
}
