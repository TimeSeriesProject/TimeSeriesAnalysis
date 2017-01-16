package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialPeriodAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

/**
 * 
 * @author 顺
 * @功能：根据频繁项挖掘结果，计算频繁项间隔来找出部分周期 重要数据结构说明 Dij 指第i个点和第j个点之间的距离 部分周期信息保存在二叉树中
 *                              SearchTable 搜索表，记录Dij节点是否被使用
 *
 */
public class PartialPeriod {
	Map<String, ArrayList<Pair>> frequentmap;// 输入：频繁项挖掘结果
	double threshold=0.15;
	// DataItems data;// 原始序列

	Map<String, ArrayList<Pair>> positionResult;// 统计周期出现的位置，String为(频繁项+前两个数据点)
	Map<String, Double> periodResult;// 统计周期

	public PartialPeriod(Map<String, ArrayList<Pair>> frequentmap,double threshold) {
		this.frequentmap = frequentmap;
		this.threshold=threshold;
		periodResult = new HashMap<String, Double>();
		positionResult = new HashMap<String, ArrayList<Pair>>();
	}

	/**
	 * @功能：发现潜在的部分周期
	 */
	void run2() {
		
	}
	/**
	 * @功能：发现潜在的部分周期
	 */
	Map<String, ArrayList<Pair>> run() {

		for (Map.Entry<String, ArrayList<Pair>> entry : frequentmap.entrySet()) {
			int len = entry.getValue().size();
			Pair[] positionIndex = new Pair[len];// 每个元素记录位置下标
			byte[][] t = new byte[len][len];// 记录这个点是否已经是一个部分周期的一部分
			ArrayList<Pair> position = entry.getValue();
			/* 初始化位置矩阵 */
			for (int i = 0; i < len; i++) {
				positionIndex[i] = position.get(i);
			}
			/* 初始化搜索表 isUsed */
			// 使用过标志1，未被使用标志0
			for (int i = 0; i < len; i++) {
				for (int j = 0; j <= i; j++) {
					t[i][j] = 1;
				}
			}
			for (int i = 0; i < len - 2; i++) {
				for (int j = i + 1; j < len - 1; j++) {
					if (t[i][j] == 0) {
						ArrayList<Integer> list = new ArrayList<Integer>();// 记录部分周期临时结果
						list.add(i);
						list.add(j);

						double distance1 = positionIndex[j].getBegin()
								- positionIndex[i].getBegin();
						t[i][j] = 1;
						int m = j, n = m + 1;
						boolean hasParticalPeriod = false;
						double period = distance1;
						int count = 1;
						while (m != len - 1 && n != len) {
							if (t[m][n] == 0) {
								double distance2 = positionIndex[n].getBegin()
										- positionIndex[m].getBegin();
								// 比较两个间隔是否相近
								if (this.IsSimilar(period, distance2,threshold)) {
									System.out.println("m=" + m + "	n=" + n);
									t[m][n] = 1;
									period = (period + distance2) / 2;
									count++;
									if (count >= 3) {
										hasParticalPeriod = true;
									}
									// list.add(m);
									list.add(n);
									System.out.println("加入" + n);
									m = n;
									n++;
									// 如果一直都是相等的，全是部分周期
								} else {
									if (distance2 > 2 * period) {
										// 结束处理，没找到跳出while外处理
										break;
									} else {
										n++;
									}
									//
								}
							} else {
								n++;
							}
						}
						// 没找到部分周期进行一些处理：跟新搜索表，释放list
						if (!hasParticalPeriod) {
							Iterator<Integer> it = list.iterator();
							it.next();// 跳过第一个点i
							if (count == 1) {
								list.removeAll(list);// i,j不跟新为0,废弃
							} else {
								count = 1;
								it.next();// 跳过j
								int k = j;
								while (it.hasNext()) {
									int p = it.next();
									t[k][p] = 0;// 把出ij两点不能形成部分周期的点跟新为0
									k = p;
								}
							}
						} else {
							// 找到了部分周期，记录周期和周期位置，更新搜索表
							hasParticalPeriod=false;
							this.periodResult.put(entry.getKey() + ":" + i
									+ ":" + j, period);
							this.positionResult.put(
									entry.getKey() + ":" + i + ":" + j,
									exchangeIntegerListToPairList(
											positionIndex, list));
							// this.diaplayMap(periodPointRecord);
							System.out.println("模式 = " + entry.getKey() +" "+i+":"+"j"
									+ ", 周期 = " + period);
							

							// 跟新表1 
							for (int index1 = 0; index1 < list.size(); index1++) {
								for (int index2 = index1 + 1; index2 < list
										.size(); index2++) {
									t[list.get(index1)][list.get(index2)] = 1;
								}
							}
							list.removeAll(list);// 
							// 跟新表：搜索表每行只要有一个为1，其他全为1
/*							for (int index1 = 0; index1 < list.size(); index1++) {
								for (int index2 = 0; index2 < list
										.size(); index2++) {
									t[list.get(index1)][index2] = 1;
								}
							}*/
							// 打印记录
/*							for (Entry<String, Double> entry1 : periodResult
									.entrySet()) {
								
								System.out.println("模式 = " + entry1.getKey()
										+ ", 周期 = " + this.periodResult.get(entry1.getKey()));
								Iterator<Pair> it = this.positionResult.get(
										entry1.getKey()).iterator();
								System.out.println("起始位置： ");
								while (it.hasNext()) {
									System.out.println(it.next().getBegin()
											+ " ");
								}
							}*/
						}
					}
				}
			}
			// System.out.println();
		}
		return positionResult;
	}

	public void diaplayMap(Map<String, ArrayList<Pair>> map) {
		for (Map.Entry<String, ArrayList<Pair>> entry : map.entrySet()) {

			System.out.println("模式 = " + entry.getKey());
			Iterator it3 = entry.getValue().iterator();
			while (it3.hasNext()) {
				Pair p = (Pair) it3.next();

				System.out.print("--" + "(" + p.getBegin() + "," + p.getEnd()
						+ ")");
				// System.out.println();
			}
		}
	}

	private ArrayList<Pair> exchangeIntegerListToPairList(Pair[] positionIndex,
			ArrayList<Integer> list) {
		// TODO Auto-generated method stub
		if (positionIndex == null || list == null) {
			return null;
		} else {
			int len = positionIndex.length;
			ArrayList<Pair> newList = new ArrayList<Pair>();
			Iterator<Integer> it = list.iterator();
			while (it.hasNext()) {
				newList.add(positionIndex[it.next()]);
			}
			return newList;
		}

	}

	/**
	 * 比较两个数值是否近似
	 * 
	 * @param a
	 * @param b
	 * @return |a-b|/max(a-b) <0.1 返回true
	 */
	public boolean IsSimilar(double a, double b, double threshold) {
		double t = (double) (a > b ? (a - b) : (b - a));
		double t1 = a > b ? a : b;
		double t2 = t / t1;
		return t2 > threshold ? false : true;
	}
	
	public Map<String, ArrayList<Pair>> getPositionResult() {
		return positionResult;
	}

	public Map<String, Double> getPeriodResult() {
		return periodResult;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Map<String, ArrayList<Pair>> frequentmap = new HashMap<String, ArrayList<Pair>>();
		ArrayList<Pair> list = new ArrayList<Pair>();

		int[] a1 = { 358 ,
				856 ,
				1362 ,
				1894 ,
				2376  };
		for (int i = 0; i < a1.length; i++) {
			Pair p1 = new Pair(a1[i], 3);
			list.add(p1);
		}
		frequentmap.put("abc", list);
		PartialPeriod pp = new PartialPeriod(frequentmap,0.15);
		// pp.run();
		pp.diaplayMap(pp.run());
		System.out.println("*********");
	}
}
