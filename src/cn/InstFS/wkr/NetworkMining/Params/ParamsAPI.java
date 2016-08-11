package cn.InstFS.wkr.NetworkMining.Params;

import cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams.AssociationRuleLineParams;
import cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams.AssociationRuleSimilarityParams;
import cn.InstFS.wkr.NetworkMining.Params.PcapParseParams.PcapParseParams;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.GlobalConfig;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

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
		return pcapParseParams;
	}

	public void setPcapParseParams(PcapParseParams pcapParseParams) {
		this.pcapParseParams = pcapParseParams;
	}

	public ParamsOM getPom() {
		return paramsOutlierMiner;
	}
	public void setPom(ParamsOM pom) {
		this.paramsOutlierMiner = pom;
	}
	
	public static void main(String[] args) {
		ParamsAssocitionRule ar = ParamsAPI.getInstance().getAssociationRuleParams();
		System.out.println(ar.toString());
		ParamsAssocitionRule ar2 = ParamsAPI.getInstance().getAssociationRuleParams();
	}
}
