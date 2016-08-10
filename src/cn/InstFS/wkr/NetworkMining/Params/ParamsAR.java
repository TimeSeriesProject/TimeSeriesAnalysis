package cn.InstFS.wkr.NetworkMining.Params;

import cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams.AssociationRuleLineParams;
import cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams.AssociationRuleSimilarityParams;
import cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams.AssocitionRuleRTreeParams;

public class ParamsAR {

	AssociationRuleLineParams arlp = null;
	AssociationRuleSimilarityParams arsp = null;
	AssocitionRuleRTreeParams artp = null;
	
	public AssociationRuleLineParams getAssociationRuleLineParams() {
		return arlp;
	}
	public void setAssociationRuleLineParams(AssociationRuleLineParams arlp) {
		this.arlp = arlp;
	}
	public AssociationRuleSimilarityParams getAssociationRuleSimilarityParams() {
		return arsp;
	}
	public void setAssociationRuleSimilarityParams(AssociationRuleSimilarityParams arsp) {
		this.arsp = arsp;
	}
	public AssocitionRuleRTreeParams getAssocitionRuleRTreeParams() {
		return artp;
	}
	public void setAssocitionRuleRTreeParams(AssocitionRuleRTreeParams artp) {
		this.artp = artp;
	}
	
}
