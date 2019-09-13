package bPlusTree;

/**
* Copyright 2018 NoGaBi
*/

import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class BPlusTree {

	private static Scanner sc;
	private static ArrayList<Tree> TrArray;

	public static class UnexpectedCommandException extends Exception {
		/**
		 * 
		 */

		public UnexpectedCommandException(String message) {
			super(message);
		}
	}

	private static class Command {
		public static final int CREATE = 2;
		public static final int INSERT = 3;
		public static final int DELETE = 4;
		public static final int SINGLE_KEY_SEARCH = 5;
		public static final int RANGED_SEARCH = 6;
		private int type;
		private String name;
		private String index_file;
		private ArrayList<String> args;

		public ArrayList<String> getArgs() {
			return args;
		}

		public String getName() {
			return name;
		}

		public String getIndex_file() {
			return index_file;
		}

		public int getType() {
			return type;
		}

		public Command(String cmdLine/* from cmd, to arguments */) throws UnexpectedCommandException {
			args = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(cmdLine, "-");
			int i;
			for (i = 0; st.hasMoreElements(); i++) {
				if (i == 0) {
					String token = st.nextToken();
					this.name = token.substring(0, token.length() - 1);
					System.out.println("Command obj's name has set as [" + this.name + "]");
				} else if (i == 1) {
					String temp = st.nextToken(" ");
					if (temp.equals("-c"))
						this.type = CREATE;
					else if (temp.equals("-i"))
						this.type = INSERT;
					else if (temp.equals("-d"))
						this.type = DELETE;
					else if (temp.equals("-s"))
						this.type = SINGLE_KEY_SEARCH;
					else if (temp.equals("-r"))
						this.type = RANGED_SEARCH;
					else
						throw new UnexpectedCommandException(
								"There are no commands like [" + temp + "]. check Entered Command Line.");
					System.out.println("you entered [" + temp + "] command");
				} else if (i == 2) {
					this.index_file = st.nextToken(" ");
					System.out.println("index file set as [" + this.index_file + "]");
				} else {
					String token = st.nextToken(" ");
					args.add(token);
					System.out.println("argument [" + token + "] added to Argument-ArrayList of Command obj");
				}
			}
			if (i < 2)
				throw new UnexpectedCommandException(
						"	Unexpected Command has entered! Enter 'exit' to end process,\n	or check your command line's arguments.");
		}
	}

	protected class Tree {
		/**
		 * 
		 */
		private Node header;
		private String name;
		private int max_size;
		private String index_file;

		public Tree(String name, int size) {
			this.name = name;
			this.header = null;
			this.max_size = size;
		}

		public Tree(String name) {
			this.name = name;
		}

		public Value_Node Search(int key) {
			if (this.header.typeOfNode == Node.LEAF_NODE)
				return (Value_Node) ((Leaf_Node) this.header).Search(key);
			else if (this.header.typeOfNode == Node.NON_LEAF_NODE)
				return (Value_Node) ((Non_Leaf_Node) this.header).Search(key);
			else
				return null;
		}

		public void insert(int key, int value) throws ExecUnavailableException {

			Value_Node vn = new Value_Node(key, value);

			if (this.header == null) {

				Leaf_Node ln = new Leaf_Node(this.max_size, this);
				vn.parent = ln;
				setHeader(ln);
				ln.p.add(vn);
				ln.key = vn.key;
				System.out.println("      insert in head Node :" + ln.key);
			} else {
				if (this.header.typeOfNode == Node.LEAF_NODE)
					((Leaf_Node) this.header).Insert(vn);
				else if (this.header.typeOfNode == Node.NON_LEAF_NODE) {
					Node temp = this.header;
					while (temp.typeOfNode == Node.NON_LEAF_NODE) {
						for (int i = 1; i < temp.p.size(); i += 2) {
							if (i == 1 && key < temp.p.get(i).key) {
								temp = temp.p.get(0);
								// System.out.println("+at 0");
								break;
							} else if (i == temp.p.size() - 2 && key >= temp.p.get(i).key) {
								temp = temp.p.get(temp.IndexOf(Node.BIGGEST_SEARCH));
								// System.out.println("+at last");
								break;
							} else if (key < temp.p.get(i).key) {
								temp = temp.p.get(i - 1);
								// System.out.println("+at "+ (i-1));
								break;
							}
						}
					}
					temp.Insert(vn);
				}
			}
		}

		public void delete(int key) throws ExecUnavailableException {
			if (header.Delete(new Value_Node(key, 0))) {

				// System.out.println(header.p.size());
				if (header != null) {
					while (header.typeOfNode == Node.NON_LEAF_NODE && header.p.size() == 1) {
						header = header.p.get(0);
						header.parent = null;
					}
					if (header.p.isEmpty())
						header = null;
				}
				System.out.println("Deletion of key [" + key + "] Complete!");
			} else {
				System.out.println("Deletion Failed!");
			}
		}

		public String getName() {
			return name;
		}

		public Node getHeader() {
			return header;
		}

		public int getMax_size() {
			return max_size;
		}

		public String getIndex_file() {
			return index_file;
		}

		public void setHeader(Node header) {
			this.header = header;
		}

		public void setIndex_file(String index_file) {
			this.index_file = index_file;
		}

		@Override
		public boolean equals(Object arg0) {
			return this.name.equals(((Tree) arg0).getName());
		}
	}

	public static class ExecUnavailableException extends Exception {

		/**
		 * 
		 */

		public ExecUnavailableException(String message) {
			super(message);
		}
	}

	private void execCommand(Command cmd) throws ExecUnavailableException {
		try {
			TrArray.clear();
			BufferedReader indexFile = new BufferedReader(new FileReader(cmd.getIndex_file()));
			String indexFileLine = indexFile.readLine();
			if (indexFileLine != null) {
				while (indexFileLine.charAt(0) == '!') {
					StringTokenizer indexSt = new StringTokenizer(indexFileLine.substring(1), ",");
					String TreeName = indexSt.nextToken();
					System.out.println("TreeName: " + TreeName);
					int sizeOfTree = Integer.parseInt(indexSt.nextToken());
					Tree trTemp = new Tree(TreeName, sizeOfTree);
					System.out.println("tree named [" + trTemp.getName() + "] created.");
					TrArray.add(trTemp);

					indexFileLine = indexFile.readLine();
					while (indexFileLine != null) {
						if (indexFileLine.charAt(0) == '!')
							break;
						System.out.println("readLine: " + indexFileLine);

						String args = "";
						StringTokenizer stk = new StringTokenizer(indexFileLine, ",");
						while (stk.hasMoreElements())
							args += " " + stk.nextToken();

						Command cmdTemp = new Command(TreeName + " -i " + cmd.getIndex_file() + args);
						// for(int i = 0; i < cmdTemp.getArgs().size(); i++)
						// System.out.println(cmdTemp.getArgs().get(i));
						trTemp.insert(Integer.parseInt(cmdTemp.getArgs().get(0)),
								Integer.parseInt(cmdTemp.getArgs().get(1)));
						System.out.println("========Insertion========\nInsertion of\nkey: " + cmdTemp.getArgs().get(0)
								+ "\nvalue: " + cmdTemp.getArgs().get(1) + "\nComplete.\n=========================");

						// Node tempOut = TrArray.get(TrArray.indexOf(new Tree(cmd.getName()))).header;
						// while (tempOut.typeOfNode == Node.NON_LEAF_NODE)
						// tempOut = tempOut.p.get(0);
						// tempOut = tempOut.p.get(0);
						// int keyTemp;
						// while (tempOut != null) {
						// if (tempOut.parent.parent != null)
						// System.out.print(tempOut.key + "[" + tempOut.parent.parent.key + "], ");
						// keyTemp = new Integer(tempOut.parent.key);
						// tempOut = tempOut.r;
						// if (tempOut != null && keyTemp != tempOut.parent.key)
						// System.out.print("|");
						// }
						// System.out.println();
						indexFileLine = indexFile.readLine();

					}
					if (indexFileLine == null)
						break;
				}
			}

			indexFile.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		if (cmd.getType() == Command.CREATE) {
			Tree tr = new Tree(cmd.getName(), Integer.parseInt(cmd.getArgs().get(0)));
			if (!TrArray.contains(tr))
				TrArray.add(tr);
			else
				System.out.println("Error: Tree named [" + tr.getName() + "] already Exists in Tree Array!");
		} else if (cmd.getType() == Command.INSERT) {
			try {
				int index;
				if ((index = TrArray.indexOf(new Tree(cmd.getName()))) >= 0) {

					cmd.getArgs().get(0);

					BufferedReader inputCsv = new BufferedReader(new FileReader(cmd.getArgs().get(0)));
					while (true) {

						String cmdLine = inputCsv.readLine();
						if (cmdLine == null)
							break;
						String args = "";
						StringTokenizer stk = new StringTokenizer(cmdLine, ",");
						while (stk.hasMoreElements())
							args += " " + stk.nextToken();

						Command cmdTemp = new Command(cmd.getName() + " -i " + cmd.getIndex_file() + args);
						// for(int i = 0; i < cmdTemp.getArgs().size(); i++)
						// System.out.println(cmdTemp.getArgs().get(i));
						TrArray.get(index).insert(Integer.parseInt(cmdTemp.getArgs().get(0)),
								Integer.parseInt(cmdTemp.getArgs().get(1)));
						System.out.println("========Insertion========\nInsertion of\nkey: " + cmdTemp.getArgs().get(0)
								+ "\nvalue: " + cmdTemp.getArgs().get(1) + "\nComplete.\n=========================");

						Node tempOut = TrArray.get(TrArray.indexOf(new Tree(cmd.getName()))).header;
						while (tempOut.typeOfNode == Node.NON_LEAF_NODE)
							tempOut = tempOut.p.get(0);
						tempOut = tempOut.p.get(0);
						int keyTemp;
						while (tempOut != null) {
							if (tempOut.parent.parent != null)
								System.out.print(tempOut.key + "[" + tempOut.parent.parent.key + "], ");
							keyTemp = new Integer(tempOut.parent.key);
							tempOut = tempOut.r;
							if (tempOut != null && keyTemp != tempOut.parent.key)
								System.out.print("|");
						}
						System.out.println();
					}

					inputCsv.close();

				} else
					System.out.println("========Insertion========\nTree name [" + cmd.getName()
							+ "] not Found!\n=========================");

			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} else if (cmd.getType() == Command.DELETE) {
			try {
				int index;
				if ((index = TrArray.indexOf(new Tree(cmd.getName()))) >= 0) {

					cmd.getArgs().get(0);

					BufferedReader inputCsv = new BufferedReader(new FileReader(cmd.getArgs().get(0)));
					while (true) {

						String cmdLine = inputCsv.readLine();
						if (cmdLine == null)
							break;
						String args = "";
						StringTokenizer stk = new StringTokenizer(cmdLine, ",");
						while (stk.hasMoreElements())
							args += " " + stk.nextToken();

						Command cmdTemp = new Command(cmd.getName() + " -d " + cmd.getIndex_file() + args);
						// for(int i = 0; i < cmdTemp.getArgs().size(); i++)
						// System.out.println(cmdTemp.getArgs().get(i));
						TrArray.get(index).delete(Integer.parseInt(cmdTemp.getArgs().get(0)));
						System.out.println("========Deletion=========\nDeletion of\nkey: " + cmdTemp.getArgs().get(0)
								+ "\nComplete.\n=========================");

						Node tempOut = TrArray.get(TrArray.indexOf(new Tree(cmd.getName()))).header;
						if (tempOut != null) {
							while (tempOut.typeOfNode == Node.NON_LEAF_NODE)
								tempOut = tempOut.p.get(0);
							tempOut = tempOut.p.get(0);
							int keyTemp;
							while (tempOut != null) {
								System.out.print(tempOut.key + "[" + tempOut.parent.key + "], ");
								keyTemp = new Integer(tempOut.parent.key);
								tempOut = tempOut.r;
								if (tempOut != null && keyTemp != tempOut.parent.key)
									System.out.print("|");
							}
							System.out.println();
						}else System.out.println("It is Empty!");
					}

					inputCsv.close();

				} else
					System.out.println("========Deletion=========\nTree name [" + cmd.getName()
							+ "] not Found!\n=========================");

			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} else if (cmd.getType() == Command.SINGLE_KEY_SEARCH) {
			try {
				int index;
				if ((index = TrArray.indexOf(new Tree(cmd.getName()))) >= 0) {
					Value_Node vn = (Value_Node) TrArray.get(index).header
							.Search(Integer.parseInt(cmd.getArgs().get(0)));
					if (vn.key == Integer.parseInt(cmd.getArgs().get(0))) {
						int count = 0;
						Node tempOut = vn.parent;
						for (; tempOut.parent != null; count++)
							tempOut = tempOut.parent;
						tempOut = vn;
						// System.out.println(count);
						for (int i = count; i >= 0; i--) {
							for (int k = 0; k <= i; k++) {
								tempOut = tempOut.parent;
							}
							if (tempOut.typeOfNode == Node.NON_LEAF_NODE) {
								System.out.print("Elements of Non-Leaf Node: ");
								for (int j = 1; j < tempOut.p.size(); j += 2) {
									if (j != 1)
										System.out.print(", ");
									System.out.print(tempOut.p.get(j).key);
								}
								System.out.println();
							} else if (tempOut.typeOfNode == Node.LEAF_NODE) {
								System.out.print("Elements of Leaf Node: ");
								for (int j = 0; j < tempOut.p.size(); j++) {
									if (j != 0)
										System.out.print(", ");
									System.out.print(tempOut.p.get(j).key);
								}
								System.out.println();
							}
							tempOut = vn;
						}
						System.out.println("Found Value: " + vn.value);
					} else
						System.out.println("key [" + Integer.parseInt(cmd.getArgs().get(0)) + "] Not Found");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} else if (cmd.getType() == Command.RANGED_SEARCH) {
			try {
				int index;
				if ((index = TrArray.indexOf(new Tree(cmd.getName()))) >= 0) {
					Value_Node vn = (Value_Node) TrArray.get(index).header
							.Search(Integer.parseInt(cmd.getArgs().get(0)));
					while (vn != null && vn.key <= Integer.parseInt(cmd.getArgs().get(1))) {
						System.out.println(vn.toString());
						vn = (Value_Node) vn.r;
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		BPlusTree bptree = new BPlusTree();
		sc = new Scanner(System.in);
		TrArray = new ArrayList<Tree>();
		Command cmd;
		String enteredCmd = sc.nextLine();
		System.out.println("--------Command Obj--------");

		try {
			cmd = new BPlusTree.Command(enteredCmd);
			System.out.println("---------------------------");

			FileOutputStream fileOut = new FileOutputStream(cmd.getIndex_file(), true);
			fileOut.close();

			while (!enteredCmd.equals("exit")) {
				bptree.execCommand(cmd);
				StringBuffer buf = new StringBuffer();
				String enter = String.format("%n");
				for (int i = 0; i < TrArray.size(); i++) {
					Tree temp = TrArray.get(i);
					if (i != 0)
						buf.append(enter);
					buf.append("!" + temp.getName() + "," + temp.getMax_size());
					// if (cmd.getType() != Command.CREATE) {
					Node nodeTemp = temp.header;
					if (nodeTemp != null) {
						while (nodeTemp.typeOfNode == Node.NON_LEAF_NODE)
							nodeTemp = nodeTemp.p.get(0);

						nodeTemp = nodeTemp.p.get(0);
						while (nodeTemp != null) {
							if(nodeTemp.active) buf.append(enter + nodeTemp.key + "," + ((Value_Node) nodeTemp).value);
							nodeTemp = nodeTemp.r;
						}
					}
					// System.out.println("Buf:\n" + buf.toString());
					// }
				}
				String output = buf.toString();
				// System.out.println("Wrote:\n" + output);
				fileOut = new FileOutputStream(cmd.getIndex_file());
				fileOut.write(output.getBytes("UTF-8"));
				fileOut.close();
				enteredCmd = sc.nextLine();
				System.out.println("--------Command Obj--------");

				try {
					cmd = new BPlusTree.Command(enteredCmd);
					System.out.println("---------------------------");
				} catch (UnexpectedCommandException e) {
					System.out.println(e.getMessage());
					System.out.println("---------------------------");
				}
			}
		} catch (

		Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println("---------------------------");
		}
	}
}
