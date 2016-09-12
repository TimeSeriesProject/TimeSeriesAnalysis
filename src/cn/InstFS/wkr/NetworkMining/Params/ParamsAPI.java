package cn.InstFS.wkr.NetworkMining.Params;

import cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams.AssociationRuleLineParams;
import cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams.AssociationRuleSimilarityParams;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.*;
import cn.InstFS.wkr.NetworkMining.Params.PMParams.PMparam;
import cn.InstFS.wkr.NetworkMining.Params.PcapParseParams.PcapParseParams;
import cn.InstFS.wkr.NetworkMining.Params.PredictionAlgorithmParams.ARIMAParams;
import cn.InstFS.wkr.NetworkMining.Params.PredictionAlgorithmParams.NeuralNetworkParams;
import cn.InstFS.wkr.NetworkMining.Params.SMParams.SMParam;
import cn.InstFS.wkr.NetworkMining.Params.statistics.SeriesStatisticsParam;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.GlobalConfig;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import weka.core.pmml.jaxbbindings.PMML;

import java.io.File;
import java.io.IOException;


public class ParamsAPI {
	private static class ParamsAPIHolder {
		private static final ParamsAPI INSTANCE = new ParamsAPI();
	}

	/**
	 * 关联规则相关参数
	 * author:艾长青
	 */
	ParamsAssocitionRule paramsAssocitionRule = null;
	/**
	 * pcap包解析参数
	 * 陈维
	 */
	private PcapParseParams pcapParseParams;
	/**
	 * 异常检测参数
	 * LYH
	 */
	ParamsOM paramsOutlierMiner = null;

	private ParamsPM paramsPeriodMiner = null; // 周期检测参数

	private ParamsPA paramsPrediction;	// 预测参数

	private ParamsStatistic paramsStatistic = null; // 统计参数

	private ParamsSM paramsSequencePattern = null; // 频繁项参数

	private ParamsAPI(){}

	public static final ParamsAPI getInstance() {
		return ParamsAPIHolder.INSTANCE;
	}

	private Element getRootElement(String filePath) {
		Element classElement = null;
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			File inputFile = new File(filePath);
			Document document = saxBuilder.build(inputFile);

			classElement = document.getRootElement();
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}

		return classElement;
	}

	public ParamsAssocitionRule getAssociationRuleParams() {
		if (paramsAssocitionRule == null){
			String arParamPath = GlobalConfig.getInstance().getArParamPath();
			Element arParam = getRootElement(arParamPath);
			paramsAssocitionRule = new ParamsAssocitionRule();

			Element arlpParam = arParam.getChild("associationRuleLineParams");
			AssociationRuleLineParams arlp = new AssociationRuleLineParams(arlpParam);
			Element arspParam = arParam.getChild("associationRuleSimilarityParams");
			AssociationRuleSimilarityParams arsp = new AssociationRuleSimilarityParams(arspParam);

			paramsAssocitionRule.setAssociationRuleLineParams(arlp);
			paramsAssocitionRule.setAssociationRuleSimilarityParams(arsp);
		}
		return paramsAssocitionRule;
	}

	public void setAssociationRuleParams(ParamsAssocitionRule par) {
		this.paramsAssocitionRule = par;
	}

	public PcapParseParams getPcapParseParams() {
		if (pcapParseParams == null) {
			String pcapParseParamsPath = GlobalConfig.getInstance().getPcapParseParamPath();
			Element pcapParam = getRootElement(pcapParseParamsPath);
			pcapParseParams = new PcapParseParams(pcapParam);
		}
		return pcapParseParams;
	}

	public void setPcapParseParams(PcapParseParams pcapParseParams) {
		this.pcapParseParams = pcapParseParams;
	}

	public ParamsOM getPom() {
		if (paramsOutlierMiner == null) {
			String outlierParamsPath = GlobalConfig.getInstance().getOmParamPath();
			Element outlierParams = getRootElement(outlierParamsPath);
			paramsOutlierMiner = new ParamsOM();

			Element fastFourierParams = outlierParams.getChild("fastFourierParams");
			OMFastFourierParams fastFourier = new OMFastFourierParams(fastFourierParams);
			Element gaussianParams = outlierParams.getChild("gaussianParams");
			OMGuassianParams gaussian = new OMGuassianParams(gaussianParams);
			Element pointPatternParams = outlierParams.getChild("pointPatternParams");
			OMPiontPatternParams pointPattern = new OMPiontPatternParams(pointPatternParams);
			Element TEOParams = outlierParams.getChild("TEOParams");
			OMTEOParams teo = new OMTEOParams(TEOParams);
			Element multiDimensionalParams = outlierParams.getChild("multidimensionalParams");
			OMMultidimensionalParams multi = new OMMultidimensionalParams(multiDimensionalParams);
			Element saxPatternParams = outlierParams.getChild("saxPatternParams");
			OMSAXPartternParams sax = new OMSAXPartternParams(saxPatternParams);

			paramsOutlierMiner.setOmFastFourierParams(fastFourier);
			paramsOutlierMiner.setOmGuassianParams(gaussian);
			paramsOutlierMiner.setOmPiontPatternParams(pointPattern);
			paramsOutlierMiner.setOmteoParams(teo);
			paramsOutlierMiner.setOmMultidimensionalParams(multi);
			paramsOutlierMiner.setOmSaxPartternParams(sax);
		}
		return paramsOutlierMiner;
	}
	public void setPom(ParamsOM pom) {
		this.paramsOutlierMiner = pom;
	}

	public ParamsPM getParamsPeriodMiner() {
		if (paramsPeriodMiner == null) {
			String pmParamPath = GlobalConfig.getInstance().getPmParamPath();
			Element pmParams = getRootElement(pmParamPath);
			paramsPeriodMiner = new ParamsPM();

			Element pmParamElement = pmParams.getChild("ERPandEntropyParams");
			PMparam pmParam = new PMparam(pmParamElement);

			paramsPeriodMiner.setPmparam(pmParam);
		}
		return paramsPeriodMiner;
	}

	public void setParamsPeriodMiner(ParamsPM paramsPeriodMiner) {
		this.paramsPeriodMiner = paramsPeriodMiner;
	}

	public ParamsPA getParamsPrediction() {
		if (paramsPrediction == null){
			String forecastParamPath = GlobalConfig.getInstance().getForecastParamPath();
			Element forecastParam = getRootElement(forecastParamPath);
			paramsPrediction = new ParamsPA();

			Element neuralNetworkParam = forecastParam.getChild("neuralNetworkParams");
			NeuralNetworkParams nnp = new NeuralNetworkParams(neuralNetworkParam);
			Element arimaParams = forecastParam.getChild("ARIMAParams");
			ARIMAParams ap = new ARIMAParams(arimaParams);

			paramsPrediction.setNnp(nnp);
			paramsPrediction.setAp(ap);
		}
		return paramsPrediction;
	}

	public void setParamsPrediction(ParamsPA paramsPrediction) {
		this.paramsPrediction = paramsPrediction;
	}

	public ParamsStatistic getParamsStatistic() {
		if (paramsStatistic == null) {
			String statisticsParamPath = GlobalConfig.getInstance().getSeriesStatisticParamPath();
			Element statisticsParam = getRootElement(statisticsParamPath);
			paramsStatistic = new ParamsStatistic();

			Element seriesStatisticsParam = statisticsParam.getChild("seriesStatisticsParam");
			SeriesStatisticsParam ssp = new SeriesStatisticsParam(seriesStatisticsParam);

			paramsStatistic.setSsp(ssp);
		}
		return paramsStatistic;
	}

	public void setParamsStatistic(ParamsStatistic paramsStatistic) {
		this.paramsStatistic = paramsStatistic;
	}

	public ParamsSM getParamsSequencePattern() {
		if (paramsSequencePattern == null) {
			String sequencePatternPath = GlobalConfig.getInstance().getSequencePatternParamPath();
			Element sequencePatternParams = getRootElement(sequencePatternPath);
			paramsSequencePattern = new ParamsSM();

			Element sequencePP = sequencePatternParams.getChild("smParam");
			SMParam smp = new SMParam(sequencePP);

			paramsSequencePattern.setSMparam(smp);
		}
		return paramsSequencePattern;
	}

	public void setParamsSequencePattern(ParamsSM paramsSequencePattern) {
		this.paramsSequencePattern = paramsSequencePattern;
	}

	public static void main(String[] args) {
		ParamsAssocitionRule ar = ParamsAPI.getInstance().getAssociationRuleParams();
		System.out.println(ar.toString());
		ParamsAssocitionRule ar2 = ParamsAPI.getInstance().getAssociationRuleParams();
	}
}
