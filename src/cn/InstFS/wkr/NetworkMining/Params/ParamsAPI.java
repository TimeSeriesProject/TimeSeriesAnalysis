package cn.InstFS.wkr.NetworkMining.Params;

import cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams.AssociationRuleLineParams;
import cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams.AssociationRuleSimilarityParams;
import cn.InstFS.wkr.NetworkMining.Params.DistributeParams.ClientParams;
import cn.InstFS.wkr.NetworkMining.Params.DistributeParams.PcapParseDisParams;
import cn.InstFS.wkr.NetworkMining.Params.DistributeParams.ServerParams;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.*;
import cn.InstFS.wkr.NetworkMining.Params.PMParams.PMparam;
import cn.InstFS.wkr.NetworkMining.Params.PcapParseParams.PcapParseParams;
import cn.InstFS.wkr.NetworkMining.Params.PortParams.PortParams;
import cn.InstFS.wkr.NetworkMining.Params.PredictionAlgorithmParams.ARIMAParams;
import cn.InstFS.wkr.NetworkMining.Params.PredictionAlgorithmParams.NeuralNetworkParams;
import cn.InstFS.wkr.NetworkMining.Params.SMParams.SMParam;
import cn.InstFS.wkr.NetworkMining.Params.statistics.SeriesStatisticsParam;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.GlobalConfig;

import common.ErrorLogger;
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
	 * 分布式参数
	 * zsc
	 */
	private PcapParseDisParams pcapParseDisParams;
	private ServerParams serverParams;
	private ClientParams clientParams;
	private PortParams portParams;
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

	private Element getRootElement(String algoParams) {
		Element classElement = null;
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			File inputFile = new File("configs/algorithmsParams.xml");
			Document document = saxBuilder.build(inputFile);

			classElement = document.getRootElement().getChild(algoParams);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
			ErrorLogger.log("算法参数文件读取错误,或因文件不存在、文档结构不正确等原因");
			ErrorLogger.log("文件地址","configs/algorithmsParams.xml");
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

	public PcapParseDisParams getPcapParseDisParams() {
		if (pcapParseDisParams == null) {
			String pcapParseDisParamsPath = GlobalConfig.getInstance().getPcapParseDisParamPath();
			Element pcapParam = getRootElement(pcapParseDisParamsPath);
			pcapParseDisParams = new PcapParseDisParams(pcapParam);
		}
		return pcapParseDisParams;
	}

	public void setPcapParseDisParams(PcapParseDisParams pcapParseDisParams) {
		this.pcapParseDisParams = pcapParseDisParams;
	}

	public ServerParams getServerParams() {
		if (serverParams == null) {
			String serverParamsPath = GlobalConfig.getInstance().getServerParamPath();
			Element serverParam = getRootElement(serverParamsPath);
			serverParams = new ServerParams(serverParam);

		}
		return serverParams;
	}

	public void setServerParams(ServerParams serverParams) {
		this.serverParams = serverParams;
	}

	public ClientParams getClientParams() {
		if (clientParams == null) {
			String clientParamsPath = GlobalConfig.getInstance().getClientParamPath();
			Element clientParam = getRootElement(clientParamsPath);
			clientParams = new ClientParams(clientParam);
		}
		return clientParams;
	}

	public void setClientParams(ClientParams clientParams) {
		this.clientParams = clientParams;
	}

	public PortParams getPortParams() {
		if (portParams == null) {
			String portParamsPath = GlobalConfig.getInstance().getPortParamPath();
			Element portParam = getRootElement(portParamsPath);
			portParams = new PortParams(portParam);
		}
		return portParams;
	}

	public void setPortParams(PortParams portParams) {
		this.portParams = portParams;
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
			Element periodBasedParams = outlierParams.getChild("periodBasedParams");
			OMperiodBasedParams period = new OMperiodBasedParams(periodBasedParams);

			paramsOutlierMiner.setOmFastFourierParams(fastFourier);
			paramsOutlierMiner.setOmGuassianParams(gaussian);
			paramsOutlierMiner.setOmPiontPatternParams(pointPattern);
			paramsOutlierMiner.setOmteoParams(teo);
			paramsOutlierMiner.setOmMultidimensionalParams(multi);
			paramsOutlierMiner.setOmSaxPartternParams(sax);
			paramsOutlierMiner.setOMperiodBasedParams(period);
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

	public void resetAllParams() {
		paramsAssocitionRule = null;
        pcapParseParams = null;
		pcapParseDisParams = null;
		serverParams = null;
		clientParams = null;
		portParams = null;
		paramsOutlierMiner = null;

		paramsPeriodMiner = null; // 周期检测参数

		paramsPrediction = null;  // 预测参数

		paramsStatistic = null; // 统计参数
		paramsSequencePattern = null; // 频繁项参数
	}

	public static void main(String[] args) {
		ParamsAssocitionRule ar = ParamsAPI.getInstance().getAssociationRuleParams();
		System.out.println(ar.toString());
		ParamsAssocitionRule ar2 = ParamsAPI.getInstance().getAssociationRuleParams();
	}
}
