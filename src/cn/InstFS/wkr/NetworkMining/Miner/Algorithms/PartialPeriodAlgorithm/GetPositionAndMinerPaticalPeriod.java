package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialPeriodAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.Miner.Common.LineElement;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPartialPeriod;

/**
 * 根据模式和线段集合，寻找频繁项起始点，开始部分周期挖掘
 * 
 * @author shun
 *
 */
public class GetPositionAndMinerPaticalPeriod {
	List<ArrayList<String>> patterns; //输入：线段模式
	List<LineElement> lineElements;//输入：所有初始序列线段化后的集合，每个LineElement包含label,begin,end
	Map<String, ArrayList<Pair>> fresult;
	Map<String, ArrayList<Pair>> positionResult;
	Map<String, Integer> periodResult;
	double threshold;//距离相似度阈值
	Map<String,Double> testError;
	boolean hasParticalPeriod;

	public GetPositionAndMinerPaticalPeriod(List<ArrayList<String>> patterns,
			List<LineElement> lineElements,double threshold) {
		fresult = new HashMap<String, ArrayList<Pair>>();
		positionResult = new HashMap<String, ArrayList<Pair>>();
		this.patterns = patterns;
		this.lineElements = lineElements;
		this.threshold=threshold;
		run();

	}

	/**
	 * @
	 */
	private void run() {
		// TODO Auto-generated method stub
		// 获取频繁项
		Iterator<ArrayList<String>> itPatterns = patterns.iterator();
		while (itPatterns.hasNext()) {
			ArrayList<String> onePattern = itPatterns.next();// 得到频繁项，例如abc
																// 的a-b-c链
			getPosition(onePattern, lineElements);
		}
		PartialPeriod particalPeriod = new PartialPeriod(fresult,threshold);

		particalPeriod.run();
		positionResult = particalPeriod.getPositionResult();
		periodResult=particalPeriod.getPeriodResult();
		hasParticalPeriod=particalPeriod.getHasParticalPeriod();
		testError=particalPeriod.GetTestError();
	}

	/**
	 * 寻找每个频繁项在原始线段化序列得位置，记录模式的起始点和终点
	 * @param onePattern [line1,confidence1,line2,confidence2] line1:开始的线段，line2结束的线段
	 *            123频繁项 1-2-3时，line1=1，line2=3
	 * @param lineElements
	 * @返回：fresult :
	 * 			fresult.put(patternName, position); patternName =line1+line2,position=list<Pair<line1begin,line2end>>,且忽略中间的点
	 */
	private void getPosition(ArrayList<String> onePattern,
			List<LineElement> lineElements) {
		// TODO Auto-generated method stub
		// String pattern=onePattern.toString();
		if (onePattern == null || lineElements == null) {
			return;
		}
		String patternName = "";//line1+line2
		ArrayList<Pair> position = new ArrayList<Pair>();// 存放位置
		int[] patterns = new int[onePattern.size()];
		Iterator<String> it1 = onePattern.iterator();// itOnePattern
		Iterator<LineElement> it2 = lineElements.iterator();// itLineElements
		//
		int i = 0;
		while (it1.hasNext()) {
			int a = Integer.parseInt(it1.next().split(",")[0]);
			patterns[i++] = a;
			patternName += a;
		}

		it1 = onePattern.iterator();// itOnePattern
		int len = patterns.length;
		int k = 0;
		int begin = 0;// 记录位置
		while (it2.hasNext()) {

			LineElement line = it2.next();

			if (patterns[k] == line.getLabel()) {

				if (k > 0 && k < len - 1) {
					k++;			
				} else if (k == len - 1) {
					position.add(new Pair(begin, line.getEnd()));
					k = 0;
				} else if (k == 0) {//记录起点的start坐标
					begin = line.getStart();
					k++;
				}
			} else {
				k = 0;
			}

		}
		// 3个以上才有周期
		if (position.size() > 3) {
			fresult.put(patternName, position);
		}

	}

	public MinerResultsPartialPeriod getResult() {
		// TODO Auto-generated method stub
		MinerResultsPartialPeriod minerResultsPartialPeriod = new MinerResultsPartialPeriod();
		minerResultsPartialPeriod.setPositionResult(positionResult);
		minerResultsPartialPeriod.setPeriodResult(periodResult);
		minerResultsPartialPeriod.setHasPartialPeriod(hasParticalPeriod);
		return minerResultsPartialPeriod;
	}
	
	public Map<String,Double> getTestErrorResult() {
		return testError;
		
	}
}
