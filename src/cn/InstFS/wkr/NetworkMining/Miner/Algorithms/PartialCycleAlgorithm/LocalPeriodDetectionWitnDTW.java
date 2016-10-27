package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPartialCycle;

public class LocalPeriodDetectionWitnDTW {

	protected double[] seq;// 序列2的开始位置索引
	protected double[][] d;
	// protected int[] localp;//当前检测周期的周期预期值

	private MinerResultsPartialCycle result = null;
	private HashMap<Integer, ArrayList<NodeSection>> map = null;

	protected int[][] warpingPath;

	protected int K;
	protected int warpinglength;

	protected double pointThreshold;// 点的相似度阈值
	protected double seqThreshold;// 点的相似度阈值
	protected int limit;// 扭曲路径限制
	protected double simPointCount;// 统计相似点数
	protected int len;// 序列最大长度

	protected double warpingDistance;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double pointThreshold = 0.9;
		double seqthreshold = 0.9;
		/*
		 * float[] seq = {1f, 5f, 5f, 5f,4f,1f, 5f, 5f, 5f,4f,1f, 5f, 5f, 5f,4f,
		 * 5f, 5f, 5f,4f, 5f, 5f, 5f,4f, 5f, 5f, 5f,4f, 5f, 5f, 5f,4f, 5f, 5f,
		 * 5f,4f, 5f, 5f, 5f,4f, 5f, 5f, 5f,4f, 5f, 5f, 5f,4f, 5f, 5f, 5f,4f,
		 * 5f, 5f, 5f,4f, 5f, 5f, 5f,4f, 5f, 5f, 5f,4f};
		 */
		/*
		 * int[] seq = { 4,7,9,0,1, 2,35,67,89,12, 1, 5, 5, 5,4, 1,9, 5, 5, 5,4,
		 * 1, 5, 5, 5,4, 1, 5, 5, 5,4, 1, 5, 3, 5,5,
		 * 
		 * 4,7,9,0,1,2,35,67,89,12,4, };
		 */
		/*
		 * int[] seq = { 4,11,2,10,2,13,2,12 };
		 */

		DataItems data = new DataItems();
		// 赋值
		/*
		 * double[] da={1f, 5f, 5f, 5f,4f,1f, 5f, 5f, 5f,4f,1f, 5f, 5f, 5f,4f,
		 * 5f, 5f, 5f,4f, 5f, 5f, 5f,4f, 5f, 5f, 5f,4f, 5f, 5f, 5f,4f, 5f, 5f,
		 * 5f,4f, 5f, 5f, 5f,4f, 5f, 5f, 5f,4f, 5f, 5f, 5f,4f, 5f, 5f, 5f,4f,
		 * 5f, 5f, 5f,4f, 5f, 5f, 5f,4f, 5f, 5f, 5f,4f};
		 */
		double[] da = { 3, 4, 5, 6, 1, 2, 3, 4, 5, 5, 5, 5, 5, 5, 4, 4, 4, 3,
				2, 1, 2, 3, 4, 5, 5, 5, 5, 5, 4, 4, 4, 4, 1, 2, 3, 1, 7, 8, 9,
				0, 33, 44, 55, 6, 7, 66, 8, 2, 3, 4, 5, 6, 7, 1, 1, 1, 1, 1, 2,
				2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2 };
		
		
/*		double[] da = { 3, 4, 5, 6, 1, 2, 3, 4, 5, 5, 5, 5, 5, 5, 4, 4, 4, 3,
				2, 1, 2, 3, 4, 5, 5, 5, 5, 4, 4, 4, 4, 1, 2, 3, 1, 7, 8, 9, 0,
				33, 44, 55, 6, 7, 66, 8, 2, 3, 4, 5, 6, 7 };*/
		for (int i = 0; i < da.length; i++) {
			DataItem d = new DataItem();
			d.setData("" + da[i]);
			data.add1Data(d);
		}
		LocalPeriodDetectionWitnDTW dtw = new LocalPeriodDetectionWitnDTW(data,
				pointThreshold, seqthreshold, 5);

		/*
		 * dtw = new DTW(n1, n2,threshold); System.out.println(dtw);
		 * System.out.println("相似度："+dtw.getSimilarity());
		 */

		// DTW2 dtw = new DTW2(seq, pointThreshold, seqthreshold);
		/*
		 * System.out.println(dtw);
		 * System.out.println("相似度："+dtw.getSimilarity());
		 */

	}

	/**
	 * Constructor
	 * 
	 * @param data
	 *            待测时间序列
	 * @param pointThreshold
	 *            对应点相似度阈值
	 * @param seqThreshold
	 *            对应序列相似度阈值
	 * @param limit
	 *            限制区域大小，一般是limit=5
	 * @功能 阈值 判断两段序列的相似度阈值
	 * 
	 */
	public LocalPeriodDetectionWitnDTW(DataItems data, double pointThreshold,
			double seqThreshold, int limit) {
		result = new MinerResultsPartialCycle();
		this.limit = limit;
		int begin = 0;
		this.setData(data);
		map = new HashMap<Integer, ArrayList<NodeSection>>();
		// 初始化距离矩阵
		// d=new double[seq.length][seq.length];
		// System.out.println("开始计算矩阵");
		// this.initDistanceMatrix(seq);
		// System.out.println("结束");

		len = data.getLength();
		this.pointThreshold = pointThreshold;
		this.seqThreshold = seqThreshold;
		// int maxp=(len-begin)/2;
		System.out.println("开始检测。。。");
		this.compute(seq, begin, seq.length - 1);
		System.out.println("检测结束。。。");

	}

	/**
	 * 
	 * @param data
	 */
	public void setData(DataItems dataItems) {
		if (dataItems == null) {
			seq = new double[0];
			return;
		}
		seq = new double[dataItems.getLength()];
		List<String> data = dataItems.getData();
		Iterator it = data.iterator();
		int index = 0;
		while (it.hasNext()) {
			seq[index++] = Double.parseDouble((String) it.next());
			// System.out.println("seq["+(index-1)+"]"+seq[index-1]);
		}

	}

	public MinerResultsPartialCycle getResult() {
		//result = new MinerResultsPartialCycle();
		result.setPartialCyclePos(map);
		return result;

	}

	/**
	 * 初始化序列与自身每个点的距离矩阵
	 */
	private void initDistanceMatrix(double[] seq) {
		// TODO Auto-generated method stub
		// 计算 local distances:每个对应点的距离
		for (int i = 0; i < seq.length; i++) {
			for (int j = 0; j < seq.length; j++) {
				d[i][j] = distanceBetween(seq[i], seq[j]);
			}
		}

	}

	/**
	 * 
	 * @param seq
	 * @param seq1begin
	 * @param end
	 */
	public void compute(double[] seq, int seq1begin, int end) {
		if ((seq.length - seq1begin) < 4) {
			return;
		}

		int maxp = (end - seq1begin + 1) / 2;
		int p;
		for (p = maxp; p > 2; p--) {
			K = 1;
			warpinglength = 0;
			warpingDistance = 0.0;
			warpingPath = new int[p + p][2]; // max(n, m) <= K < n + m

			double accumulatedDistance = 0.0;

			// double[][] d = new double[p][p]; // local distances
			int seq2begin = seq1begin + p;

			if (p < (20 + limit) || ((limit * 2 + 2) < p)) {
				// //////
				double[][] D = new double[p][p]; // global distances
				d = new double[p][p];
				// 初始化d[][]
				for (int i = 0; i < p; i++) {
					for (int j = 0; j < p; j++) {
						d[i][j] = distanceBetween(seq[i + seq1begin], seq[j
								+ seq1begin + p]);
					}
				}
				// 初始化D[][]
				// 计算一遍 global distances全局距离：从左下到右上路径的距离--此处只是计算左边与下边的边缘距离
				D[0][0] = d[0][0];
				for (int i = 1; i < p; i++) {
					D[i][0] = d[i][0] + D[i - 1][0];// 右边
					D[0][i] = d[0][i] + D[0][i - 1];// 上面
				}

				for (int i = 1; i < p; i++) {
					for (int j = 1; j < p; j++) {
						D[i][j] = Math.min(
								Math.min(D[i - 1][j], D[i - 1][j - 1]),
								D[i][j - 1]) + d[i][j];
					}
				}

				// 寻找扭曲路径
				int i = p - 1;
				int j = p - 1;
				int minIndex = 1;

				warpingPath[K - 1][0] = i + seq1begin;
				warpingPath[K - 1][1] = j + seq1begin + p;
				warpinglength++;
				//
				while ((i + j) != 0) {
					if (i == 0) {
						j -= 1;
					} else if (j == 0) {
						i -= 1;
					} else { // i != 0 && j != 0
						double[] array = { D[i - 1][j], D[i][j - 1],
								D[i - 1][j - 1] };
						minIndex = this.getIndexOfMinimum(array);

						if (minIndex == 0) {
							i -= 1;
						} else if (minIndex == 1) {
							j -= 1;
						} else if (minIndex == 2) {
							i -= 1;
							j -= 1;
						}
					} // end else
					K++;
					warpingPath[K - 1][0] = i + seq1begin;
					warpingPath[K - 1][1] = j + seq1begin + p;
					warpinglength++;
				} // end while

			} else {
				double[][] D = new double[p][limit * 2 + 1]; // global distances
				d = new double[p][limit * 2 + 1];
				// 初始化d[][]
				// 先计算左边特殊的那一块d[][]
				for (int i = 0; i < (limit + 1); i++) {
					for (int j = 0; j < i + (limit + 1); j++) {
						d[i][j] = distanceBetween(seq[i + seq1begin], seq[j
								+ seq1begin + p]);
					}
				}
				// 中间常规的d[][]
				for (int i = limit + 1; i < p - (limit + 1); i++) {
					for (int j = i - limit; j < i + (limit + 1); j++) {
						d[i][j - i + limit] = distanceBetween(
								seq[i + seq1begin], seq[j + seq1begin + p]);
					}
				}
				// 右边的d[][]
				for (int i = p - (limit + 1); i < p; i++) {
					int m = 2 * limit;
					for (int j = p - 1; j > i - (limit + 1); j--) {
						d[i][m] = distanceBetween(seq[i + seq1begin], seq[j
								+ seq1begin + p]);
						m--;
					}

					for (int k = m; k >= 0; k--) {
						d[i][k] = Double.MAX_VALUE;
					}

				}

				// 初始化D[][]
				// 计算一遍 global distances全局距离：从左下到右上路径的距离--此处只是计算左边与下边的边缘距离
				D[0][0] = d[0][0];

				for (int i = 1; i < (limit + 1); i++) {
					D[i][0] = d[i][0] + D[i - 1][0];// 右边
					D[0][i] = d[0][i] + D[0][i - 1];// 上面
				}

				for (int i = 1; i < (limit + 1); i++) {
					for (int j = 1; j < i + limit; j++) {
						D[i][j] = Math.min(
								Math.min(D[i - 1][j], D[i - 1][j - 1]),
								D[i][j - 1]) + d[i][j];
					}
					D[i][i + limit] = Math.min(D[i - 1][i + (limit - 1)],
							D[i][i + (limit - 1)]) + d[i][i + limit];
					for (int j = i + (limit + 1); j < (limit * 2 + 1); j++) {
						D[i][j] = Double.MAX_VALUE;
					}
				}

				for (int i = (limit + 1); i <= p - (limit + 1); i++) {
					// D[i][i-3] = Math.min( D[i - 1][i-4],D[i-1][i-3]);
					D[i][0] = Math.min(D[i - 1][0], D[i - 1][1]) + d[i][0];
					for (int j = i - (limit - 1); j < i + limit; j++) {
						D[i][j - i + limit] = Math.min(
								Math.min(D[i - 1][j - i + limit], D[i - 1][j
										- i + (limit + 1)]), D[i][j - i
										+ (limit - 1)])
								+ d[i][j - i + limit];
					}
					D[i][limit * 2] = Math.min(D[i - 1][limit * 2],
							D[i][limit * 2 - 1]) + d[i][limit * 2];
				}
				for (int i = p - limit; i < p; i++) {
					// int m=6;
					for (int k = 0; k < (i - p + (limit + 1)); k++) {
						D[i][k] = Double.MAX_VALUE;
					}
					for (int m = (i - p + (limit + 1)); m < (limit * 2 + 1); m++) {
						D[i][m] = Math.min(
								Math.min(D[i - 1][m - 1], D[i - 1][m]),
								D[i][m - 1]) + d[i][m];
					}

				}

				// 寻找扭曲路径
				int i = p - 1;
				int j = (limit * 2);
				int minIndex = 1;

				warpingPath[K - 1][0] = i + seq1begin;
				warpingPath[K - 1][1] = j + (p - (limit * 2 + 1)) + seq1begin
						+ p;
				warpinglength++;
				//
				while (i > p - (limit + 1)) {
					if (j == 0) {
						i -= 1;
					} else { // i != 0 && j != 0
						double[] array = { D[i - 1][j], D[i][j - 1],
								D[i - 1][j - 1] };
						minIndex = this.getIndexOfMinimum(array);

						if (minIndex == 0) {
							i -= 1;
						} else if (minIndex == 1) {
							j -= 1;
						} else if (minIndex == 2) {
							i -= 1;
							j -= 1;
						}
					} // end else
					K++;
					warpingPath[K - 1][0] = i + seq1begin;
					warpingPath[K - 1][1] = j + (p - (limit * 2 + 1))
							+ seq1begin + p;
					warpinglength++;
				}

				while (i > limit) {
					if (j == limit * 2) {
						double[] array = { D[i - 1][j], D[i][j - 1] };
						minIndex = this.getIndexOfMinimum(array);

						if (minIndex == 0) {
							i -= 1;
						} else if (minIndex == 1) {
							j -= 1;
						}

					} else if (j == 0) {
						double[] array = { D[i - 1][j + 1], D[i - 1][j] };
						minIndex = this.getIndexOfMinimum(array);
						if (minIndex == 0) {
							i -= 1;
							j += 1;
						} else if (minIndex == 1) {
							i -= 1;
						}

					} else { // i != 0 && j != 0
						double[] array = { D[i - 1][j + 1], D[i][j - 1],
								D[i - 1][j] };
						minIndex = this.getIndexOfMinimum(array);

						if (minIndex == 0) {
							i -= 1;
							j += 1;
						} else if (minIndex == 1) {
							j -= 1;
						} else if (minIndex == 2) {
							i -= 1;
						}
					} // end else
					K++;
					warpingPath[K - 1][0] = i + seq1begin;
					warpingPath[K - 1][1] = i - limit + j + seq1begin + p;
					warpinglength++;
				} // end while

				while ((i + j) != 0) {
					if (i == 0) {
						j -= 1;
					} else if (j == 0) {
						i -= 1;
					} else { // i != 0 && j != 0
						double[] array = { D[i - 1][j], D[i][j - 1],
								D[i - 1][j - 1] };
						minIndex = this.getIndexOfMinimum(array);

						if (minIndex == 0) {
							i -= 1;
						} else if (minIndex == 1) {
							j -= 1;
						} else if (minIndex == 2) {
							i -= 1;
							j -= 1;
						}
					} // end else
					K++;
					warpingPath[K - 1][0] = i + seq1begin;
					warpingPath[K - 1][1] = j + seq1begin + p;
					warpinglength++;
				} // end while

			}

			// warpingDistance = accumulatedDistance / K;

			// 路径倒序
			// this.reversePath(warpingPath);
			// System.out.println("@@@@@@@");
			// System.out.println(this);

			// System.out.println("相似度：" + this.getSimilarity()+ "周期" +
			// p+"begin:" + seq1begin + " end:" + (seq1begin + p + p));

			if (this.getSimilarity() > this.seqThreshold) {
				/*
				 * System.out.println("**********************************周期" + p
				 * + "begin:" + begin + " end:" + end);
				 */

				System.out.println("********");
				// System.out.println(this);
				System.out.println("begin:" + seq1begin + " end:"
						+ (seq1begin + p + p));

				System.out.println("周期" + p + " 相似度：" + this.getSimilarity());
				System.out.print("seq1=");
				for (int i = seq1begin; i < p + seq1begin; i++) {
					System.out.print(seq[i] + " ");
				}
				System.out.println();
				System.out.print("seq2=");
				for (int i = seq1begin + p; i < seq1begin + 2 * p; i++) {
					System.out.print(seq[i] + " ");
				}

				System.out.println();

				NodeSection nodesection = new NodeSection(seq1begin, seq1begin
						+ 2 * p);
				if (map.containsKey(p)) {

					map.get(p).add(nodesection);
				} else {
					ArrayList<NodeSection> list = new ArrayList<NodeSection>();
					list.add(nodesection);
					map.put(p, list);
				}

				System.out.println(this);
				break;

				// this.compute(seq, begin,begin+p);
			}

		}
		if (p == 2) {
			seq1begin++;
			this.compute(seq, seq1begin, end);
		} else {
			// 在大周期里面继续寻找小周期
			/*
			 * end = seq1begin + p; this.compute(seq, seq1begin, end);
			 */

			// 在检测出来的周期序列外继续检测周期
			seq1begin = seq1begin + p * 2;
			end = seq.length - 1;
			this.compute(seq, seq1begin, end);

		}

	}

	/**
	 * Changes the order of the warping path (increasing order)
	 * 
	 * @param path
	 *            the warping path in reverse order
	 */
	protected void reversePath(int[][] path) {
		int[][] newPath = new int[K][2];
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < 2; j++) {
				newPath[i][j] = path[K - i - 1][j];
			}
		}
		warpingPath = newPath;
	}

	/**
	 * Returns the warping distance
	 * 
	 * @return
	 */
	public double getDistance() {
		return warpingDistance;
	}

	/**
	 * Computes a distance between two points
	 * 
	 * @param p1
	 *            the point 1
	 * @param p2
	 *            the point 2
	 * @return the distance between two points
	 */
	protected double distanceBetween(double p1, double p2) {
		return p1 > p2 ? (p1 - p2) : (p2 - p1);
	}

	/**
	 * Finds the index of the minimum element from the given array
	 * 
	 * @param array
	 *            the array containing numeric values
	 * @return the min value among elements
	 */
	protected int getIndexOfMinimum(double[] array) {
		int index = 0;
		double val = array[0];

		for (int i = 1; i < array.length; i++) {
			if (array[i] < val) {
				val = array[i];
				index = i;
			}
		}
		return index;
	}

	/**
	 * Returns a string that displays the warping distance and path
	 */
	public String toString() {
		String retVal = "Warping Distance: " + warpingDistance + "\n";
		retVal += "Warping Path: {";
		for (int i = 0; i < K; i++) {
			retVal += "(" + warpingPath[i][0] + ", " + warpingPath[i][1] + ")";
			retVal += (i == K - 1) ? "}" : ", ";

		}
		return retVal;
	}

	/**
	 * 返回两段时间序列的相似度
	 */
	public double getSimilarity() {
		if ((seq == null)) {
			return 0;
		}
		simPointCount = 0;
		for (int i = 0; i < warpinglength; i++) {

			double biger = seq[(warpingPath[i][0])] > seq[(warpingPath[i][1])] ? 1
					: 0;
			double a = biger == 1 ? seq[(warpingPath[i][0])]
					- seq[(warpingPath[i][1])] : seq[(warpingPath[i][1])]
					- seq[(warpingPath[i][0])];
			double pointSim = a
					/ (biger > 0 ? seq[(warpingPath[i][0])]
							: seq[(warpingPath[i][1])]);
			if ((1 - pointSim) > pointThreshold) {
				simPointCount++;
			}
		}
		return simPointCount / warpinglength;

	}

	/**
	 * Tests this class
	 * 
	 * @param args
	 *            ignored
	 */
	/*
	 * public static void main(String[] args) { float[] n2 = {1.5f, 3.9f, 4.1f,
	 * 3.3f}; float[] n1 = {2.1f, 2.45f, 3.673f, 4.32f, 2.05f, 1.93f, 5.67f,
	 * 6.01f}; DTW dtw = new DTW(n1, n2); System.out.println(dtw); }
	 */

}
